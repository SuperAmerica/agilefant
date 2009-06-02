package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.*;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Task;

public class TaskActionTest {

    private TaskAction taskAction = new TaskAction();
    private TaskBusiness taskBusiness;
    private Task task;
    
    @Before
    public void setUp() {
        task = new Task();
        taskBusiness = createMock(TaskBusiness.class);
        taskAction.setTaskBusiness(taskBusiness);
        taskAction.setTask(task);
        taskAction.setBacklogId(2);
    }
    
    @Test
    public void testAjaxStoreTask_newTask() {
        expect(taskBusiness.storeTask(task, 2, 0, taskAction.getUserIds())).andReturn(task);
        replay(taskBusiness);
        
        assertEquals(CRUDAction.AJAX_SUCCESS, taskAction.ajaxStoreTask());
        
        verify(taskBusiness);
    }
    
    @Test
    public void testAjaxStoreTask_error() {
        expect(taskBusiness.storeTask(task, 2, 0, taskAction.getUserIds()))
            .andThrow(new ObjectNotFoundException("Iteration not found"));
        replay(taskBusiness);
        
        assertEquals(CRUDAction.AJAX_ERROR, taskAction.ajaxStoreTask());
        
        verify(taskBusiness);
    }
}
