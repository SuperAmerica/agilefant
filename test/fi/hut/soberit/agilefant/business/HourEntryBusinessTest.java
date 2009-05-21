package fi.hut.soberit.agilefant.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.business.impl.SettingBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Setting;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.CalendarUtils;
import fi.hut.soberit.agilefant.util.DailySpentEffort;
import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;



public class HourEntryBusinessTest extends TestCase {

    private HourEntryBusinessImpl hourEntryBusiness;
    private SettingBusinessImpl settingBusiness;
    private BacklogItemHourEntryDAO bheDAO;
    private BacklogHourEntryDAO blheDAO;
    private HourEntryDAO heDAO;
    private SettingDAO settingDAO;
    
    private void compareHe(HourEntry he1, HourEntry he2) 
            throws Exception {
        if(he1.getUser().getId() != he2.getUser().getId()) {
            throw new Exception("Users not equal.");
        }
        if(!he1.getDate().equals(he2.getDate())) {
            throw new Exception("Dates not equal.");
        }
        if(!he1.getDescription().equals(he2.getDescription())) {
            throw new Exception("Descriptions not equal.");
        }
        if(!he1.getTimeSpent().equals(he2.getTimeSpent())) {
            throw new Exception("Time spent not equal.");
        }
    }
    public void testStore()
    {
        hourEntryBusiness = new HourEntryBusinessImpl();
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        blheDAO = createMock(BacklogHourEntryDAO.class);
        hourEntryBusiness.setBacklogHourEntryDAO(blheDAO);
        hourEntryBusiness.setBacklogItemHourEntryDAO(bheDAO);
        
        Backlog bl = new Iteration();
        BacklogItem bli = new BacklogItem();
        HourEntry he = new HourEntry();
        BacklogItemHourEntry blihe = new BacklogItemHourEntry();
        BacklogHourEntry blhe = new BacklogHourEntry();
        User u = new User();
        u.setId(1);
        he.setUser(u);
        he.setDate(new Date());
        he.setDescription("test");
        he.setTimeSpent(new AFTime(2));
        blhe.setId(1);
        blihe.setId(1);
        
        he.setId(1);
        expect(bheDAO.get(1)).andReturn(blihe).times(1);
        expect(blheDAO.get(1)).andReturn(blhe).times(1);
        bheDAO.store(blihe);
        blheDAO.store(blhe);
        replay(blheDAO);
        replay(bheDAO);
        //store under BLI
        hourEntryBusiness.store(bli, he);
        try {
            compareHe(he,blihe);
        } catch(Exception e) {
            fail("Hour entry data update failed!");
        }
        //store under BL
        hourEntryBusiness.store(bl, he);
        try {
            compareHe(he,blhe);
        } catch(Exception e) {
            fail("Hour entry data update failed!");
        }
        //store under null
        try {
            hourEntryBusiness.store(null, he);
            fail("Exception expected when storing under invalid parent.");
        } catch(IllegalArgumentException iae) { }
        //store null entry
        try {
            hourEntryBusiness.store(bl, null);
            fail("Exception expected when storing null entry.");
        } catch(IllegalArgumentException iae) { }
        
        verify(bheDAO);
        verify(blheDAO);
    }


    public void testLoadSumsToBacklogItems() {
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        settingDAO = createMock(SettingDAO.class);
        settingBusiness = new SettingBusinessImpl();
        settingBusiness.setSettingDAO(settingDAO);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setSettingBusiness(settingBusiness);
        hourEntryBusiness.setBacklogItemHourEntryDAO(bheDAO);
        Iteration iteration = new Iteration();
        BacklogItem bli1 = new BacklogItem();
        BacklogItem bli2 = new BacklogItem();
        BacklogItemHourEntry blihe1 = new BacklogItemHourEntry();
        BacklogItemHourEntry blihe2 = new BacklogItemHourEntry();
        BacklogItemHourEntry blihe3 = new BacklogItemHourEntry();
        ArrayList<BacklogItemHourEntry> collection = new ArrayList<BacklogItemHourEntry>();
        Setting setting = new Setting();
        setting.setValue(SettingBusinessImpl.SETTING_VALUE_TRUE);
        
        blihe1.setTimeSpent(new AFTime(10));
        blihe1.setBacklogItem(bli1);
        collection.add(blihe1);
        blihe2.setTimeSpent(new AFTime(1));
        blihe2.setBacklogItem(bli1);
        collection.add(blihe2);
        blihe3.setTimeSpent(new AFTime(2));
        blihe3.setBacklogItem(bli2);
        collection.add(blihe3);
        
        expect(bheDAO.getSumsByBacklog(iteration)).andReturn(collection).atLeastOnce();
        replay(bheDAO);
        expect(settingDAO.getSetting(SettingBusinessImpl.SETTING_NAME_HOUR_REPORTING))
            .andReturn(setting).atLeastOnce();
        replay(settingDAO);
        
        // Try a normal case with timesheet functionality turned on.
        hourEntryBusiness.loadSumsToBacklogItems(iteration);
        assertEquals(bli1.getEffortSpent().getTime(), new AFTime(11).getTime());
        assertEquals(bli2.getEffortSpent().getTime(), new AFTime(2).getTime());
        
        // Try with timesheet functionality turned off.
        bli1.setEffortSpent(null);
        bli2.setEffortSpent(null);
        setting.setValue("false");
        hourEntryBusiness.loadSumsToBacklogItems(iteration);
        assertNull(bli1.getEffortSpent());
        assertNull(bli2.getEffortSpent());
        
        verify(bheDAO);
        verify(settingDAO);
    }
    

