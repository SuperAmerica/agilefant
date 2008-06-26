package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import fi.hut.soberit.agilefant.business.impl.HistoryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHistory;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HistoryEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import junit.framework.TestCase;

public class HistoryBusinessTest extends TestCase {

    private BacklogDAO backlogDAO;
    private HistoryBusinessImpl hisBusiness = new HistoryBusinessImpl();
    
    
    public void testAddFirstBacklogItemWithoutEstimate() {
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
    
    public void testAddFirstBacklogItemWithEstimate() {
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
    
    public void testMultipleBacklogItems() {
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
        final int bliId2 = 11;
        final int backlogId = 100;
        final int effLeft = 12;
        final int origEst = 17;
        final int delta = -7;
        final int hour1 = 15;
        final int hour2 = 8;
     
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
    
    
    
}
