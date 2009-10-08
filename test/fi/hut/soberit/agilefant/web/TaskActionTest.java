package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.TaskTO;

public class TaskActionTest {

    private TaskAction taskAction = new TaskAction();
    private TaskBusiness taskBusiness;
    private TransferObjectBusiness transferObjectBusiness;
    private Task task;
    private User user1;
    private User user2;
    
    @Before
    public void setUp_dependencies() {
        transferObjectBusiness = createMock(TransferObjectBusiness.class);
        taskAction.setTransferObjectBusiness(transferObjectBusiness);
        
        taskBusiness = createMock(TaskBusiness.class);
        taskAction.setTaskBusiness(taskBusiness);
        user1 = new User();
        user1.setId(1);
        user2 = new User();
        user2.setId(2);
    }
    
    private void replayAll() {
        replay(transferObjectBusiness, taskBusiness);
    }
    
    private void verifyAll() {
        verify(transferObjectBusiness, taskBusiness);
    }
    
    @Before
    public void setUp() {
        task = new Task();
        task.setId(444);
        taskAction.setTask(task);
        taskAction.setTaskId(task.getId());
        task.setResponsibles(Arrays.asList(user1));
    }
    
    private void expectPopulateJsonData() {
        expect(transferObjectBusiness.constructTaskTO(task)).andReturn(new TaskTO(task));
    }
    
    /*
     * TEST RETRIEVING.
     */
    @Test
    public void testRetrieve() {
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expectPopulateJsonData();
        replayAll();
        
        assertEquals(Action.SUCCESS, taskAction.retrieve());
        assertTrue(taskAction.getTask() instanceof TaskTO);
        
        verifyAll(); 
    }

    
    @Test(expected = ObjectNotFoundException.class)
    public void testRetrieve_noSuchTask() {
        taskAction.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        taskAction.retrieve();
        
        verifyAll();
    }
    
    
    /*
     * TEST STORING.
     */
    
    @Test
    public void testAjaxStoreTask_newTask() {
        taskAction.setStoryId(null);
        taskAction.setIterationId(2);

        expect(taskBusiness.storeTask(task, 2, null))
            .andReturn(task);
        expectPopulateJsonData();
        replayAll();
        
        assertEquals(Action.SUCCESS, taskAction.store());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testAjaxStoreTask_error() {
        taskAction.setIterationId(2);
        
        expect(taskBusiness.storeTask(task, 2, null))
            .andThrow(new ObjectNotFoundException("Iteration not found"));
        replayAll();
        
        taskAction.store();
        
        verifyAll();
    }
    
    @Test
    public void testStoreTask_clearUsers() {
        taskAction.setUsersCleared(true);
        taskAction.setIterationId(2);
        
        expect(taskBusiness.storeTask(task, 2, null))
            .andReturn(task);
        
        expectPopulateJsonData();
        
        replayAll();
        taskAction.store();
        verifyAll();
        
        assertEquals(0, task.getResponsibles().size());
    }
    
    @Test
    public void testStoreTask_updateUsers() {
        taskAction.setIterationId(2);
        task.setResponsibles(Arrays.asList(user1, user2));
        
        expect(taskBusiness.storeTask(task, 2, null))
            .andReturn(task);
        
        expectPopulateJsonData();
        
        replayAll();
        taskAction.store();
        verifyAll();
    }
    
    /*
     * TEST DELETING
     */
    
    @Test
    public void testDeleteTask() {
       expect(taskBusiness.retrieve(task.getId())).andReturn(task);
       taskBusiness.delete(task.getId());
       replayAll();
       
       assertEquals(Action.SUCCESS, taskAction.delete());
       
       verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteTask_noSuchTask() {
        taskAction.setTaskId(-1);
        expect(taskBusiness.retrieve(-1))
            .andThrow(new ObjectNotFoundException());
        replayAll();
        
        taskAction.delete();
        
        verifyAll();
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void testDeleteTask_withHourEntries() {
       expect(taskBusiness.retrieve(task.getId())).andReturn(task);
       taskBusiness.delete(task.getId());
       expectLastCall().andThrow(new ConstraintViolationException("Action not allowed", null, null));
       replayAll();
       
       taskAction.delete();
       
       verifyAll();
    }
    
    /*
     * TEST MOVING
     */
    @Test
    public void testMoveTask_toStory() {
        Story story = new Story();
        story.setId(3);
        
        taskAction.setStoryId(story.getId());
        taskAction.setIterationId(null);
        taskAction.setTaskId(task.getId());
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, null, story.getId())).andReturn(task);
        expectPopulateJsonData();
        
        replay(taskBusiness, transferObjectBusiness);

        assertEquals(Action.SUCCESS, taskAction.move());
        assertTrue(taskAction.getTask() instanceof TaskTO);
        
        verifyAll();
    }
    
    @Test
    public void testMoveTask_toIteration() {
        Iteration iter = new Iteration();
        iter.setId(333);
        
        taskAction.setStoryId(null);
        taskAction.setIterationId(iter.getId());
        taskAction.setTaskId(task.getId());
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, iter.getId(), null)).andReturn(task);
        expectPopulateJsonData();
        