    public void testRemoveHourEntriesByParent()
    {
        hourEntryBusiness = new HourEntryBusinessImpl();
        heDAO = createMock(HourEntryDAO.class);
        hourEntryBusiness.setHourEntryDAO(heDAO);
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        blheDAO = createMock(BacklogHourEntryDAO.class);
        hourEntryBusiness.setBacklogHourEntryDAO(blheDAO);
        hourEntryBusiness.setBacklogItemHourEntryDAO(bheDAO);
        
        BacklogItemHourEntry blihe1 = new BacklogItemHourEntry();
        BacklogItemHourEntry blihe2 = new BacklogItemHourEntry();
        BacklogHourEntry blhe1 = new BacklogHourEntry();
        BacklogHourEntry blhe2 = new BacklogHourEntry();
        Backlog bl1 = new Iteration();
        Backlog bl2 = new Iteration();
        BacklogItem bli = new BacklogItem();
        List<BacklogHourEntry> blheList = new ArrayList<BacklogHourEntry>();
        List<BacklogItemHourEntry> bliheList = new ArrayList<BacklogItemHourEntry>();
  
        blihe1.setBacklogItem(bli);
        blihe1.setId(1);
        blihe2.setBacklogItem(bli);
        blihe2.setId(2);
        blhe1.setBacklog(bl1);
        blhe1.setId(3);
        blhe2.setBacklog(bl1);
        blhe2.setId(4);    
        
        blheList.add(blhe1);
        blheList.add(blhe2);
        bliheList.add(blihe1);
        bliheList.add(blihe2);
        
        expect(bheDAO.getEntriesByBacklogItem(bli)).andReturn(bliheList).times(1);
        expect(blheDAO.getEntriesByBacklog(bl1)).andReturn(blheList).times(1);
        expect(blheDAO.getEntriesByBacklog(bl2)).andReturn(null).times(1);
        heDAO.remove(1);
        heDAO.remove(2);
        heDAO.remove(3);
        heDAO.remove(4);
        replay(bheDAO);
        replay(blheDAO);
        replay(heDAO);
        //remove under BLI that has HEs
        hourEntryBusiness.removeHourEntriesByParent(bli);
        //remove under BL that has HEs
        hourEntryBusiness.removeHourEntriesByParent(bl1);
        //remove under BL that has no HEs
        hourEntryBusiness.removeHourEntriesByParent(bl2);
        //remove under null
        hourEntryBusiness.removeHourEntriesByParent(null);
        verify(bheDAO);
        verify(blheDAO);
        verify(heDAO);
    }
    
    public void testGetEffortSumByUserAndTimeInterval() {
        heDAO = createMock(HourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setHourEntryDAO(heDAO);
        User user = new User();
        String correctStartString = new String("2008-06-10 10:10");
        String falseStartString = new String("2008-13-10 10:10");
        String correctEndString = new String("2008-07-10 11:11");
        String falseEndString = new String("2008-07+10,13:24");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        AFTime timeSum = new AFTime(3600);
        AFTime timeRet;
        Date startDate;
        Date endDate;
        
        
        try {
            startDate = df.parse(correctStartString);
            endDate = df.parse(correctEndString);
            
            expect(heDAO.getEffortSumByUserAndTimeInterval(user, startDate, endDate))
                        .andReturn(timeSum).atLeastOnce();
            replay(heDAO);
            
            // Try with correct dates.
            timeRet = hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                    startDate, endDate);
            assertSame(timeSum, timeRet);
            
            // Try with correct strings.
            timeRet = hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                    correctStartString, correctEndString);
            assertSame(timeSum, timeRet);
            
            // Try with startDate being after endDate.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, endDate, startDate);
                fail("StartDate was after endDate, but no exception was thrown.");
            } catch (IllegalArgumentException iae) {
                
            }
            
