package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.User;

public class HourEntryActionTest {
    
    private HourEntryAction hourEntryAction;

    private HourEntryBusiness hourEntryBusiness;
    
    private HourEntry hourEntry;
    private User user;
    
    @Before
    public void setUp_dependencies() {
        hourEntryAction = new HourEntryAction();
        
        hourEntryBusiness = createMock(HourEntryBusiness.class);
        hourEntryAction.setHourEntryBusiness(hourEntryBusiness);
    }
    
    private void replayAll() {
        replay(hourEntryBusiness);
    }
    
    private void verifyAll() {
        verify(hourEntryBusiness);
    }
    
    @Before
    public void setUp_data() {
        user = new User();
        user.setId(10);

        hourEntry = new HourEntry();
        hourEntry.setId(1);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testDelete_nonExistentHourEntry() {
        hourEntryAction.setHourEntryId(-1);
        expect(hourEntryBusiness.retrieve(-1))
            .andThrow(new ObjectNotFoundException());
        replayAll();
        
        hourEntryAction.delete();
        
        verifyAll();
    }
    
    @Test
    public void testDelete_happyCase() {
        hourEntryAction.setHourEntryId(hourEntry.getId());
        
        expect(hourEntryBusiness.retrieve(hourEntry.getId())).andReturn(hourEntry);
        hourEntryBusiness.delete(hourEntry.getId());
        replayAll();
        
        assertEquals(Action.SUCCESS, hourEntryAction.delete());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testRetrieve_noSuchHourEntry() {
        hourEntryAction.setHourEntryId(-1);
        expect(hourEntryBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        hourEntryAction.retrieve();
        
        verifyAll();
    }  
    
    @Test
    public void testLogStoryEffort() {
        HourEntry effortEntry = new HourEntry();
        Set<Integer> userIds = new HashSet<Integer>();
        hourEntryBusiness.logStoryEffort(1, effortEntry, userIds);
        replayAll();
        hourEntryAction.setParentObjectId(1);
        hourEntryAction.setHourEntry(effortEntry);
        hourEntryAction.setUserIds(userIds);
        hourEntryAction.logStoryEffort();
        verifyAll();
    }
}
