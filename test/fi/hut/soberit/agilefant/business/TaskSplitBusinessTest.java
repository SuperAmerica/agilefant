package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.TaskSplitBusinessImpl;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public class TaskSplitBusinessTest {
    TaskSplitBusinessImpl testable;

    TaskDAO taskDAO;

    TaskBusiness taskBusiness;

    Iteration iteration;

    Task originalTask;

    List<Task> newTasks;
    List<Task> createdTasks;
    
    
    Story parentStory;
    

    @Before
    public void setUp_dependencies() {
        testable = new TaskSplitBusinessImpl();

        taskDAO = createStrictMock(TaskDAO.class);
        testable.setTaskDAO(taskDAO);

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
        verify(taskDAO, taskBusiness);
    }

    private void replayAll() {
        replay(taskDAO, taskBusiness);
    }

    @Test
    public void testSplitTask_iterationTask() {
        originalTask.setIteration(iteration);
        originalTask.setStory(null);
        
        createChildTasks();
        
        replayAll();
        testable.splitTask(originalTask, newTasks);
        verifyAll();
    }

    @Test
    public void testSplitTask_storyTask() {
        originalTask.setIteration(null);
        originalTask.setStory(parentStory);

        createChildTasks();
        
        replayAll();
        testable.splitTask(originalTask, newTasks);
        verifyAll();
    }

    private void createChildTasks() {
        expect(taskDAO.create(newTasks.get(1))).andReturn(4);
        expect(taskDAO.get(4)).andReturn(createdTasks.get(1));
        expect(taskBusiness.rankUnderTask(createdTasks.get(1), originalTask))
            .andReturn(createdTasks.get(1));
        
        expect(taskDAO.create(newTasks.get(0))).andReturn(3);
        expect(taskDAO.get(3)).andReturn(createdTasks.get(0));
        expect(taskBusiness.rankUnderTask(createdTasks.get(0), originalTask))
            .andReturn(createdTasks.get(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitTask_emptyList() {
        testable.splitTask(new Task(), new ArrayList<Task>());
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
