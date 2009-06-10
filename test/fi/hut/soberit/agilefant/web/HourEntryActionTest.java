package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.*;
import static org.junit.Assert.*;

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
    private StoryBusiness storyBusiness;
    private TaskBusiness taskBusiness;
    private ProjectBusiness projectBusiness;
    private UserBusiness userBusiness;
    
    private HourEntry hourEntry;
    private User user;
    
    @Before
    public void setUp() {
        hourEntryAction = new HourEntryAction();
        
        hourEntryBusiness = createMock(HourEntryBusiness.class);
        hourEntryAction.setHourEntryBusiness(hourEntryBusiness);
        
        storyBusiness = createMock(StoryBusiness.class);
        hourEntryAction.setStoryBusiness(storyBusiness);
        
        taskBusiness = createMock(TaskBusiness.class);
        hourEntryAction.setTaskBusiness(taskBusiness);
        
        projectBusiness = createMock(ProjectBusiness.class);
        hourEntryAction.setProjectBusiness(projectBusiness);
        
        userBusiness = createMock(UserBusiness.class);
        hourEntryAction.setUserBusiness(userBusiness);
        
        user = new User();
        user.setId(10);

        hourEntry = new HourEntry();
        hourEntry.setId(1); 
    }
    
    @Test
    public void testDelete_nonExistentHourEntry() {
        hourEntryAction.setHourEntryId(-1);
        expect(hourEntryBusiness.retrieve(-1))
            .andThrow(new ObjectNotFoundException());
        replay(hourEntryBusiness);
        
        assertEquals(CRUDAction.AJAX_ERROR, hourEntryAction.delete());
        
        verify(hourEntryBusiness);
    }
    
    @Test
    public void testDelete_happyCase() {
        hourEntryAction.setHourEntryId(hourEntry.getId());
        
        expect(hourEntryBusiness.retrieve(hourEntry.getId())).andReturn(hourEntry);
        hourEntryBusiness.delete(hourEntry.getId());
        replay(hourEntryBusiness);
        
        assertEquals(CRUDAction.AJAX_SUCCESS, hourEntryAction.delete());
        
        verify(hourEntryBusiness);
    }
    
    @Test
    public void testEdit() {
        hourEntryAction.setHourEntryId(-1);
        expect(hourEntryBusiness.retrieve(-1))
            .andThrow(new ObjectNotFoundException());
        replay(hourEntryBusiness);
        
        assertEquals("error", hourEntryAction.edit());
        
        verify(hourEntryBusiness);
    }    
}
