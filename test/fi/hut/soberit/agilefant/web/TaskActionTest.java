package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.*;

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
import fi.hut.soberit.agilefant.transfer.TaskTO;

public class TaskActionTest {

    private TaskAction taskAction = new TaskAction();
    private TaskBusiness taskBusiness;
    private TransferObjectBusiness transferObjectBusiness;
    private Task task;
    
    @Before
    public void setUp_dependencies() {
        transferObjectBusiness = createMock(TransferObjectBusiness.class);
        taskAction.setTransferObjectBusiness(transferObjectBusiness);
        
        taskBusiness = createMock(TaskBusiness.class);
        taskAction.setTaskBusiness(taskBusiness);
    }
    
    @Before
    public void setUp() {
        task = new Task();
        taskAction.setTask(task);
        taskAction.setTaskId(task.getId());
    }
    
    /*
     * TEST RETRIEVING.
     */
    
    /*
     * TEST STORING.
     */
    
    @Test
    public void testAjaxStoreTask_newTask() {
        taskAction.setStoryId(null);
        taskAction.setBacklogId(2);
        expect(taskBusiness.storeTask(task, 2, null, taskAction.getUserIds()))
            .andReturn(task);
        expect(transferObjectBusiness.constructTaskTO(task))
            .andReturn(new TaskTO(task));
        replay(taskBusiness, transferObjectBusiness);
        
        assertEquals(Action.SUCCESS, taskAction.store());
        
        verify(taskBusiness, transferObjectBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testAjaxStoreTask_error() {
        taskAction.setBacklogId(2);
        
        expect(taskBusiness.storeTask(task, 2, null, taskAction.getUserIds()))
            .andThrow(new ObjectNotFoundException("Iteration not found"));
        replay(taskBusiness, transferObjectBusiness);
        
        taskAction.store();
        
        verify(taskBusiness, transferObjectBusiness);
    }
    
    /*
     * TEST DELETING
     */
    
    @Test
    public void testDeleteTask() {
       expect(taskBusiness.retrieve(task.getId())).andReturn(task);
       taskBusiness.delete(task.getId());
       replay(taskBusiness);
       
       assertEquals(Action.SUCCESS, taskAction.delete());
       
       verify(taskBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testDeleteTask_noSuchTask() {
        taskAction.setTaskId(-1);
        expect(taskBusiness.retrieve(-1))
            .andThrow(new ObjectNotFoundException());
        replay(taskBusiness);
        
        taskAction.delete();
        
        verify(taskBusiness);
    }
    
    @Test(expected = ConstraintViolationException.class)
    public void testDeleteTask_withHourEntries() {
       expect(taskBusiness.retrieve(task.getId())).andReturn(task);
       taskBusiness.delete(task.getId());
       expectLastCall().andThrow(new ConstraintViolationException("Action not allowed", null, null));
       replay(taskBusiness);
       
       taskAction.delete();
       
       verify(taskBusiness);
    }
    
    /*
     * TEST MOVING
     */
    @Test
    public void testMoveTask_toStory() {
        Story story = new Story();
        story.setId(3);
        
        taskAction.setStoryId(story.getId());
        taskAction.setBacklogId(null);
        taskAction.setTaskId(task.getId());
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, null, story.getId())).andReturn(task);
        expect(transferObjectBusiness.constructTaskTO(task)).andReturn(new TaskTO(task));
        
        replay(taskBusiness, transferObjectBusiness);

        assertEquals(Action.SUCCESS, taskAction.move());
        assertTrue(taskAction.getTask() instanceof TaskTO);
        
        verify(taskBusiness, transferObjectBusiness);
    }
    
    @Test
    public void testMoveTask_toIteration() {
        Iteration iter = new Iteration();
        iter.setId(333);
        
        taskAction.setStoryId(null);
        taskAction.setBacklogId(iter.getId());
        taskAction.setTaskId(task.getId());
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, iter.getId(), null)).andReturn(task);
        expect(transferObjectBusiness.constructTaskTO(task)).andReturn(new TaskTO(task));
        
        replay(taskBusiness, transferObjectBusiness);

        assertEquals(Action.SUCCESS, taskAction.move());
        assertTrue(taskAction.getTask() instanceof TaskTO);
        
        verify(taskBusiness, transferObjectBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testMoveTask_noSuchTask() {
        taskAction.setTaskId(-1);
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replay(taskBusiness);
        
        taskAction.move();
        
        verify(taskBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testMoveTask_bothIdsGiven() {
        taskAction.setTaskId(task.getId());
        taskAction.setStoryId(123);
        taskAction.setBacklogId(1233);
        
        expect(taskBusiness.retrieve(task.getId())).andReturn(task);
        expect(taskBusiness.move(task, 1233, 123)).andThrow(new ObjectNotFoundException());
        replay(taskBusiness);
        
        taskAction.move();
        
        verify(taskBusiness);
    }
    
    
    
    /*
     * TEST PREFETCHING
     */
    
    @Test
    public void testInitializePrefetchedData_happyCase() {
        Task expected = new Task();
        expect(taskBusiness.retrieve(123)).andReturn(expected);
        replay(taskBusiness);
        
        taskAction.initializePrefetchedData(123);
        assertEquals(expected, taskAction.getTask());
        
        verify(taskBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testInitializePrefetchedData_objectNotFound() {
        expect(taskBusiness.retrieve(-1)).andThrow(new ObjectNotFoundException());
        replay(taskBusiness);
        
        taskAction.initializePrefetchedData(-1);
        
        verify(taskBusiness);
    }
}
