package fi.hut.soberit.agilefant.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;

public class TimesheetExportRowDataTest {

    private TimesheetExportRowData testable;
    private Iteration iteration;
    private Project project;
    private Product product;
    private Story story;
    private Task task;
    
    @Before
    public void setUp() {
        testable = null;
        product = new Product();
        project = new Project();
        iteration = new Iteration();
        story = new Story();
        task = new Task();
        
        project.setParent(product);
        iteration.setParent(project);
        story.setBacklog(iteration);
    }
    
    private void backlogCompare(Backlog bl1, Backlog bl2, Backlog bl3) {
        assertEquals(bl1, this.testable.getIteration());
        assertEquals(bl2, this.testable.getProject());
        assertEquals(bl3, this.testable.getProduct());
    }
    private void storyTaskCompare(Story story, Task task) {
        assertEquals(story, testable.getStory());
        assertEquals(task, testable.getTask());
    }
    @Test
    public void testAddIterationTask() {
        TaskHourEntry entry = new TaskHourEntry();
        this.task.setIteration(this.iteration);
        entry.setTask(this.task);
        this.testable = new TimesheetExportRowData(entry);
        this.backlogCompare(iteration, project, product);
        this.storyTaskCompare(null, task);
    }
    
    @Test
    public void testAddTask() {
        TaskHourEntry entry = new TaskHourEntry();
        this.task.setStory(this.story);
        entry.setTask(this.task);
        this.testable = new TimesheetExportRowData(entry);
        this.backlogCompare(iteration, project, product);
        this.storyTaskCompare(story, task);

    }
    
    @Test
    public void testAddStory() {
        StoryHourEntry entry = new StoryHourEntry();
        entry.setStory(this.story);
        this.testable = new TimesheetExportRowData(entry);
        this.backlogCompare(iteration, project, product);
        this.storyTaskCompare(story, null);

    }
    
    @Test
    public void testAddIterationEntry() {
        BacklogHourEntry entry = new BacklogHourEntry();
        entry.setBacklog(this.iteration);
        this.testable = new TimesheetExportRowData(entry);
        this.backlogCompare(iteration, project, product);
        this.storyTaskCompare(null, null);

    }
    
    @Test
    public void testAddIterationProject() {
        BacklogHourEntry entry = new BacklogHourEntry();
        entry.setBacklog(this.project);
        this.testable = new TimesheetExportRowData(entry);
        this.backlogCompare(null, project, product);
        this.storyTaskCompare(null, null);

    }
    
    @Test
    public void testAddIterationProduct() {
        BacklogHourEntry entry = new BacklogHourEntry();
        entry.setBacklog(this.product);
        this.testable = new TimesheetExportRowData(entry);
        this.backlogCompare(null, null, product);
        this.storyTaskCompare(null, null);

    }
}
