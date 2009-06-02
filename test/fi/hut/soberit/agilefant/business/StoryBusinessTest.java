package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.*;

import fi.hut.soberit.agilefant.business.impl.StoryBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import static org.junit.Assert.*;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class StoryBusinessTest {

    StoryBusinessImpl storyBusiness = new StoryBusinessImpl();
    StoryDAO storyDAO;
    Backlog backlog;
    Story story1;
    Story story2;
    
    @Before
    public void setUp() {
        backlog = new Product();
        story1 = new Story();
        story2 = new Story();
        storyDAO = createMock(StoryDAO.class);
        storyBusiness.setStoryDAO(storyDAO);
    }
    
    @Test
    public void testGetStoriesByBacklog() {
        List<Story> storiesList = Arrays.asList(story1, story2);
        expect(storyDAO.getStoriesByBacklog(backlog)).andReturn(storiesList);
        replay(storyDAO);
        
        assertSame(storiesList, storyBusiness.getStoriesByBacklog(backlog));
        
        verify(storyDAO);
    }
    
    @Test
    public void testGetStoryContents() {
        assertNull(storyBusiness.getStoryContents(story1));
        fail("Not yet implemented");
    }
}
