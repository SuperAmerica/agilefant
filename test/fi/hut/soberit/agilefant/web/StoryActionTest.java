package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;




public class StoryActionTest {

    StoryAction storyAction = new StoryAction();
    StoryBusiness storyBusiness;
    Story story;
    Iteration iter;
    
    @Before
    public void setUp() {
        story = new Story();
        story.setId(1234);
        iter = new Iteration();
        iter.setId(6446);
        
        storyBusiness = createMock(StoryBusiness.class);
        storyAction.setStoryBusiness(storyBusiness);
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
        storyAction.setIterationId(iter.getId());
        storyBusiness.attachStoryToIteration(story.getId(), iter.getId(), false);
        replay(storyBusiness);
        assertEquals(CRUDAction.AJAX_SUCCESS, storyAction.moveStory());
        verify(storyBusiness);
    }
    
}
