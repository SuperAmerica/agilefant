package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.TaskSplitBusinessImpl;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public class TaskSplitBusinessTest {
    TaskSplitBusinessImpl testable;

    TaskBusiness taskBusiness;

    Iteration iteration;

    Task originalTask;

    List<Task> newTasks;
    List<Task> createdTasks;
    
    
    Story parentStory;
    

    @Before
    public void setUp_dependencies() {
        testable = new TaskSplitBusinessImpl();

        taskBusiness = createStrictMock(TaskBusiness.class);
        testable.setTaskBusiness(taskBusiness);
    }

    @Before
    public void setUp_data() {
        iteration = new Iteration();
        iteration.setId(3);

        parentStory = new Story();
        parentStory.setId(5);
        parentStory.setBacklog(iteration);
        
        originalTask = new Task();
        originalTask.setId(1);
        originalTask.setRank(2);

        User responsible = new User();
        originalTask.getResponsibles().add(responsible);

        newTasks = new ArrayList<Task>();
        newTasks.add(new Task());
        newTasks.add(new Task());
        
        createdTasks = new ArrayList<Task>();
        createdTasks.add(new Task());
        createdTasks.add(new Task());
    }

    private void verifyAll() {
        verify(taskBusiness);
    }

    private void replayAll() {
        replay(taskBusiness);
    }

    @Test
    public void testSplitTask_iterationTask() {
        originalTask.setIteration(iteration);
        originalTask.setStory(null);
        
        createChildTasks(null, 3);
        
        replayAll();
        testable.splitTask(originalTask, newTasks);
        verifyAll();
    }

    @Test
    public void testSplitTask_storyTask() {
        originalTask.setIteration(null);
        originalTask.setStory(parentStory);

        createChildTasks(5, null);
        
        replayAll();
        testable.splitTask(originalTask, newTasks);
        verifyAll();
    }

    private void createChildTasks(Integer storyId, Integer iterationId) {
        expect(taskBusiness.storeTask(newTasks.get(0), iterationId, storyId, null))
            .andReturn(createdTasks.get(0));
        expect(taskBusiness.rankUnderTask(createdTasks.get(0), originalTask))
            .andReturn(createdTasks.get(0));
        
        expect(taskBusiness.storeTask(newTasks.get(1), iterationId, storyId, null))
            .andReturn(createdTasks.get(1));
        expect(taskBusiness.rankUnderTask(createdTasks.get(1), originalTask))
            .andReturn(createdTasks.get(1));
    }

    public void testSplitTask_successWithEmptyList() {
        testable.splitTask(originalTask, new ArrayList<Task>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitTask_nullOriginal() {
        testable.splitTask(null, Arrays.asList(new Task()));
    }
    
    @Test(expected = RuntimeException.class)
    public void testSplitTask_originalNotPersisted() {
        testable.splitTask(new Task(), Arrays.asList(new Task()));
    }
}
