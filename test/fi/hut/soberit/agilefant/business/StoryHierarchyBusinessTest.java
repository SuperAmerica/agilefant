package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.StoryHierarchyBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryHierarchyBusinessTest extends MockedTestCase {

    @TestedBean
    private StoryHierarchyBusinessImpl storyHierarchyBusiness;
    
    @Mock(strict=true)
    private StoryHierarchyDAO storyHierarchyDAO;
    
    @Mock(strict=true)
    private StoryBusiness storyBusiness;
    
    @Test
    @DirtiesContext
    public void testRetrieveProjectLeafStories() {
        Project proj = new Project();
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProjectLeafStories(proj)).
            andReturn(stories);
        replayAll();
        assertSame(stories, storyHierarchyBusiness.retrieveProjectLeafStories(proj));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testRetrieveProjectRootStories() {
        Project proj = new Project();
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProjectRootStories(proj)).
            andReturn(stories);
        replayAll();
        assertSame(stories, storyHierarchyBusiness.retrieveProjectRootStories(proj));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testRetrieveProductRootStories() {
        Product prod = new Product();
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProductRootStories(prod)).
            andReturn(stories);
        replayAll();
        assertSame(stories, storyHierarchyBusiness.retrieveProductRootStories(prod));
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void testMoveUnder() {
        Story story = new Story();
        Story oldParent = new Story();
        Story newParent = new Story();
        
        story.setParent(oldParent);
        oldParent.getChildren().add(story);
        
        expect(storyBusiness.updateStoryRanks(newParent)).andReturn(null);
        expect(storyBusiness.updateStoryRanks(oldParent)).andReturn(null);
        
        replayAll();
        
        storyHierarchyBusiness.moveUnder(story, newParent);
        
        verifyAll();
        
        assertTrue(newParent.getChildren().contains(story));
        assertFalse(oldParent.getChildren().contains(story));
        assertSame(newParent, story.getParent());
    }

}
