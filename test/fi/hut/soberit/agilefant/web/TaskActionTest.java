package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.transfer.TaskTO;

public class TaskActionTest {

    private TaskAction taskAction = new TaskAction();
    private TaskBusiness taskBusiness;
    private TransferObjectBusiness transferObjectBusiness;
    private Task task;
    
    @Before
    public void setUp() {
        task = new Task();
        taskAction.setTask(task);
        taskAction.setBacklogId(2);
        
        transferObjectBusiness = createMock(TransferObjectBusiness.class);
        taskAction.setTransferObjectBusiness(transferObjectBusiness);
        
        taskBusiness = createMock(TaskBusiness.class);
        taskAction.setTaskBusiness(taskBusiness);
    }
    
    @Test
    public void testAjaxStoreTask_newTask() {
        expect(taskBusiness.storeTask(task, 2, 0, taskAction.getUserIds())).andReturn(task);
//        expect(taskBusiness.getTaskResponsibles(task)).andReturn(null);
        expect(transferObjectBusiness.constructTaskTO(task))
            .andReturn(new TaskTO(task));
        replay(taskBusiness, transferObjectBusiness);
        
        assertEquals(CRUDAction.AJAX_SUCCESS, taskAction.ajaxStoreTask());
        
        verify(taskBusiness, transferObjectBusiness);
    }
    
    @Test(expected = ObjectNotFoundException.class)
    public void testAjaxStoreTask_error() {
        expect(taskBusiness.storeTask(task, 2, 0, taskAction.getUserIds()))
            .andThrow(new ObjectNotFoundException("Iteration not found"));
        replay(taskBusiness, transferObjectBusiness);
        
        taskAction.ajaxStoreTask();
        
        verify(taskBusiness, transferObjectBusiness);
    }
    
    @Test
    public void testAjaxDeleteTask() {
       taskBusiness.delete(task.getId());
       replay(taskBusiness);
       
       assertEquals(CRUDAction.AJAX_SUCCESS, taskAction.ajaxDeleteTask());
       
       verify(taskBusiness);
    }
}
