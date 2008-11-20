package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.HistoryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.HistoryDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HistoryEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.CalendarUtils;

public class HistoryBusinessTest extends TestCase {

    private BacklogDAO backlogDAO;
    private HistoryDAO historyDAO;
    private HistoryBusinessImpl hisBusiness = new HistoryBusinessImpl();
    
    public void testScoping_addFirstBacklogItemWithoutEstimate() {
        backlogDAO = createMock(BacklogDAO.class);
        hisBusiness.setBacklogDAO(backlogDAO);
        
        // Declare test values
        final int bliId = 10;
        final int backlogId = 100;
        
        // Create new non-estimated backlog item and set test values to it
        BacklogItem bli = new BacklogItem();
        bli.setId(bliId);
        bli.setEffortLeft(new AFTime(0));
        bli.setOriginalEstimate(new AFTime(0));

        // Create backlog for backlog item
        Backlog backlog = new Iteration();
        backlog.setId(backlogId);
        bli.setBacklog(backlog);
        Collection<BacklogItem> items = new ArrayList<BacklogItem>();
        items.add(bli);
        backlog.setBacklogItems(items);
        
        // Record expected behavior       
        expect(backlogDAO.get(backlogId)).andReturn(backlog);       
        replay(backlogDAO);       
               
        // run method under test
        hisBusiness.updateBacklogHistory(backlogId);
        
        // Should only today's entry with EL,OE,D = 0,0,0
        BacklogHistory history = backlog.getBacklogHistory();;        
        assertNotNull(history);
        assertEquals(1, history.getEffortHistoryEntries().size());
        
        HistoryEntry<BacklogHistory> entry = history.getCurrentEntry();        
        assertNotNull(entry);
        assertEquals(0, entry.getEffortLeft().getTime());
        assertEquals(0, entry.getOriginalEstimate().getTime());
        assertEquals(0, entry.getDeltaEffortLeft().getTime());
              
        // verify behavior
        verify(backlogDAO);
    }
    
    public void testScoping_addFirstBacklogItemWithEstimate() {
        backlogDAO = createMock(BacklogDAO.class);
        hisBusiness.setBacklogDAO(backlogDAO);
        
        // Declare test values
        final int bliId = 10;
        final int backlogId = 100;
        final int hour = 5;
        
        // Create new estimated backlog item and set test values to it
        BacklogItem bli = new BacklogItem();
        bli.setId(bliId);
        bli.setEffortLeft(new AFTime(hour));
        bli.setOriginalEstimate(new AFTime(hour));

        // Create backlog for backlog item
        Backlog backlog = new Iteration();
        backlog.setId(backlogId);
        bli.setBacklog(backlog);
        Collection<BacklogItem> items = new ArrayList<BacklogItem>();
        items.add(bli);
        backlog.setBacklogItems(items);
        
        // Record expected behavior       
        expect(backlogDAO.get(backlogId)).andReturn(backlog);       
        replay(backlogDAO);       
               
        // run method under test
        hisBusiness.updateBacklogHistory(backlogId);
        
        /* Should have today's entry with EL,OE,D = 5,5,0
         * and yesterday's entry with 0,0,5.
         * 
         */ 
        BacklogHistory history = backlog.getBacklogHistory();;        
        assertNotNull(history);
        assertEquals(2, history.getEffortHistoryEntries().size());
        
        HistoryEntry<BacklogHistory> entry = history.getCurrentEntry();
        HistoryEntry<BacklogHistory> yesterdayEntry = history.getLastToCurrentEntry();
        assertNotNull(entry);
        assertEquals(hour, entry.getEffortLeft().getTime());
        assertEquals(hour, entry.getOriginalEstimate().getTime());
        assertEquals(0, entry.getDeltaEffortLeft().getTime());
        
        Calendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DATE, -1);
        Calendar testYesterday = new GregorianCalendar();
        testYesterday.setTime(yesterdayEntry.getDate());
        
        // Test that the earlier entry really was yesterday.
        boolean wasYesterday = yesterday.get(GregorianCalendar.YEAR) == testYesterday.get(GregorianCalendar.YEAR)
        && yesterday.get(GregorianCalendar.MONTH) == testYesterday.get(GregorianCalendar.MONTH)
        && yesterday.get(GregorianCalendar.DAY_OF_MONTH) == testYesterday.get(GregorianCalendar.DAY_OF_MONTH);
        
