package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.business.impl.SettingBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.SettingDAO;
import fi.hut.soberit.agilefant.db.TaskHourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.DailySpentEffort;



public class HourEntryBusinessTest {

    private HourEntryBusinessImpl hourEntryBusiness;
    private SettingBusinessImpl settingBusiness;
    private TaskHourEntryDAO theDAO;
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
        if(he1.getMinutesSpent() != he2.getMinutesSpent()) {
            throw new Exception("Time spent not equal.");
        }
    }
    
    @Before
    public void setUp_dependencies() {
        hourEntryBusiness = new HourEntryBusinessImpl();
        heDAO = createMock(HourEntryDAO.class);
        theDAO = createMock(TaskHourEntryDAO.class);
        blheDAO = createMock(BacklogHourEntryDAO.class);
        hourEntryBusiness.setHourEntryDAO(heDAO);
        hourEntryBusiness.setBacklogHourEntryDAO(blheDAO);
        hourEntryBusiness.setTaskHourEntryDAO(theDAO);
    }
    
    @Test
    public void testStore() {
        Backlog bl = new Iteration();
        Task task = new Task();
        HourEntry he = new HourEntry();
        TaskHourEntry taskHourEntry = new TaskHourEntry();
        BacklogHourEntry backlogHourEntry = new BacklogHourEntry();
        User u = new User();
        u.setId(1);
        he.setUser(u);
        he.setDate(new DateTime());
        he.setDescription("test");
        he.setMinutesSpent(120);
        backlogHourEntry.setId(1);
        taskHourEntry.setId(1);
        
        he.setId(1);
        expect(theDAO.get(1)).andReturn(taskHourEntry).times(1);
        expect(blheDAO.get(1)).andReturn(backlogHourEntry).times(1);
        theDAO.store(taskHourEntry);
        blheDAO.store(backlogHourEntry);
        replay(blheDAO);
        replay(theDAO);
        //store under BLI
        hourEntryBusiness.store(task, he);
        try {
            compareHe(he,taskHourEntry);
        } catch(Exception e) {
            fail("Hour entry data update failed!");
        }
        //store under BL
        hourEntryBusiness.store(bl, he);
        try {
            compareHe(he,backlogHourEntry);
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
        
        verify(theDAO);
        verify(blheDAO);
    }
    
    @Test
    public void testCalculateSumOfBacklogsHourEntries() {
        Iteration iteration = new Iteration();
        iteration.setId(123);
        expect(heDAO.calculateIterationHourEntriesSum(123))
            .andReturn(22332L);
        replay(heDAO);
        
        assertEquals(22332L, hourEntryBusiness.calculateSumOfIterationsHourEntries(iteration));
        
        verify(heDAO);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCalculateSumOfBacklogsHourEntries_nullBacklog() {
        hourEntryBusiness.calculateSumOfIterationsHourEntries(null);
    }
    
    @Test
    public void testCalculateSumByUserAndTimeInterval() {
        DateTime start = new DateTime();
        DateTime end = start.plusDays(7);
        User user = new User();
        user.setId(11);
        
        expect(heDAO.calculateSumByUserAndTimeInterval(user, start, end)).andReturn(400L);
        expect(heDAO.calculateSumByUserAndTimeInterval(11, start, end)).andReturn(400L);
        
        replay(heDAO);
        assertEquals(400L, this.hourEntryBusiness.calculateSumByUserAndTimeInterval(11, start, end));
        assertEquals(400L, this.hourEntryBusiness.calculateSumByUserAndTimeInterval(user, start, end));
        verify(heDAO);
    }
    
    @Test
    public void testGetEntriesByUserAndTimeInterval() {
        DateTime start = new DateTime();
        DateTime end = start.plusDays(7);
        List<HourEntry> noEntries = Collections.emptyList();
        expect(heDAO.getHourEntriesByFilter(start, end, 11)).andReturn(noEntries);
        replay(heDAO);
        assertEquals(noEntries, hourEntryBusiness.getEntriesByUserAndTimeInterval(11, start, end));
        verify(heDAO);
    }
    
    private static HourEntry createEntry(int year, int month, int day, long effort) {
        HourEntry entry = new HourEntry();
        DateTime date = new DateTime(year, month, day, 0,0,0,0);
        entry.setMinutesSpent(effort);
        entry.setDate(date);
        return entry;
    }
    @Test
    public void testGetDailySpentEffortByWeek() {
        DateTime start = new DateTime(2009,6,1,0,0,1,0);
        DateTime end = new DateTime(2009,6,7,23,59,59,0);
        List<HourEntry> entries = Collections.emptyList();
        
        expect(heDAO.getHourEntriesByFilter(start, end, 0)).andReturn(entries);

        replay(heDAO);
        assertEquals(7, hourEntryBusiness.getDailySpentEffortByWeek(start.toLocalDate(), 0).size());
        verify(heDAO);
    }
    
    @Test
    public void calculateWeekSum() {
        DateTime start = new DateTime(2009,6,1,0,0,1,0);
        DateTime end = new DateTime(2009,6,7,23,59,59,0);
        
        expect(heDAO.calculateSumByUserAndTimeInterval(0, start, end)).andReturn(0L);

        replay(heDAO);
        assertEquals(0L, hourEntryBusiness.calculateWeekSum(start.plusDays(3).toLocalDate(), 0));
        verify(heDAO);
    }
    
    @Test
    public void testGetDailySpentEffortByInterval_noData() {
        DateTime start = new DateTime(2009,6,1,0,0,0,0);
        DateTime end = new DateTime(2009,6,7,0,0,0,0);
        
        List<HourEntry> entries = new ArrayList<HourEntry>();

        expect(heDAO.getHourEntriesByFilter(start, end, 0)).andReturn(entries);

        replay(heDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByInterval(start, end, 0);
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
    
    @Test
    public void testGetDailySpentEffortByInterval_yearChanges() {
        List<HourEntry> entries = new ArrayList<HourEntry>();
        entries.add(createEntry(2008, 1, 28, 100));
        entries.add(createEntry(2008, 12, 28, 900));
        entries.add(createEntry(2008, 12, 28, 1000));
        entries.add(createEntry(2008, 12, 29, 4000));
        entries.add(createEntry(2009, 1, 1, 50000));
        entries.add(createEntry(2009, 1, 2, 6000000));
        entries.add(createEntry(2009, 7, 28, 70000000));
        DateTime start = new DateTime(2008,12,27,0,0,0,0);
        DateTime end = new DateTime(2009,1,3,0,0,0,0);
        
        expect(heDAO.getHourEntriesByFilter(start, end, 0)).andReturn(entries);
        replay(heDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByInterval(start, end, 0);
        assertEquals(8, res.size());
        assertEquals(null, res.get(0).getSpentEffort());
        assertEquals(1900L, (long)res.get(1).getSpentEffort());
        assertEquals(4000L, (long)res.get(2).getSpentEffort());
        assertEquals(null, res.get(3).getSpentEffort());
        assertEquals(null, res.get(4).getSpentEffort());
        assertEquals(50000L, (long)res.get(5).getSpentEffort());
        assertEquals(6000000L, (long)res.get(6).getSpentEffort());
        assertEquals(null, res.get(7).getSpentEffort());
        verify(heDAO);
    }
    
    @Test
    public void testGetDailySpentEffortByInterval() {
        List<HourEntry> entries = new ArrayList<HourEntry>();
        entries.add(createEntry(2009, 1, 28, 100));
        entries.add(createEntry(2009, 4, 28, 900));
        entries.add(createEntry(2009, 4, 28, 1000));
        entries.add(createEntry(2009, 4, 30, 4000));
        entries.add(createEntry(2009, 4, 30, 50000));
        entries.add(createEntry(2009, 5, 1, 6000000));
        entries.add(createEntry(2009, 5, 28, 70000000));
        DateTime start = new DateTime(2009,4,28,0,0,0,0);
        DateTime end = new DateTime(2009,5,1,23,59,0,0);

        
        expect(heDAO.getHourEntriesByFilter(start, end, 0)).andReturn(entries);
        replay(heDAO);
        List<DailySpentEffort> res = hourEntryBusiness.getDailySpentEffortByInterval(start, end, 0);
        assertEquals(4, res.size());
        assertEquals(1900L, (long)res.get(0).getSpentEffort());
        assertEquals(null, res.get(1).getSpentEffort());
        assertEquals(54000L, (long)res.get(2).getSpentEffort());
        assertEquals(6000000L, (long)res.get(3).getSpentEffort());
        verify(heDAO);
    }
    
    @Test
    public void testGetEntriesByUserAndDay() {
        DateTime start = new DateTime(2009,6,2,0,0,0,0);
        DateTime end = new DateTime(2009,6,2,23,59,59,0);
        expect(heDAO.getHourEntriesByFilter(start, end, 42)).andReturn(null);
        replay(heDAO);
        assertEquals(null, hourEntryBusiness.getEntriesByUserAndDay(start.toLocalDate(), 42));
        verify(heDAO);
    }

}
