package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.StoryTreeBranchMetrics;
import fi.hut.soberit.agilefant.util.StoryFilters;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

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
        StoryTO expected = new StoryTO(story);
        storyHierarchyAction.setStoryId(123);
        
        expect(storyBusiness.retrieve(123)).andReturn(story);
        expect(storyHierarchyBusiness.recurseHierarchy(story)).andReturn(expected);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.recurseHierarchyAsList());
        verifyAll();

        assertEquals(expected, storyHierarchyAction.getTopmostStory());
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
    
//    @Test
//    @DirtiesContext
//    public void testMoveUnder_integrityErrorThrown() {
//        Story story = new Story();
//        Story story2 = new Story();
//        expect(storyBusiness.retrieve(1)).andReturn(story);
//        expect(storyBusiness.retrieve(2)).andReturn(story2);
//        
//        storyHierarchyBusiness.moveUnder(story, story2);
//        expectLastCall().andThrow(new StoryTreeIntegrityViolationException(
//                Arrays.asList(new StoryTreeIntegrityMessage(story, story2, StoryHierarchyIntegrityViolationType.PARENT_DEEPER_IN_HIERARCHY))));
//        
//        storyHierarchyAction.setStoryId(1);
//        storyHierarchyAction.setReferenceStoryId(2);
//        
//        replayAll();
//        assertEquals(Action.ERROR, storyHierarchyAction.moveStoryUnder());
//        verifyAll();
//        
//        assertEquals(1, storyHierarchyAction.getIntegrityErrors().size());
//    }
    
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
        expect(storyHierarchyBusiness.retrieveProductRootStories(EasyMock.eq(123), EasyMock.isA(StoryFilters.class)))
            .andReturn(stories);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyHierarchyAction.retrieveProductRootStories());
        verifyAll();
        
        assertEquals(stories, storyHierarchyAction.getStories());
    }
    
    @Test
    @DirtiesContext
    public void testRetrieveBranchMetrics() {
        Story story = new Story();
        StoryTreeBranchMetrics metrics = new StoryTreeBranchMetrics();
        expect(storyBusiness.retrieve(55)).andReturn(story);
        expect(storyHierarchyBusiness.calculateStoryTreeMetrics(story)).andReturn(metrics);
        
        replayAll();
        this.storyHierarchyAction.setStoryId(55);
        assertEquals(Action.SUCCESS, this.storyHierarchyAction.retrieveBranchMetrics());
        verifyAll();
        assertEquals(metrics, storyHierarchyAction.getBranchMetrics());
    }

}
