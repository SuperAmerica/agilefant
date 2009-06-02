package fi.hut.soberit.agilefant.web;

import java.util.Arrays;
import java.util.List;

import org.junit.*;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import static org.junit.Assert.*;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class BacklogContentsActionTest {

    private BacklogContentsAction backlogContentsAction = new BacklogContentsAction();
    StoryBusiness storyBusiness;
    Backlog backlog;
    Story story1;
    Story story2;
    
    @Before
    public void setUp() {
        backlog = new Product();
        story1 = new Story();
        story2 = new Story();
        storyBusiness = createMock(StoryBusiness.class);
        backlogContentsAction.setStoryBusiness(storyBusiness);
    }
    
    @Test
    public void testInitializeContents_interaction() {
        List<Story> storiesList = Arrays.asList(story1, story2);
        expect(storyBusiness.getStoriesByBacklog(backlog)).andReturn(storiesList);
        replay(storyBusiness);
        
        backlogContentsAction.initializeContents(backlog);
        assertEquals(storiesList, backlogContentsAction.getStories());
        
        verify(storyBusiness);
    }
}
