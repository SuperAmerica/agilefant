package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryHierarchyActionTest extends MockedTestCase {
    
    @TestedBean
    private StoryHierarchyAction storyHierarchyAction;
    
    @Mock(strict=true)
    private StoryBusiness storyBusiness;
    
    @Mock
    private StoryHierarchyBusiness storyHierarchyBusiness;
    
    @Test
    @DirtiesContext
    public void testRecurseHierarchyAsList() {
        Story story = new Story();
        Story parent = new Story();
        Story ancestor = new Story();
        parent.setParent(ancestor);
        story.setParent(parent);
        storyHierarchyAction.setStoryId(123);
        
        expect(storyBusiness.retrieve(123)).andReturn(story);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.recurseHierarchyAsList());
        verifyAll();
        
        assertEquals(3, storyHierarchyAction.getHierarchy().size());
        assertSame(ancestor, storyHierarchyAction.getHierarchy().get(0));
        assertSame(parent, storyHierarchyAction.getHierarchy().get(1));
        assertSame(story, storyHierarchyAction.getHierarchy().get(2));
        
        assertSame(story, storyHierarchyAction.getStory());
    }
    
    @Test
    @DirtiesContext
    public void testRecurseHierarchyAsList_noParent() {
        Story story = new Story();
        storyHierarchyAction.setStoryId(123);
        
        expect(storyBusiness.retrieve(123)).andReturn(story);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.recurseHierarchyAsList());
        verifyAll();
        
        assertEquals(1, storyHierarchyAction.getHierarchy().size());
        assertSame(story, storyHierarchyAction.getHierarchy().get(0));
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testRecurseHierarchyAsList_noSuchStory() {
        storyHierarchyAction.setStoryId(-1);
        
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        storyHierarchyAction.recurseHierarchyAsList();
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testMoveUnder() {
        Story story = new Story();
        expect(storyBusiness.retrieve(1)).andReturn(story);
        expect(storyBusiness.retrieve(2)).andReturn(story);
        storyHierarchyBusiness.moveUnder(story, story);
        
        storyHierarchyAction.setStoryId(1);
        storyHierarchyAction.setReferenceStoryId(2);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.moveStoryUnder());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testMoveBefore() {
        Story story = new Story();
        expect(storyBusiness.retrieve(1)).andReturn(story);
        expect(storyBusiness.retrieve(2)).andReturn(story);
        storyHierarchyBusiness.moveBefore(story, story);
        
        storyHierarchyAction.setStoryId(1);
        storyHierarchyAction.setReferenceStoryId(2);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.moveStoryBefore());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testMoveAfter() {
        Story story = new Story();
        expect(storyBusiness.retrieve(1)).andReturn(story);
        expect(storyBusiness.retrieve(2)).andReturn(story);
        storyHierarchyBusiness.moveAfter(story, story);
        
        storyHierarchyAction.setStoryId(1);
        storyHierarchyAction.setReferenceStoryId(2);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.moveStoryAfter());
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void testRetrieveProductRootStories() {
        List<Story> stories = new ArrayList<Story>(Arrays.asList(new Story()));
        
        storyHierarchyAction.setProductId(123);
        expect(storyHierarchyBusiness.retrieveProductRootStories(123))
            .andReturn(stories);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.retrieveProductRootStories());
        verifyAll();
        
        assertEquals(stories, storyHierarchyAction.getStories());
    }

}
