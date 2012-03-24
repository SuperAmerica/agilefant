package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.LabelBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryRankBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.ChildHandlingChoice;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;
import fi.hut.soberit.agilefant.util.TaskHandlingChoice;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryActionTest extends MockedTestCase {

    @TestedBean
    StoryAction storyAction;
    
    @Mock
    StoryBusiness storyBusiness;
    
    @Mock
    LabelBusiness labelBusiness;
    
    @Mock
    BacklogBusiness backlogBusiness;
    
    @Mock
    StoryRankBusiness storyRankBusiness;
    
    Story story;
    Iteration iter;
    

    @Before
    public void setUp() {
        story = new Story();
        story.setId(1234);
        iter = new Iteration();
        iter.setId(6446);
    }

    
    @Test
    @DirtiesContext
    public void testRetrieve() {
        storyAction.setStoryId(story.getId());
        StoryTO storyTo = new StoryTO(story);
        expect(storyBusiness.retrieveStoryWithMetrics(story.getId())).andReturn(storyTo);
       
        replayAll();
        
        assertEquals(Action.SUCCESS, storyAction.retrieve());
        assertEquals(1234, storyAction.getStory().getId());
        
        verifyAll();
    }

    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testRetrieve_noSuchStory() {
        storyAction.setStoryId(-1);
        expect(storyBusiness.retrieveStoryWithMetrics(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        storyAction.retrieve();
        
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testCreate() {
        Story returnedStory = new Story();
        returnedStory.setName("Tested story");
        returnedStory.setBacklog(new Project());
        List<String> labelNames = new ArrayList<String>();
        
        StoryRank rank = new StoryRank();
        rank.setRank(222);
        
        expect(storyBusiness.create(storyAction.getStory(), storyAction.getBacklogId(), 0, storyAction.getUserIds(), labelNames)).andReturn(returnedStory);
        expect(storyRankBusiness.getRankByBacklog(returnedStory, returnedStory.getBacklog())).andReturn(rank);
        
        replayAll();
        storyAction.setLabelNames(labelNames);
        assertEquals(Action.SUCCESS, storyAction.create());
        verifyAll();
        
        assertTrue(storyAction.getStory() instanceof StoryTO);
        assertSame("Tested story", storyAction.getStory().getName());
        assertEquals(new Integer(222), ((StoryTO)storyAction.getStory()).getRank());
    }
    
    
    @Test
    @DirtiesContext
    public void testStore() {
        storyAction.setStory(story);
        storyAction.setStoryId(story.getId());
        storyAction.setUsersChanged(false);
        storyAction.setTasksToDone(true);
        
        expect(storyBusiness.store(story.getId(), story, null, null, true))
                .andReturn(story);
        replayAll();
        assertEquals("success_withTasks", storyAction.store());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testStore_changeResponsibles() {
        storyAction.setStory(story);
        storyAction.setStoryId(story.getId());
        storyAction.setUsersChanged(true);
        storyAction.setTasksToDone(false);
        
        expect(storyBusiness.store(story.getId(), story, null, storyAction.getUserIds(), false))
                .andReturn(story);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.store());
        verifyAll();
    }
    
        
    @Test
    @DirtiesContext
    public void testMoveStory() {
        storyAction.setStoryId(story.getId());
        storyAction.setBacklogId(iter.getId());
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        expect(backlogBusiness.retrieve(iter.getId())).andReturn(iter);
        storyBusiness.moveStoryAway(story, iter);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.moveStoryAway());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testSaveMoveSignleStory() {
        storyAction.setStoryId(story.getId());
        storyAction.setBacklogId(iter.getId());
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        expect(backlogBusiness.retrieve(iter.getId())).andReturn(iter);
        storyBusiness.moveSingleStoryToBacklog(story, iter);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.safeMoveSingleStory());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testMoveStoryAndChildren() {
        storyAction.setStoryId(story.getId());
        storyAction.setBacklogId(iter.getId());
        
        expect(storyBusiness.retrieve(story.getId())).andReturn(story);
        expect(backlogBusiness.retrieve(iter.getId())).andReturn(iter);
        storyBusiness.moveStoryAndChildren(story, iter);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.moveStoryAndChildren());
        verifyAll();
    }

    /*
     * TEST DELETION
     */
    
    @Test
    @DirtiesContext
    public void testDelete() {
       storyAction.setStoryId(story.getId());
       storyBusiness.deleteAndUpdateHistory(story.getId(), null, null, null, null);
       replayAll();
       
       assertEquals(Action.SUCCESS, storyAction.delete());
       
       verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testDelete_withChoices() {
       storyAction.setStoryId(story.getId());
       storyAction.setTaskHandlingChoice(TaskHandlingChoice.MOVE);
       storyAction.setStoryHourEntryHandlingChoice(HourEntryHandlingChoice.MOVE);
       storyAction.setTaskHourEntryHandlingChoice(HourEntryHandlingChoice.DELETE);
       storyAction.setChildHandlingChoice(ChildHandlingChoice.DELETE);
       storyBusiness.deleteAndUpdateHistory(story.getId(), TaskHandlingChoice.MOVE, HourEntryHandlingChoice.MOVE, HourEntryHandlingChoice.DELETE, ChildHandlingChoice.DELETE);
       replayAll();
       
       assertEquals(Action.SUCCESS, storyAction.delete());
       
       verifyAll();
    }

    @Test
    @DirtiesContext
    public void testInitializePrefetchingData() {
        Story newStory = new Story();
        newStory.setId(2222);
        expect(storyBusiness.retrieveDetached(newStory.getId())).andReturn(newStory);
        
        replayAll();
        
        storyAction.initializePrefetchedData(newStory.getId());
        assertEquals(newStory, storyAction.getStory());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testInitializePrefetchingData_noSuchStory() {
        expect(storyBusiness.retrieveDetached(-1)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        
        storyAction.initializePrefetchedData(-1);
        
        verifyAll();
    }
    
    
    @Test
    @DirtiesContext
    public void testRankUnder() {
        storyAction.setStoryId(123);
        storyAction.setTargetStoryId(666);
        storyAction.setBacklogId(222);
        
        Story lower = new Story();
        Story upper = new Story();
        Story returned = new Story();
        
        expect(storyBusiness.retrieve(123)).andReturn(lower);
        expect(storyBusiness.retrieveIfExists(666)).andReturn(upper);
        expect(backlogBusiness.retrieveIfExists(222)).andReturn(iter);
        expect(storyBusiness.rankStoryUnder(lower, upper, iter)).andReturn(returned);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.rankStoryUnder());
        verifyAll();
        
        assertEquals(returned, storyAction.getStory());
    }
    
    @Test
    @DirtiesContext
    public void testRankOver() {
        storyAction.setStoryId(123);
        storyAction.setTargetStoryId(666);
        storyAction.setBacklogId(222);
        
        Story targetStory = new Story();
        Story story = new Story();
        Story returned = new Story();
        
        expect(storyBusiness.retrieve(123)).andReturn(story);
        expect(storyBusiness.retrieveIfExists(666)).andReturn(targetStory);
        expect(backlogBusiness.retrieveIfExists(222)).andReturn(iter);
        expect(storyBusiness.rankStoryOver(story, targetStory, iter)).andReturn(returned);
        
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.rankStoryOver());
        verifyAll();
        
        assertEquals(returned, storyAction.getStory());
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testRankUnder_notFound() {
        storyAction.setStoryId(-1);
        expect(storyBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        storyAction.rankStoryUnder();
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testRankStoryToTop() {
        Story returned = new Story();
        
        storyAction.setStoryId(123);
        storyAction.setBacklogId(222);
        expect(storyBusiness.retrieve(123)).andReturn(story);
        expect(backlogBusiness.retrieve(222)).andReturn(iter);
        expect(storyBusiness.rankStoryToTop(story, iter)).andReturn(returned);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.rankStoryToTop());
        verifyAll();
        assertSame(returned, storyAction.getStory());
    }
    
    @Test
    @DirtiesContext
    public void testRankStoryToBottom() {
        Story returned = new Story();
        
        storyAction.setStoryId(123);
        storyAction.setBacklogId(222);
        expect(storyBusiness.retrieve(123)).andReturn(story);
        expect(backlogBusiness.retrieve(222)).andReturn(iter);
        expect(storyBusiness.rankStoryToBottom(story, iter)).andReturn(returned);
        replayAll();
        assertEquals(Action.SUCCESS, storyAction.rankStoryToBottom());
        verifyAll();
        assertSame(returned, storyAction.getStory());
    }
    
    @Test
    @DirtiesContext
    public void testCreateStoryUnder() {
        Story data = new Story();
        Story res = new Story();
        Set<Integer> userIds = Collections.emptySet();
        List<String> labelNames = new ArrayList<String>();
        
        expect(storyBusiness.createStoryUnder(1, data, userIds, labelNames)).andReturn(res);
        replayAll();
        storyAction.setLabelNames(labelNames);
        storyAction.setStory(data);
        storyAction.setStoryId(1);
        storyAction.createStoryUnder();
        verifyAll();
        storyAction.setLabelNames(labelNames);
    }
    
    @Test
    @DirtiesContext
    public void testCreateStorySibling() {
        Story data = new Story();
        Story res = new Story();
        Set<Integer> userIds = Collections.emptySet();
        List<String> labelNames = new ArrayList<String>();
        
        expect(storyBusiness.createStorySibling(1, data, userIds, labelNames)).andReturn(res);
        replayAll();
        storyAction.setStory(data);
        storyAction.setStoryId(1);
        storyAction.setLabelNames(labelNames);
        storyAction.createStorySibling();
        verifyAll();
        assertEquals(res, storyAction.getStory());
    }
    
}
