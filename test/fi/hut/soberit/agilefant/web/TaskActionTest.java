package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class TaskActionTest extends MockedTestCase {

    @TestedBean
    private TaskAction testable;
    
    @Mock
    private TaskBusiness taskBusiness;
    
    @Mock
    private TransferObjectBusiness transferObjectBusiness;
    
    @Mock
    private StoryHierarchyBusiness storyHierarchyBusiness;
    
    private Task task;
    private User user2;
    private User user1;
    
    
    @Before
    public void setUp() {
        user1 = new User();
        user1.setId(1);
        
        user2 = new User();
        user2.setId(2);
        
        task = new Task();
        task.setId(444);
        testable.setTask(task);
        testable.setTaskId(task.getId());
    }
    
    private void expectPopulateJsonData() {
        expect(transferObjectBusiness.constructTaskTO(task)).andReturn(new TaskTO(task));
    }
    

    
    
    /*
     * TEST RETRIEVING.
     */
    @Test
    @DirtiesContext
    public void testRetrieve() {
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expectPopulateJsonData();
        replayAll();
        
        assertEquals(Action.SUCCESS, testable.retrieve());
        assertTrue(testable.getTask() instanceof TaskTO);
        
        verifyAll(); 
    }

    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testRetrieve_noSuchTask() {
        testable.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        testable.retrieve();
        
        verifyAll();
    }
    
    
    /*
     * TEST STORING.
     */
    
    @Test
    @DirtiesContext
    public void testAjaxStoreTask_newTask() {
        testable.setStoryId(null);
        testable.setIterationId(2);
        testable.setResponsiblesChanged(true);
        expect(taskBusiness.storeTask(task, 2, null))
            .andReturn(task);
        expectPopulateJsonData();
        replayAll();
        
        assertEquals(Action.SUCCESS, testable.store());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testAjaxStoreTask_error() {
        testable.setIterationId(2);
        testable.setResponsiblesChanged(true);
        
        expect(taskBusiness.storeTask(task, 2, null))
            .andThrow(new ObjectNotFoundException("Iteration not found"));
        replayAll();
        
        testable.store();
        
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testStoreTask_dontUpdateUsers() {
        testable.setResponsiblesChanged(false);
        testable.setIterationId(2);
        testable.setNewResponsibles(new HashSet<User>(Arrays.asList(user1, user2)));
        
        expect(taskBusiness.storeTask(task, 2, null))
            .andReturn(task);
        
        expectPopulateJsonData();
        
        replayAll();
        testable.store();
        verifyAll();
        
        assertEquals(0, task.getResponsibles().size());
    }
    
    @Test
    @DirtiesContext
    public void testStoreTask_updateUsers() {
        testable.setResponsiblesChanged(true);
        testable.setIterationId(2);
        testable.setNewResponsibles(new HashSet<User>(Arrays.asList(user1, user2)));
        
        expect(taskBusiness.storeTask(task, 2, null))
            .andReturn(task);
        
        expectPopulateJsonData();
        
        replayAll();
        testable.store();
        verifyAll();
        
        assertEquals(2, task.getResponsibles().size());
    }
    
    /*
     * TEST DELETING
     */
    
    @Test
    @DirtiesContext
    public void testDeleteTask() {
       taskBusiness.deleteAndUpdateHistory(task.getId(), null);
       replayAll();
       
       assertEquals(Action.SUCCESS, testable.delete());
       
       verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testDeleteTask_noSuchTask() {
        testable.setTaskId(-1);
        taskBusiness.deleteAndUpdateHistory(-1, null);
        expectLastCall().andThrow(new ObjectNotFoundException());        
        replayAll();
        
        testable.delete();
        
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testDeleteTask_moveChoice() {
       testable.setHourEntryHandlingChoice(HourEntryHandlingChoice.MOVE);
       taskBusiness.deleteAndUpdateHistory(task.getId(), HourEntryHandlingChoice.MOVE);
       replayAll();
       
       assertEquals(Action.SUCCESS, testable.delete());
       
       verifyAll();
    }

    @Test
    @DirtiesContext
    public void testDeleteTask_deleteChoice() {
       testable.setHourEntryHandlingChoice(HourEntryHandlingChoice.DELETE);
       taskBusiness.deleteAndUpdateHistory(task.getId(), HourEntryHandlingChoice.DELETE);
       replayAll();
       
       assertEquals(Action.SUCCESS, testable.delete());
       
       verifyAll();
    }
    
    /*
     * TEST MOVING
     */
    @Test
    @DirtiesContext
    public void testMoveTask_toStory() {
        Story story = new Story();
        story.setId(3);
        
        testable.setStoryId(story.getId());
        testable.setIterationId(null);
        testable.setTaskId(task.getId());
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, null, story.getId())).andReturn(task);
        expectPopulateJsonData();
        
        replayAll();

        assertEquals(Action.SUCCESS, testable.move());
        assertTrue(testable.getTask() instanceof TaskTO);
        
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testMoveTask_toIteration() {
        Iteration iter = new Iteration();
        iter.setId(333);
        
        testable.setStoryId(null);
        testable.setIterationId(iter.getId());
        testable.setTaskId(task.getId());
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, iter.getId(), null)).andReturn(task);
        expectPopulateJsonData();
        
        replayAll();

        assertEquals(Action.SUCCESS, testable.move());
        assertTrue(testable.getTask() instanceof TaskTO);
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testMoveTask_noSuchTask() {
        testable.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        testable.move();
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testMoveTask_bothIdsGiven() {
        testable.setTaskId(task.getId());
        testable.setStoryId(123);
        testable.setIterationId(1233);
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, 1233, 123)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        testable.move();
        
        verifyAll();
    }
    
    /*
     * TEST RESETING ORIGINAL ESTIMATE
     */
    @Test
    @DirtiesContext
    public void testResetOriginalEstimate() {
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.resetOriginalEstimate(task.getId())).andReturn(task);
        expectPopulateJsonData();
        replayAll();
        
        assertEquals(Action.SUCCESS, testable.resetOriginalEstimate());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testResetOriginalEstimate_noSuchTask() {
        testable.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        testable.resetOriginalEstimate();
        
        verifyAll();
    }
    
    
    /*
     * TEST PREFETCHING
     */
    
    @Test
    @DirtiesContext
    public void testInitializePrefetchedData_happyCase() {
        Task expected = new Task();
        expect(taskBusiness.retrieve(123)).andReturn(expected);
        replayAll();
        
        testable.initializePrefetchedData(123);
        assertEquals(expected, testable.getTask());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testInitializePrefetchedData_objectNotFound() {
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        testable.initializePrefetchedData(-1);
        
        verifyAll();
    }
    
    /*
     * TEST RANKING 
     */
    @Test
    @DirtiesContext
    public void testRankUnder_noParentChange() {
        testable.setTaskId(222);
        testable.setRankUnderId(651);
        testable.setIterationId(null);
        testable.setStoryId(null);
        
        Task returned = new Task();
        
        expect(taskBusiness.retrieve(222)).andReturn(task);
        expect(taskBusiness.retrieveIfExists(651)).andReturn(null);
        expect(taskBusiness.rankAndMove(task, null, null, null))
            .andReturn(returned);
        //expect(taskBusiness.rankUnderTask(task, null)).andReturn(returned);
        
        replayAll();
        
        assertEquals(Action.SUCCESS, testable.rankUnder());
        assertSame(returned, testable.getTask());
        
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testRankUnder_iteration() {
        
    }
    
    @Test(expected = ObjectNotFoundException.class)
    @DirtiesContext
    public void testRankUnder_objectNotFound() {
        testable.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        
        testable.rankUnder();
        
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testDeleteTaskForm() {
        testable.setTaskId(10);
        expect(taskBusiness.retrieve(10)).andReturn(task);
        
        replayAll();
        
        assertEquals(Action.SUCCESS, testable.deleteTaskForm());
        
        verifyAll();
    }
    
    /*
     * TEST CONTEXT FETCHING
     */
    @Test
    @DirtiesContext
    public void testGetTaskContext_underStory() {
        Story parent = new Story();
        StoryTO parentTo = new StoryTO(parent);
        task.setStory(parent);
        task.setIteration(null);
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(storyHierarchyBusiness.recurseHierarchy(parent)).andReturn(parentTo);
        
        replayAll();
        assertEquals(Action.SUCCESS, testable.getTaskContext());
        verifyAll();
        
        assertEquals(parentTo, testable.getParentStory());
    }
    
    @Test
    @DirtiesContext
    public void testGetTaskContext_underIteration() {
        task.setStory(null);
        task.setIteration(new Iteration());
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        
        replayAll();
        assertEquals(Action.SUCCESS, testable.getTaskContext());
        verifyAll();
        
        assertNull(testable.getParentStory());
    }
}
