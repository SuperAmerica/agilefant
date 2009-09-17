package fi.hut.soberit.agilefant.web.context;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

public class TaskContextLinkGeneratorTest {
    Task task;
    Iteration iteration;
    Story story;
    ContextLinkGenerator<Task> testable;
    
    @Before
    public void setup() {
        task = new Task();
        task.setId(1);
        
        iteration = new Iteration();
        iteration.setId(3);
        
        story = new Story();
        story.setId(2);
        story.setBacklog(iteration);
    }
    
    @Test
    public void testCreateLink_forIterationTask() {
        task.setIteration(iteration);
        // <Task>
        testable = ContextLinkGeneratorFactory.getInstance().getContextLinkGenerator(Task.class);
        assertNotNull(testable);
        
        testable.setObject(task);
        assertEquals("editIteration.action?iterationId=3#taskId=1", testable.createLink());
    }

    @Test
    public void testCreateLink_forStoryTask() {
        task.setStory(story);
        // <Task>
        testable = ContextLinkGeneratorFactory.getInstance().getContextLinkGenerator(Task.class);
        assertNotNull(testable);
        
        testable.setObject(task);
        assertEquals("editIteration.action?iterationId=3#storyId=2&taskId=1", testable.createLink());
    }
}