        assertNotNull(yesterdayEntry);
        assertTrue(wasYesterday);
        assertEquals(0, yesterdayEntry.getEffortLeft().getTime());
        assertEquals(0, yesterdayEntry.getOriginalEstimate().getTime());
        assertEquals(hour, yesterdayEntry.getDeltaEffortLeft().getTime());
        
        // verify behavior
        verify(backlogDAO);
                                          
    }
    
    public void testScoping_multipleBacklogItems() {
        backlogDAO = createMock(BacklogDAO.class);
        hisBusiness.setBacklogDAO(backlogDAO);
        
        // Declare test values
        final int bliId1 = 10;
        final int bliId2 = 11;
        final int bliId3 = 12;
        final int backlogId = 100;
        final int hour1 = 12;
        final int hour2 = 8;
        final int hour1b = 9;
        final int hour3 = 5;
        
        // Create new backlog items and set test values to them.
        BacklogItem bli1 = new BacklogItem();
        bli1.setId(bliId1);
        bli1.setEffortLeft(new AFTime(hour1));
        bli1.setOriginalEstimate(new AFTime(hour1));
        BacklogItem bli2 = new BacklogItem();
        bli2.setId(bliId2);
        bli2.setEffortLeft(new AFTime(hour2));
        bli2.setOriginalEstimate(new AFTime(hour2));
        BacklogItem bli3 = new BacklogItem();
        bli3.setId(bliId3);
        // Create backlog for backlog items
        Backlog backlog = new Iteration();
        backlog.setId(backlogId);
        bli1.setBacklog(backlog);
        bli2.setBacklog(backlog);
        bli3.setBacklog(backlog);
        Collection<BacklogItem> items = new ArrayList<BacklogItem>();
        items.add(bli1);
        items.add(bli2);
        items.add(bli3);
        backlog.setBacklogItems(items);
        
        // Record expected behavior       
        expect(backlogDAO.get(backlogId)).andReturn(backlog);
        expect(backlogDAO.get(backlogId)).andReturn(backlog);
        expect(backlogDAO.get(backlogId)).andReturn(backlog);
        expect(backlogDAO.get(backlogId)).andReturn(backlog);
        replay(backlogDAO);       
               
        // run method under test
        hisBusiness.updateBacklogHistory(backlogId);
        
        BacklogHistory history = backlog.getBacklogHistory();        
        assertNotNull(history);
        assertEquals(2, history.getEffortHistoryEntries().size());
        HistoryEntry<BacklogHistory> entry = history.getCurrentEntry();
        HistoryEntry<BacklogHistory> yesterdayEntry = history.getLastToCurrentEntry();
        assertNotNull(entry);
        assertEquals(hour1 + hour2, entry.getEffortLeft().getTime());
        assertEquals(hour1 + hour2, entry.getOriginalEstimate().getTime());
        assertEquals(0, entry.getDeltaEffortLeft().getTime());
        assertNotNull(yesterdayEntry);
        assertEquals(0, yesterdayEntry.getEffortLeft().getTime());
        assertEquals(0, yesterdayEntry.getOriginalEstimate().getTime());
        assertEquals(hour1 + hour2, yesterdayEntry.getDeltaEffortLeft().getTime());     
              
        // Change item's estimate -- should not change delta.
        bli1.setEffortLeft(new AFTime(hour1b));
        
        // Run test
        hisBusiness.updateBacklogHistory(backlogId);
        
        entry = history.getCurrentEntry();
        yesterdayEntry = history.getLastToCurrentEntry();
        assertEquals(hour1b + hour2, entry.getEffortLeft().getTime());
        assertEquals(hour1 + hour2, entry.getOriginalEstimate().getTime());
        assertEquals(0, entry.getDeltaEffortLeft().getTime());
        assertEquals(0, yesterdayEntry.getEffortLeft().getTime());
        assertEquals(0, yesterdayEntry.getOriginalEstimate().getTime());
        assertEquals(hour1 + hour2, yesterdayEntry.getDeltaEffortLeft().getTime());
        
        // Change non-estimated to estimated
        bli3.setOriginalEstimate(new AFTime(hour3));
        bli3.setEffortLeft(new AFTime(hour3));
        
        hisBusiness.updateBacklogHistory(backlogId);
        
        entry = history.getCurrentEntry();
        yesterdayEntry = history.getLastToCurrentEntry();
        assertEquals(hour1b + hour2 + hour3, entry.getEffortLeft().getTime());
        assertEquals(hour1 + hour2 + hour3, entry.getOriginalEstimate().getTime());
        assertEquals(0, entry.getDeltaEffortLeft().getTime());
        assertEquals(0, yesterdayEntry.getEffortLeft().getTime());
        assertEquals(0, yesterdayEntry.getOriginalEstimate().getTime());
        assertEquals(hour1 + hour2 + hour3, yesterdayEntry.getDeltaEffortLeft().getTime());
        
        // Remove estimated item
        backlog.getBacklogItems().remove(bli3);
        
        hisBusiness.updateBacklogHistory(backlogId);
        
        entry = history.getCurrentEntry();
        yesterdayEntry = history.getLastToCurrentEntry();
        assertEquals(hour1b + hour2, entry.getEffortLeft().getTime());
        assertEquals(hour1 + hour2, entry.getOriginalEstimate().getTime());
        assertEquals(0, entry.getDeltaEffortLeft().getTime());
        assertEquals(0, yesterdayEntry.getEffortLeft().getTime());
        assertEquals(0, yesterdayEntry.getOriginalEstimate().getTime());
        assertEquals(hour1 + hour2, yesterdayEntry.getDeltaEffortLeft().getTime());
        
        // verify behavior
        verify(backlogDAO);
    }
    
    public void testOldEntries() {
        backlogDAO = createMock(BacklogDAO.class);
        hisBusiness.setBacklogDAO(backlogDAO);
              
        // Declare test values
        final int bliId1 = 10;
        // final int bliId2 = 11;
        final int backlogId = 100;
        final int effLeft = 12;
        final int origEst = 17;
        final int delta = -7;
        final int hour1 = 15;
        // final int hour2 = 8;
     
        // Create new backlog items and set test values to them.
        BacklogItem bli1 = new BacklogItem();
        bli1.setId(bliId1);
        bli1.setEffortLeft(new AFTime(hour1));
        bli1.setOriginalEstimate(new AFTime(hour1));
        /*
        BacklogItem bli2 = new BacklogItem();
        bli2.setId(bliId2);
        bli2.setEffortLeft(new AFTime(hour2));
        bli2.setOriginalEstimate(new AFTime(hour2));
         */
     
        // Create backlog for backlog items
        Backlog backlog = new Iteration();
        backlog.setId(backlogId);
        bli1.setBacklog(backlog);
        //bli2.setBacklog(backlog);
        Collection<BacklogItem> items = new ArrayList<BacklogItem>();
        items.add(bli1);
        //items.add(bli2);
        backlog.setBacklogItems(items);
        
        // Create backlog history
        BacklogHistory history = new BacklogHistory();
        history.setEffortHistoryEntries( new LinkedList<HistoryEntry<BacklogHistory>>());
        backlog.setBacklogHistory(history);
        
        // Create an old entry 5 days ago.
        Calendar fiveDaysAgo = new GregorianCalendar();
        fiveDaysAgo.add(Calendar.DATE, -5);
        HistoryEntry<BacklogHistory> fiveDaysAgoEntry = new HistoryEntry<BacklogHistory>();
        fiveDaysAgoEntry.setDate(new java.sql.Date(fiveDaysAgo.getTime().getTime()));
        fiveDaysAgoEntry.setHistory(history);
        fiveDaysAgoEntry.setEffortLeft(new AFTime(effLeft));
        fiveDaysAgoEntry.setOriginalEstimate(new AFTime(origEst));
        fiveDaysAgoEntry.setDeltaEffortLeft(new AFTime(delta));
        history.getEffortHistoryEntries().add(fiveDaysAgoEntry);
        
        // Record expected behavior       
        expect(backlogDAO.get(backlogId)).andReturn(backlog);
        replay(backlogDAO);      
        
        // run method under test
        hisBusiness.updateBacklogHistory(backlogId);
        
        //history = backlog.getBacklogHistory();        
        assertNotNull(history);
        assertEquals(3, history.getEffortHistoryEntries().size());
        // Since the list is sorted by date by sql, the first entry must be placed last by hand...
        HistoryEntry<BacklogHistory> oldEntry = history.getEffortHistoryEntries().get(0);
        history.getEffortHistoryEntries().remove(0);
        history.getEffortHistoryEntries().add(oldEntry);
        HistoryEntry<BacklogHistory> entry = history.getCurrentEntry();
        HistoryEntry<BacklogHistory> yesterdayEntry = history.getLastToCurrentEntry();
        assertNotNull(entry);
        assertEquals(hour1, entry.getEffortLeft().getTime());
        assertEquals(hour1, entry.getOriginalEstimate().getTime());
        assertEquals(0, entry.getDeltaEffortLeft().getTime());
                      
        Calendar yesterday = new GregorianCalendar();
        yesterday.add(Calendar.DATE, -1);
        Calendar testYesterday = new GregorianCalendar();
        testYesterday.setTime(yesterdayEntry.getDate());
        
        // Test that the earlier entry really was yesterday.
        boolean wasYesterday = yesterday.get(GregorianCalendar.YEAR) == testYesterday.get(GregorianCalendar.YEAR)
        && yesterday.get(GregorianCalendar.MONTH) == testYesterday.get(GregorianCalendar.MONTH)
        && yesterday.get(GregorianCalendar.DAY_OF_MONTH) == testYesterday.get(GregorianCalendar.DAY_OF_MONTH);
        
        // Test that yesterday's entry really copies EL and OE from the older entry.
        assertNotNull(yesterdayEntry);
        assertTrue(wasYesterday);
        assertEquals(effLeft, yesterdayEntry.getEffortLeft().getTime());
        assertEquals(origEst, yesterdayEntry.getOriginalEstimate().getTime());
        assertEquals(hour1 - effLeft, yesterdayEntry.getDeltaEffortLeft().getTime());
        
        // verify behavior
        verify(backlogDAO);
        
    }
    
    
    public void testCalculateDailyVelocity() {
        backlogDAO = createMock(BacklogDAO.class);
        historyDAO = createMock(HistoryDAO.class);
        hisBusiness.setBacklogDAO(backlogDAO);
        hisBusiness.setHistoryDAO(historyDAO);
        
        /* Generate the time */
        Calendar cal = GregorianCalendar.getInstance();
        CalendarUtils.setHoursMinutesAndSeconds(cal, 8, 0, 0);
        
        /* Generate the test data */
        Iteration iter = new Iteration();
        iter.setId(3);
        cal.add(Calendar.DATE, -3);
        iter.setStartDate(cal.getTime());
        cal.add(Calendar.DATE, 8);
        CalendarUtils.setHoursMinutesAndSeconds(cal, 16, 0, 0);
        iter.setEndDate(cal.getTime());
        
        /* Revert calendar to current date */
        cal.setTime(new Date());
        CalendarUtils.setHoursMinutesAndSeconds(cal, 12, 0, 0);
        
        /* Create the history for the iteration */
        BacklogHistory hist = new BacklogHistory();
        hist.setId(123);
        iter.setBacklogHistory(hist);
        hist.setEffortHistoryEntries(new ArrayList<HistoryEntry<BacklogHistory>>());
        
        /* Set the start date entry */
        cal.add(Calendar.DATE, -4);
        HistoryEntry<BacklogHistory> ent1 = new HistoryEntry<BacklogHistory>();
        ent1.setDate(new java.sql.Date(cal.getTimeInMillis()));
        ent1.setHistory(hist);
        ent1.setOriginalEstimate(new AFTime(0));
        ent1.setEffortLeft(new AFTime(0));
        ent1.setDeltaEffortLeft(new AFTime("15h"));
        
        /* D - 3 */
        cal.add(Calendar.DATE, 1);
        HistoryEntry<BacklogHistory> ent2 = new HistoryEntry<BacklogHistory>();
        ent2.setDate(new java.sql.Date(cal.getTimeInMillis()));
        ent2.setHistory(hist);
        ent2.setOriginalEstimate(new AFTime("15h"));
        ent2.setEffortLeft(new AFTime("14h"));
        ent2.setDeltaEffortLeft(new AFTime(0));
        
        /* D - 2 */
        cal.add(Calendar.DATE, 1);
        HistoryEntry<BacklogHistory> ent3 = new HistoryEntry<BacklogHistory>();
        ent3.setDate(new java.sql.Date(cal.getTimeInMillis()));
        ent3.setHistory(hist);
        ent3.setOriginalEstimate(new AFTime("15h"));
        ent3.setEffortLeft(new AFTime("11h"));
        ent3.setDeltaEffortLeft(new AFTime(0));
        
        /* D - 1 */
        cal.add(Calendar.DATE, 1);
        HistoryEntry<BacklogHistory> ent4 = new HistoryEntry<BacklogHistory>();
        ent4.setDate(new java.sql.Date(cal.getTimeInMillis()));
        ent4.setHistory(hist);
        ent4.setOriginalEstimate(new AFTime("15h"));
        ent4.setEffortLeft(new AFTime("6h"));
        ent4.setDeltaEffortLeft(new AFTime("4h"));
        
        /* D */
        cal.add(Calendar.DATE, 1);
        HistoryEntry<BacklogHistory> ent5 = new HistoryEntry<BacklogHistory>();
        ent5.setDate(new java.sql.Date(cal.getTimeInMillis()));
        ent5.setHistory(hist);
        ent5.setOriginalEstimate(new AFTime("19h"));
        ent5.setEffortLeft(new AFTime("10h"));
        ent5.setDeltaEffortLeft(new AFTime(0));
        
        /* Add the entries to the history element in correct order */
        hist.getEffortHistoryEntries().add(ent5);
        hist.getEffortHistoryEntries().add(ent4);
        hist.getEffortHistoryEntries().add(ent3);
        hist.getEffortHistoryEntries().add(ent2);
        hist.getEffortHistoryEntries().add(ent1);
        
        cal.setTime(new Date());
        CalendarUtils.setHoursMinutesAndSeconds(cal, 23, 59, 59);
        cal.add(Calendar.DATE, -1);
        
        expect(backlogDAO.get(3)).andReturn(iter);
        expect(historyDAO.getEntryByDate(3, cal.getTime())).andReturn(ent4);
        replay(backlogDAO);
        replay(historyDAO);
        
        AFTime velocity = new AFTime("3h");
        AFTime trueVel = hisBusiness.calculateDailyVelocity(3);
        assertEquals(velocity.getTime(), trueVel.getTime());
        
        verify(backlogDAO);
        verify(historyDAO);
    }
    
    /**
     * Test the calculation of expected finishing date of a backlog.
     */
    public void testCalculateExpectedDate() {
        /* The backlogs */
        Product prod = new Product();
        Project proj = new Project();
        
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date weekAgo = cal.getTime();
        cal.add(Calendar.DATE, 14);
        Date weekFromNow = cal.getTime();
        
        proj.setStartDate(weekAgo);
        proj.setEndDate(weekFromNow);
        
        Calendar expected = GregorianCalendar.getInstance();
        CalendarUtils.setHoursMinutesAndSeconds(expected, 0, 0, 0);
        
        /* Test the calculation */
        assertNull(hisBusiness.calculateExpectedDate(prod, new AFTime("150h"), new AFTime("5h")));
        
        /* Should finish today */
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("1min"), new AFTime("20h")));        
        
        /* Should finish 4 days from now */
        expected.add(Calendar.DATE, 4);
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("100h"), new AFTime("20h")));
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("105h"), new AFTime("20h")));
        expected.add(Calendar.DATE, -4);
        
        /* Should finish today */
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("5h"), new AFTime("4h")));
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("5h"), new AFTime("5h")));
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("5h"), new AFTime("6h")));
        
        /* 106,75 / 7 = 15,25 ~ 15 days*/
        expected.add(Calendar.DATE, 14);
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("106h 45min"), new AFTime("7h")));
        expected.add(Calendar.DATE, -14);
        
        /* 75/4 = 18,75 ~ 19 days -> 18 days from now */
        expected.add(Calendar.DATE, 18);
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("75"), new AFTime("4")));
        expected.add(Calendar.DATE, -18);
        
        /* 122 days */
        expected.add(Calendar.DATE, 122);
        assertEquals(expected.getTime(), hisBusiness.calculateExpectedDate(proj, new AFTime("246h"), new AFTime("2h")));
        expected.add(Calendar.DATE, -122);
        
        assertNull(hisBusiness.calculateExpectedDate(proj, new AFTime("3h"), new AFTime("-3h", true)));
    }
    
    
    public void testCalculateScheduleVariance() {
        /* The backlogs */
        Product prod = new Product();
        Project proj = new Project();
        Iteration iter = new Iteration();
        
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DATE, -7);
        Date weekAgo = cal.getTime();
        cal.add(Calendar.DATE, 9);
        Date twoDaysFromNow = cal.getTime();
        cal.add(Calendar.DATE, 5);
        Date weekFromNow = cal.getTime();
        
        proj.setStartDate(weekAgo);
        proj.setEndDate(weekFromNow);
        
        iter.setStartDate(weekAgo);
        iter.setEndDate(twoDaysFromNow);
        
        /* 
         * The tests
         */
        assertNull(hisBusiness.calculateScheduleVariance(prod, new AFTime("20h"), new AFTime("4h")));
        
        /* Next day */
        assertEquals(new Integer(-7), hisBusiness.calculateScheduleVariance(
                proj, new AFTime("5h"), new AFTime("4h")));
        assertEquals(new Integer(-2), hisBusiness.calculateScheduleVariance(
                iter, new AFTime("5h"), new AFTime("4h")));
        assertEquals(new Integer(-2), hisBusiness.calculateScheduleVariance(
                iter, new AFTime("1min"), new AFTime("4h")));
        
        /* Week from now */
        assertEquals(new Integer(-1), hisBusiness.calculateScheduleVariance(
                proj, new AFTime("35h"), new AFTime("5h")));
        
        /* A day after what should be */
        assertEquals(new Integer(1), hisBusiness.calculateScheduleVariance(
                proj, new AFTime("45h"), new AFTime("5h")));
        assertEquals(new Integer(1), hisBusiness.calculateScheduleVariance(
                proj, new AFTime("47h"), new AFTime("5h")));
        
        /* A day after, complex hours */
        assertEquals(new Integer(1), hisBusiness.calculateScheduleVariance(
                iter, new AFTime("18h 30min"), new AFTime("5h 12min")));
        assertEquals(new Integer(-1), hisBusiness.calculateScheduleVariance(
                iter, new AFTime("12h"), new AFTime("5h 12min")));
        
    }
    
    public void testCalculateScopingNeeded() {
        Product prod = new Product();
        Iteration iter = new Iteration();
        
        Calendar cal = GregorianCalendar.getInstance();
        cal.add(Calendar.DATE, 6);
        Date weekFromNow = cal.getTime();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -7);
        Date weekAgo = cal.getTime();
        
        iter.setStartDate(weekAgo);
        iter.setEndDate(weekFromNow);
        
        /* A product backlog always returns null */
        assertNull(hisBusiness.calculateScopingNeeded(prod, new AFTime("10h"), new AFTime("2h")));
                
        /* Negative velocity always returns null */
        assertNull(hisBusiness.calculateScopingNeeded(iter, new AFTime("10h"), new AFTime("-2h")));
        
        /* The real numbers */
        assertEquals(new AFTime("4h"), hisBusiness.calculateScopingNeeded(iter, new AFTime("18h"), new AFTime("2h")));
        assertEquals(new AFTime("11h 30min"), hisBusiness.calculateScopingNeeded(iter, new AFTime("81h 30min"), new AFTime("10h")));
        
        assertEquals(new AFTime(0), hisBusiness.calculateScopingNeeded(iter, new AFTime("10h"), new AFTime("3h")));
    }
}

