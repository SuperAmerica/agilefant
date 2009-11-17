package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StoryHierarchyBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

public class StoryHierarchyBusinessTest {

    private StoryHierarchyBusinessImpl storyHierarchyBusiness;
    
    private StoryHierarchyDAO storyHierarchyDAO;
    
    @Before
    public void setUp() {
        storyHierarchyBusiness = new StoryHierarchyBusinessImpl();
        
        storyHierarchyDAO = createStrictMock(StoryHierarchyDAO.class);
        storyHierarchyBusiness.setStoryHierarchyDAO(storyHierarchyDAO);
    }

    private void replayAll() {
        replay(storyHierarchyDAO);
    }

    private void verifyAll() {
        verify(storyHierarchyDAO);
    }

    
    @Test
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
    public void testRetrieveProductRootStories() {
        Product prod = new Product();
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProductRootStories(prod)).
            andReturn(stories);
        replayAll();
        assertSame(stories, storyHierarchyBusiness.retrieveProductRootStories(prod));
        verifyAll();
    }


}
