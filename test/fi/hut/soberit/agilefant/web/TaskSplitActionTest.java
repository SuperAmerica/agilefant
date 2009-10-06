package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TaskSplitBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

public class TaskSplitActionTest {

    TaskSplitAction testable;
    
    TaskBusiness taskBusiness;
    
    TaskSplitBusiness taskSplitBusiness;
    
    @Before
    public void setUp_dependencies() {
        testable = new TaskSplitAction();
        
        taskBusiness = createStrictMock(TaskBusiness.class);
        testable.setTaskBusiness(taskBusiness);
        
        taskSplitBusiness = createStrictMock(TaskSplitBusiness.class);
        testable.setTaskSplitBusiness(taskSplitBusiness);
    }
    
    private void replayAll() {
        replay(taskBusiness, taskSplitBusiness);
    }
    
    private void verifyAll() {
        verify(taskBusiness, taskSplitBusiness);
    }    
    
    @Test
    public void testSplit() {
        testable.setOriginalTaskId(123);
        Task task = new Task();
        testable.setOriginal(task);
        expect(taskSplitBusiness.splitTask(task, testable.getNewTasks())).andReturn(task);
        
        replayAll();
        assertEquals(Action.SUCCESS, testable.split());
        verifyAll();
    }
    
    @Test
    public void testPrefetch() {
        Task task = new Task();
        expect(taskBusiness.retrieve(123)).andReturn(task);
        replayAll();
        testable.setOriginalTaskId(123);
        testable.initializePrefetchedData(123);
        verifyAll();
        assertEquals(task, testable.getOriginal());
    }
}
