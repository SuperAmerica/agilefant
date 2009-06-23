package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Collection;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import flexjson.JSONSerializer;


public class StoryActionTest {

    StoryAction storyAction = new StoryAction();
    BacklogBusiness backlogBusiness;
    StoryBusiness storyBusiness;
    Story story;
    Iteration iter;
    
    @Before
    public void setUp() {
        story = new Story();
        story.setId(1234);
        iter = new Iteration();
        iter.setId(6446);
        
        storyAction.setStory(story);
        
        storyBusiness = createMock(StoryBusiness.class);
        storyAction.setStoryBusiness(storyBusiness);
        
        backlogBusiness = createMock(BacklogBusiness.class);
        storyAction.setBacklogBusiness(backlogBusiness);
    }
    
    @Test
    public void testGetStoryContents() {
        storyAction.setIterationId(iter.getId());
        story.setBacklog(iter);
        storyAction.setStoryId(story.getId());
        story.setTasks(Arrays.asList(new Task(), new Task()));
        expect(storyBusiness.getStoryContents(story.getId(), iter.getId()))
            .andReturn(null);
        replay(storyBusiness);
        
        assertNull(storyAction.getJsonData());
        assertEquals(CRUDAction.AJAX_SUCCESS, storyAction.getStoryContents());
        assertNotNull(storyAction.getJsonData());
        verify(storyBusiness);
    }
    
    @Test
    public void testMoveStory() {
        storyAction.setStoryId(story.getId());
        storyAction.setBacklogId(iter.getId());
        storyBusiness.attachStoryToBacklog(story.getId(), iter.getId(), false);
        replay(storyBusiness, backlogBusiness);
        assertEquals(CRUDAction.AJAX_SUCCESS, storyAction.moveStory());
        verify(storyBusiness, backlogBusiness);
    }
    
    @Test
    public void testAjaxGetStories() {
        Collection<Story> stories = Arrays.asList(story); 
        iter.setStories(stories);
        
        storyAction.setBacklogId(iter.getId());
        expect(backlogBusiness.retrieve(iter.getId())).andReturn(iter);
        replay(storyBusiness, backlogBusiness);

        assertEquals(CRUDAction.AJAX_SUCCESS, storyAction.ajaxGetStories());
        assertEquals(new JSONSerializer().serialize(stories), storyAction.getJsonData());
        
        verify(storyBusiness, backlogBusiness);
    }
    
    @Test
    public void testAjaxGetStories_invalidBacklog() {
        storyAction.setBacklogId(-1);
        
        expect(backlogBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replay(storyBusiness, backlogBusiness);
        
        assertEquals(CRUDAction.AJAX_ERROR, storyAction.ajaxGetStories());
        
        verify(storyBusiness, backlogBusiness);
    }
    
    @Test
    public void testAjaxDeleteStory() {
       storyBusiness.remove(story.getId());
       replay(storyBusiness);
       
       assertEquals(CRUDAction.AJAX_SUCCESS, storyAction.ajaxDeleteStory());
       
       verify(storyBusiness);
    }
    
    @Test
    public void testAjaxDeleteStory_storyHourEntries() {
       storyBusiness.remove(story.getId());
       expectLastCall().andThrow(new ConstraintViolationException("Action not allowed", null, null));
       replay(storyBusiness);
       
       assertEquals(CRUDAction.AJAX_FORBIDDEN, storyAction.ajaxDeleteStory());
       
       verify(storyBusiness);
    }
}
