package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

public class DailyWorkContextInfoActionTest {
    private DailyWorkContextInfoAction testable;

    private TaskBusiness taskBusiness;
    private Iteration iteration;
    private Iteration iteration2;
    private Story childStory;
    private Story parentStory;
    
    protected static final int TASK_ID = 2;
    
    @Before
    public void setUp_dependencies() {
        testable = new DailyWorkContextInfoAction();
        
        taskBusiness = createStrictMock(TaskBusiness.class);
        testable.setTaskBusiness(taskBusiness);
        
        testable.setTaskId(TASK_ID);
        
        iteration = new Iteration();
        iteration2 = new Iteration();
        childStory = new Story();
        childStory.setBacklog(iteration);
        parentStory = new Story();
        parentStory.setBacklog(iteration2);
        childStory.setParent(parentStory);
    }
    
    private void replayAll() {
        replay(taskBusiness);
    }

    private void verifyAll() {
        verify(taskBusiness);
    }
    
    @Test
    public void testRetrieve_iterationTask() {
        Task task = new Task();
        task.setId(2);
        task.setIteration(iteration);
        
        expect(taskBusiness.retrieve(TASK_ID)).andReturn(task);
        replayAll();
        
        testable.retrieve();
        
        verifyAll();
        
        assertSame(iteration, testable.getIteration());
        assertEquals(0, testable.getStories().size());
        assertSame(task, testable.getTask());
    }

    @Test
    public void testRetrieve_childStoryTask() {
        Task task = new Task();
        task.setId(2);
        task.setStory(childStory);
        
        expect(taskBusiness.retrieve(TASK_ID)).andReturn(task);
        replayAll();
        
        testable.retrieve();
        
        verifyAll();
        
        assertEquals(iteration, testable.getIteration());
        assertEquals(Arrays.asList(new Story[] { parentStory, childStory }), testable.getStories());
        assertSame(task, testable.getTask());
    }

    @Test
    public void testRetrieve_parentStoryTask() {
        Task task = new Task();
        task.setId(2);
        task.setStory(parentStory);
        
        expect(taskBusiness.retrieve(TASK_ID)).andReturn(task);
        replayAll();
        
        testable.retrieve();
        
        verifyAll();
        
        assertEquals(iteration2, testable.getIteration());
        assertEquals(Arrays.asList(new Story[] { parentStory }), testable.getStories());
        assertSame(task, testable.getTask());
    }

}