        replay(taskBusiness, transferObjectBusiness);

        assertEquals(Action.SUCCESS, taskAction.move());
        assertTrue(taskAction.getTask() instanceof TaskTO);
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testMoveTask_noSuchTask() {
        taskAction.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        taskAction.move();
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testMoveTask_bothIdsGiven() {
        taskAction.setTaskId(task.getId());
        taskAction.setStoryId(123);
        taskAction.setIterationId(1233);
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, 1233, 123)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        taskAction.move();
        
        verifyAll();
    }
    
    /*
     * TEST RESETING ORIGINAL ESTIMATE
     */
    @Test
    public void testResetOriginalEstimate() {
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.resetOriginalEstimate(task.getId())).andReturn(task);
        expectPopulateJsonData();
        replayAll();
        
        assertEquals(Action.SUCCESS, taskAction.resetOriginalEstimate());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testResetOriginalEstimate_noSuchTask() {
        taskAction.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        taskAction.resetOriginalEstimate();
        
        verifyAll();
    }
    
    
    /*
     * TEST PREFETCHING
     */
    
    @Test
    public void testInitializePrefetchedData_happyCase() {
        Task expected = new Task();
        expect(taskBusiness.retrieve(123)).andReturn(expected);
        replayAll();
        
        taskAction.initializePrefetchedData(123);
        assertEquals(expected, taskAction.getTask());
        
        verifyAll();
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testInitializePrefetchedData_objectNotFound() {
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replayAll();
        
        taskAction.initializePrefetchedData(-1);
        
        verifyAll();
    }
    
    /*
     * TEST RANKING 
     */
    @Test
    public void testRankUnder_noParentChange() {
        taskAction.setTaskId(222);
        taskAction.setRankUnderId(651);
        taskAction.setIterationId(null);
        taskAction.setStoryId(null);
        
        Task returned = new Task();
        
        expect(taskBusiness.retrieve(222)).andReturn(task);
        expect(taskBusiness.retrieveIfExists(651)).andReturn(null);
        expect(taskBusiness.rankAndMove(task, null, null, null))
            .andReturn(returned);
        //expect(taskBusiness.rankUnderTask(task, null)).andReturn(returned);
        
        replayAll();
        
        assertEquals(Action.SUCCESS, taskAction.rankUnder());
        assertSame(returned, taskAction.getTask());
        
        verifyAll();
    }
    
    @Test
    public void testRankUnder_iteration() {
        
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testRankUnder_objectNotFound() {
        taskAction.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        
        replayAll();
        
        taskAction.rankUnder();
        
        verifyAll();
    }
}