            // Try the same as above, but with strings.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                        correctEndString, correctStartString);
                fail("StartDate was after endDate, but no exception was thrown. " +
                        "(Dates in string format)");
            } catch (IllegalArgumentException iae) {
                
            }
            
            // Try with starting string in wrong format.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                        falseStartString, correctEndString);
                fail("Starting date string was in wrong format, but no exception was thrown.");
            } catch (IllegalArgumentException iae) {
                
            }
            
            // Try with ending string in wrong format.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                        correctStartString, falseEndString);
                fail("Ending date string was in wrong format, but no exception was thrown.");
            } catch (IllegalArgumentException iae) {
                
            }
            
            // Try with both strings in wrong format.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                        falseStartString, falseEndString);
                fail("Both date strings were in wrong format, but no exception was thrown.");
            } catch (IllegalArgumentException iae) {
                
            }
            
            verify(heDAO);
        } catch (ParseException pe) {
            fail("ParseException.");
        }
    }
    public void testIsAssociatedWithHourReport()
    {
        heDAO = createMock(HourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setHourEntryDAO(heDAO);
        ArrayList<HourEntry> list1 = new ArrayList<HourEntry>();
        ArrayList<HourEntry> list2 = new ArrayList<HourEntry>();
        list1.add(new HourEntry());
        list1.add(new HourEntry());
        User user = new User();
        User user2 = new User();
        User user3 = new User();
        expect(heDAO.getHourEntriesByUser(user)).andReturn(list1);
        expect(heDAO.getHourEntriesByUser(user2)).andReturn(list2);
        expect(heDAO.getHourEntriesByUser(user3)).andReturn(null);
        replay(heDAO);
        assertTrue(hourEntryBusiness.isAssociatedWithHourReport(user));
        assertFalse(hourEntryBusiness.isAssociatedWithHourReport(user2));
        assertFalse(hourEntryBusiness.isAssociatedWithHourReport(user3));
        verify(heDAO);
    }
    
    public void testGetSumsByIterationGoal(){
        Map<Integer, AFTime> result;
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setBacklogItemHourEntryDAO(bheDAO);
        Iteration iteration = new Iteration();
        BacklogItem bli1 = new BacklogItem();
        BacklogItem bli2 = new BacklogItem();
        BacklogItem bli3 = new BacklogItem();
        BacklogItemHourEntry blihe1 = new BacklogItemHourEntry();
        BacklogItemHourEntry blihe2 = new BacklogItemHourEntry();
        BacklogItemHourEntry blihe3 = new BacklogItemHourEntry();
        BacklogItemHourEntry blihe4 = new BacklogItemHourEntry();
        IterationGoal goal1 = new IterationGoal();
        IterationGoal goal2 = new IterationGoal();
        ArrayList<BacklogItemHourEntry> collection = new ArrayList<BacklogItemHourEntry>();
        
        blihe1.setTimeSpent(new AFTime(10));
        blihe1.setBacklogItem(bli1);
        bli1.setIterationGoal(goal1);
        collection.add(blihe1);
        blihe2.setTimeSpent(new AFTime(1));
        blihe2.setBacklogItem(bli1);
        bli2.setIterationGoal(goal2);
        collection.add(blihe2);
        blihe3.setTimeSpent(new AFTime(2));
        blihe3.setBacklogItem(bli2);
        collection.add(blihe3);
        blihe4.setTimeSpent(new AFTime(60));
        blihe4.setBacklogItem(bli3);
        bli3.setIterationGoal(goal1);
        collection.add(blihe4);
        
        goal1.setId(1);
        goal2.setId(2);
        
        expect(bheDAO.getSumsByBacklog(iteration)).andReturn(collection);
        replay(bheDAO);
        
        result = hourEntryBusiness.getSumsByIterationGoal(iteration);
        
        assertEquals("Wrong sum for iteration goal 1, ", 71, result.get(1).getTime());
        
        assertEquals("Wrong sum for iteration goal 2, ", 2, result.get(2).getTime());
    }
    
    public void testGetSumsByIterationGoal_NoChildren(){
        Map<Integer, AFTime> result = new HashMap<Integer, AFTime>();
        Iteration iteration = new Iteration();
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setBacklogItemHourEntryDAO(bheDAO);
        
        expect(bheDAO.getSumsByBacklog(iteration)).andReturn(null);
        
        try{
            result = hourEntryBusiness.getSumsByIterationGoal(iteration);
        }catch(NullPointerException e){
            fail("NullPointerException with null children");
        }
            
        assertTrue("Invalid data with null children", result.isEmpty());
    }
    
    private static HourEntry createEntry(int year, int month, int day, AFTime effort) {
        HourEntry entry = new HourEntry();
        Calendar cal = Calendar.getInstance();
        CalendarUtils.setHoursMinutesAndSeconds(cal, 0, 0, 0);
        cal.set(year, month, day);
        entry.setDate(cal.getTime());
        entry.setTimeSpent(effort);
        return entry;
    }
    
    public void testGetDailySpentEffortByIntervalAndUser_noData() {
        heDAO = createMock(HourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setHourEntryDAO(heDAO);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(2009, Calendar.JUNE, 1, 0, 0);
        end.set(2009, Calendar.JUNE, 7, 0, 0);
        
        List<HourEntry> entries = new ArrayList<HourEntry>();

        expect(heDAO.getEntriesByIntervalAndUser(start.getTime(), end.getTime(), null)).andReturn(entries);

        replay(heDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByIntervalAndUser(start.getTime(), end.getTime(), null);
        assertEquals(7, res.size());
        assertEquals(null, res.get(0).getSpentEffort());
        assertEquals(null, res.get(1).getSpentEffort());
        assertEquals(null, res.get(2).getSpentEffort());
        assertEquals(null, res.get(3).getSpentEffort());
        assertEquals(null, res.get(4).getSpentEffort());
        assertEquals(null, res.get(5).getSpentEffort());
        assertEquals(null, res.get(6).getSpentEffort());
        
        verify(heDAO);
    }
    public void testGetDailySpentEffortByIntervalAndUser_yearChanges() {
        heDAO = createMock(HourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setHourEntryDAO(heDAO);
        List<HourEntry> entries = new ArrayList<HourEntry>();
        entries.add(createEntry(2008, Calendar.JANUARY, 28, new AFTime(100)));
        entries.add(createEntry(2008, Calendar.DECEMBER, 28, new AFTime(900)));
        entries.add(createEntry(2008, Calendar.DECEMBER, 28, new AFTime(1000)));
        entries.add(createEntry(2008, Calendar.DECEMBER, 29, new AFTime(4000)));
        entries.add(createEntry(2009, Calendar.JANUARY, 1, new AFTime(50000)));
        entries.add(createEntry(2009, Calendar.JANUARY, 2, new AFTime(6000000)));
        entries.add(createEntry(2009, Calendar.SEPTEMBER, 28, new AFTime(70000000)));
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(2008, Calendar.DECEMBER, 27, 0, 0);
        end.set(2009, Calendar.JANUARY, 3, 0, 0);
        
        expect(heDAO.getEntriesByIntervalAndUser(start.getTime(), end.getTime(), null)).andReturn(entries);
        replay(heDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByIntervalAndUser(start.getTime(), end.getTime(), null);
        assertEquals(8, res.size());
        assertEquals(null, res.get(0).getSpentEffort());
        assertEquals(new AFTime(1900), res.get(1).getSpentEffort());
        assertEquals(new AFTime(4000), res.get(2).getSpentEffort());
        assertEquals(null, res.get(3).getSpentEffort());
        assertEquals(null, res.get(4).getSpentEffort());
        assertEquals(new AFTime(50000), res.get(5).getSpentEffort());
        assertEquals(new AFTime(6000000), res.get(6).getSpentEffort());
        assertEquals(null, res.get(7).getSpentEffort());
        verify(heDAO);
    }
    public void testGetDailySpentEffortByIntervalAndUser() {
        heDAO = createMock(HourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setHourEntryDAO(heDAO);
        List<HourEntry> entries = new ArrayList<HourEntry>();
        entries.add(createEntry(2009, 1, 28, new AFTime(100)));
        entries.add(createEntry(2009, Calendar.APRIL, 28, new AFTime(900)));
        entries.add(createEntry(2009, Calendar.APRIL, 28, new AFTime(1000)));
        entries.add(createEntry(2009, Calendar.APRIL, 30, new AFTime(4000)));
        entries.add(createEntry(2009, Calendar.APRIL, 30, new AFTime(50000)));
        entries.add(createEntry(2009, Calendar.MAY, 1, new AFTime(6000000)));
        entries.add(createEntry(2009, Calendar.MAY, 28, new AFTime(70000000)));
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        start.set(2009, Calendar.APRIL, 28, 0, 0);
        end.set(2009, Calendar.MAY, 1, 23, 59);
        
        expect(heDAO.getEntriesByIntervalAndUser(start.getTime(), end.getTime(), null)).andReturn(entries);
        replay(heDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByIntervalAndUser(start.getTime(), end.getTime(), null);
        assertEquals(4, res.size());
        assertEquals(new AFTime(1900), res.get(0).getSpentEffort());
        assertEquals(null, res.get(1).getSpentEffort());
        assertEquals(new AFTime(54000), res.get(2).getSpentEffort());
        assertEquals(new AFTime(6000000), res.get(3).getSpentEffort());
        verify(heDAO);
    }
}
