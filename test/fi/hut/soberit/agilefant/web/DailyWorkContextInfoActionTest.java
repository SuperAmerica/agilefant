package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

public class DailyWorkContextInfoActionTest {
    private DailyWorkContextInfoAction testable;

    private TaskBusiness taskBusiness;
    private Iteration iteration;
    private Iteration iteration2;
    private Story childStory;
    private Story parentStory;
    private StoryBusiness storyBusiness;
    
    protected static final int TASK_ID = 2;
    protected static final int STORY_ID = 7;
    
    @Before
    public void setUp_dependencies() {
        testable = new DailyWorkContextInfoAction();
        
        taskBusiness = createStrictMock(TaskBusiness.class);
        testable.setTaskBusiness(taskBusiness);

        storyBusiness = createStrictMock(StoryBusiness.class);
        testable.setStoryBusiness(storyBusiness);
        
        testable.setTaskId(TASK_ID);
        
        iteration = new Iteration();
        iteration.setId(123);
        iteration2 = new Iteration();
        iteration2.setId(456);
        childStory = new Story();
        childStory.setBacklog(iteration);
        childStory.setName("child");
        parentStory = new Story();
        parentStory.setBacklog(iteration2);
        parentStory.setName("parent");
        childStory.setParent(parentStory);
    }
    
    private void replayAll() {
        replay(taskBusiness, storyBusiness);
    }

    private void verifyAll() {
        verify(taskBusiness, storyBusiness);
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
        assertSame(task, testable.getItem());
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
        assertEquals(2, testable.getStories().size());
        assertSame(task, testable.getItem());
        
        ArrayList<NamedObjectAndLinkPair> storyLinkPairs = new ArrayList<NamedObjectAndLinkPair>(testable.getStories());
        NamedObjectAndLinkPair obj1 = storyLinkPairs.get(0);
        NamedObjectAndLinkPair obj2 = storyLinkPairs.get(1);

        assertEquals("editIteration.action?iterationId=456", obj1.getLink());
        assertEquals("#story-list-div", obj2.getLink());

        assertSame(parentStory, obj1.getItem());
        assertSame(childStory, obj2.getItem());
        
        assertSame(parentStory.getName(), obj1.getName());
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
        assertEquals(1, testable.getStories().size());
        assertSame(task, testable.getItem());
    }

    @Test
    public void testCreateStoryLink() {
        Story story = new Story();
        
        String link;
        
        link = testable.createStoryLink(story);
        assertEquals("", link);
        
        Product product = new Product();
        product.setId(555);
        story.setBacklog(product);
        link = testable.createStoryLink(story);
        assertEquals("editProduct.action?productId=555", link);
        
        Project project = new Project();
        project.setId(666);
        story.setBacklog(project);
        link = testable.createStoryLink(story);
        assertEquals("editProject.action?projectId=666", link);

        Iteration iteration = new Iteration();
        iteration.setId(777);
        story.setBacklog(iteration);
        link = testable.createStoryLink(story);
        assertEquals("editIteration.action?iterationId=777", link);
    }
    
    @Test
    public void testRetrieve_story() {
        Story story = new Story();
        story.setParent(parentStory);
        story.setBacklog(iteration);

        testable.setTaskId(0);
        testable.setStoryId(STORY_ID);
        expect(storyBusiness.retrieve(STORY_ID)).andReturn(story);
        replayAll();
        
        testable.retrieve();
        
        verifyAll();
        
        assertEquals(iteration, testable.getIteration());
        assertEquals(2, testable.getStories().size());
        assertSame(story, testable.getItem());
    }
}
