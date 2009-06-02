package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Arrays;

import org.junit.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;




public class StoryActionTest {

    StoryAction storyAction = new StoryAction();
    StoryBusiness storyBusiness;
    Story story;
    
    @Before
    public void setUp() {
        story = new Story();
        story.setId(1234);
        storyBusiness = createMock(StoryBusiness.class);
        storyAction.setStoryBusiness(storyBusiness);
    }
    
    @Test
    public void testGetStoryContents() {
        storyAction.setStoryId(story.getId());
        
        expect(storyBusiness.retrieveIfExists(story.getId())).andReturn(story);
        expect(storyBusiness.getStoryContents(story)).andReturn(Arrays.asList(new Task(), new Task()));
        replay(storyBusiness);
        
        assertNull(storyAction.getJsonData());
        assertEquals(CRUDAction.AJAX_SUCCESS, storyAction.getStoryContents());
        assertNotNull(storyAction.getJsonData());
        
        verify(storyBusiness);
    }
    
    @Test
    public void testGetStoryContents_nullStory() {
        storyAction.setStoryId(0);
        assertEquals(CRUDAction.AJAX_ERROR, storyAction.getStoryContents());
    }
    
}
