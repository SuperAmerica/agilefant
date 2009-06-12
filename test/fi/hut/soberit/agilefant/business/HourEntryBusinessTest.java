package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.fail;

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
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;



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
    public void setUp() {
        hourEntryBusiness = new HourEntryBusinessImpl();
        theDAO = createMock(TaskHourEntryDAO.class);
        blheDAO = createMock(BacklogHourEntryDAO.class);
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

}
