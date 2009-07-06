package fi.hut.soberit.agilefant.util;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.transfer.BacklogTimesheetNode;
import fi.hut.soberit.agilefant.transfer.StoryTimesheetNode;
import fi.hut.soberit.agilefant.transfer.TaskTimesheetNode;

public class TimesheetDataTest {

    private BacklogHourEntry backlogEntry1 = new BacklogHourEntry();
    private BacklogHourEntry backlogEntry2 = new BacklogHourEntry();
    
    private StoryHourEntry storyEntry1 = new StoryHourEntry();
    private StoryHourEntry storyEntry2 = new StoryHourEntry();;
    
    private TaskHourEntry taskEntry1 = new TaskHourEntry();
    private TaskHourEntry taskEntry2 = new TaskHourEntry();
    
    private TimesheetData tsData;
    
    @Before
    public void resetTsData() {
        Backlog backlog = new Iteration();
        Story story = new Story();
        Task task = new Task();
        
        backlog.setId(1);
        story.setId(1);
        task.setId(1);
        
        this.backlogEntry1.setBacklog(backlog);
        this.backlogEntry2.setBacklog(backlog);
        this.storyEntry1.setStory(story);
        this.storyEntry2.setStory(story);
        this.taskEntry1.setTask(task);
        this.taskEntry2.setTask(task);
        this.tsData = new TimesheetData();
    }
    
    @Test
    public void testAddBacklogEntry_newBacklog() {
        this.tsData.addEntry(this.backlogEntry1);
        TimesheetNode node = this.tsData.getBacklogNode(1);
        assertNotNull(node);
        assertEquals(1, node.getHourEntries().size());
        
    }
    @Test
    public void testAddBacklogEntry_existingBacklog() {
        this.tsData.addEntry(this.backlogEntry1);
        this.tsData.addEntry(this.backlogEntry2);
        TimesheetNode node = this.tsData.getBacklogNode(1);
        assertNotNull(node);
        assertEquals(2, node.getHourEntries().size());
    }
    
    @Test
    public void testAddStoryEntry_newStory() {
        this.tsData.addEntry(this.storyEntry1);
        TimesheetNode node = this.tsData.getStoryNode(1);
        assertNotNull(node);
        assertEquals(1, node.getHourEntries().size());
    }
    @Test
    public void testAddStoryEntry_existingStory() {
        this.tsData.addEntry(this.storyEntry1);
        this.tsData.addEntry(this.storyEntry2);
        TimesheetNode node = this.tsData.getStoryNode(1);
        assertNotNull(node);
        assertEquals(2, node.getHourEntries().size());
    }
    
    @Test
    public void testAddTaskEntry_newTask() {
        this.tsData.addEntry(this.taskEntry1);
        TimesheetNode node = this.tsData.getTaskNode(1);
        assertNotNull(node);
        assertEquals(1, node.getHourEntries().size());
    }
    @Test
    public void testAddTaskEntry_existingTask() {
        this.tsData.addEntry(this.taskEntry1);
        this.tsData.addEntry(this.taskEntry2);
        TimesheetNode node = this.tsData.getTaskNode(1);
        assertNotNull(node);
        assertEquals(2, node.getHourEntries().size());
    }
    
    @Test
    public void addNode_Backlog() {
        BacklogTimesheetNode node = new BacklogTimesheetNode(this.backlogEntry1.getBacklog());
        this.tsData.addNode(node);
        assertNotNull(this.tsData.getBacklogNode(1));
    }    
    
    @Test
    public void addNode_Story() {
        StoryTimesheetNode node = new StoryTimesheetNode(this.storyEntry1.getStory());
        this.tsData.addNode(node);
        assertNotNull(this.tsData.getStoryNode(1));
    }
    
    @Test
    public void addNode_Task() {
        TaskTimesheetNode node = new TaskTimesheetNode(this.taskEntry1.getTask());
        this.tsData.addNode(node);
        assertNotNull(this.tsData.getTaskNode(1));
    }
    
}
