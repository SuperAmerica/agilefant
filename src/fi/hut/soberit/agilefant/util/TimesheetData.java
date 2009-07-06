package fi.hut.soberit.agilefant.util;

import java.util.Collection;
import java.util.HashMap;

import java.util.Map;

import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.transfer.BacklogTimesheetNode;
import fi.hut.soberit.agilefant.transfer.StoryTimesheetNode;
import fi.hut.soberit.agilefant.transfer.TaskTimesheetNode;

public class TimesheetData {
    private Map<Integer, BacklogTimesheetNode> backlogNodes;
    private Map<Integer, StoryTimesheetNode> storyNodes;
    private Map<Integer, TaskTimesheetNode> taskNode;
    
    public TimesheetData() {
        this.backlogNodes = new HashMap<Integer, BacklogTimesheetNode>();
        this.storyNodes = new HashMap<Integer, StoryTimesheetNode>();
        this.taskNode = new HashMap<Integer, TaskTimesheetNode>();
    }

    public void addEntry(BacklogHourEntry entry) {
        int backlogId = entry.getBacklog().getId();
        if(this.backlogNodes.get(backlogId) == null) {
            BacklogTimesheetNode node = new BacklogTimesheetNode(entry.getBacklog());
            this.backlogNodes.put(backlogId, node);
        }
        this.backlogNodes.get(backlogId).addHourEntry(entry);
    }
    public void addEntry(StoryHourEntry entry) {
        int storyId = entry.getStory().getId();
        if(this.storyNodes.get(storyId) == null) {
            StoryTimesheetNode node = new StoryTimesheetNode(entry.getStory());
            this.storyNodes.put(storyId, node);
        }
        this.storyNodes.get(storyId).addHourEntry(entry);

    }
    public void addEntry(TaskHourEntry entry) {
        int taskId = entry.getTask().getId();
        if(this.taskNode.get(taskId) == null) {
            TaskTimesheetNode node = new TaskTimesheetNode(entry.getTask());
            this.taskNode.put(taskId, node);
        }
        this.taskNode.get(taskId).addHourEntry(entry);
    }
    
    public void addNode(BacklogTimesheetNode node) {
        this.backlogNodes.put(node.getId(), node);
    }
    
    public void addNode(StoryTimesheetNode node) {
        this.storyNodes.put(node.getId(), node);
    }
    
    public void addNode(TaskTimesheetNode node) {
        this.taskNode.put(node.getId(), node);
    }
    public BacklogTimesheetNode getBacklogNode(int backlogId) {
        return this.backlogNodes.get(backlogId);
    }
    public StoryTimesheetNode getStoryNode(int storyId) {
        return this.storyNodes.get(storyId);
    }
    public TaskTimesheetNode getTaskNode(int taskId) {
        return this.taskNode.get(taskId);
    }
    
    public Collection<BacklogTimesheetNode> getBacklogNodes() {
        return this.backlogNodes.values();
    }
    public Collection<StoryTimesheetNode> getStoryNodes() {
        return this.storyNodes.values();
    }
    public Collection<TaskTimesheetNode> getTaskNodes() {
        return this.taskNode.values();
    }
}
