package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.transfer.BacklogTimesheetNode;
import fi.hut.soberit.agilefant.transfer.StoryTimesheetNode;
import fi.hut.soberit.agilefant.transfer.TaskTimesheetNode;
import fi.hut.soberit.agilefant.util.TimesheetData;

@Service("timesheetBusiness")
@Transactional
public class TimesheetBusinessImpl implements TimesheetBusiness {
    
    @Autowired
    private HourEntryDAO hourEntryDAO;
    
    public long getRootNodeSum(List<BacklogTimesheetNode> nodes) {
        if(nodes == null) {
            return 0L;
        }
        long sum = 0;
        for(BacklogTimesheetNode node : nodes) {
            sum += node.getEffortSum();
        }
        return sum;
    }
    public List<BacklogTimesheetNode> findRootNodes(TimesheetData sheetData) {
        List<BacklogTimesheetNode> rootNodes = new ArrayList<BacklogTimesheetNode>();
        for(BacklogTimesheetNode node : sheetData.getBacklogNodes()) {
            if(node.getBacklog() instanceof Product) {
                node.calculateEffortSum();
                rootNodes.add(node);
            }
        }
        return rootNodes;
    }
    public List<BacklogTimesheetNode> getRootNodes(Set<Integer> backlogIds, DateTime startDate, DateTime endDate, Set<Integer> userIds) {
        TimesheetData sheetData = this.generateTimesheet(backlogIds, startDate, endDate, userIds);
        return this.findRootNodes(sheetData);
    }
    public TimesheetData generateTimesheet(Set<Integer> backlogIds, DateTime startDate, DateTime endDate, Set<Integer> userIds) {
        TimesheetData sheetData = this.getUnlinkedTimesheetData(backlogIds, startDate, endDate, userIds);
        this.linkTasks(sheetData);
        this.linkStories(sheetData);
        this.linkBacklogs(sheetData);
        return sheetData;
    }
    protected TimesheetData getUnlinkedTimesheetData(Set<Integer> backlogIds, DateTime startDate, DateTime endDate, Set<Integer> userIds) {
        TimesheetData sheetData = new TimesheetData();
        List<BacklogHourEntry> backlogEntries = this.hourEntryDAO.getBacklogHourEntriesByFilter(backlogIds, startDate, endDate, userIds);
        List<StoryHourEntry> storyEntries = this.hourEntryDAO.getStoryHourEntriesByFilter(backlogIds, startDate, endDate, userIds);
        List<TaskHourEntry> taskEntries = this.hourEntryDAO.getTaskHourEntriesByFilter(backlogIds, startDate, endDate, userIds);
        
        for(BacklogHourEntry entry : backlogEntries) {
            sheetData.addEntry(entry);
        }
        
        for(StoryHourEntry entry : storyEntries) {
            sheetData.addEntry(entry);
        }
        
        for(TaskHourEntry entry : taskEntries) {
            sheetData.addEntry(entry);
        }
        return sheetData;
    }
    
    protected void attachTaskNodeToStoryNode(TimesheetData sheetData, TaskTimesheetNode taskNode) {
        Story story = taskNode.getTask().getStory();
        if(story == null) {
            return;
        }
        int storyId = story.getId();

        StoryTimesheetNode parentNode = sheetData.getStoryNode(storyId);
        
        if(parentNode != null && parentNode.getChildren().contains(taskNode)) {
            return;
        }
        if(parentNode == null) {
            parentNode = new StoryTimesheetNode(story);
            sheetData.addNode(parentNode);
        }
        parentNode.addChild(taskNode);
    }
    
    protected void attachTaskNodeToIterationNode(TimesheetData sheetData, TaskTimesheetNode taskNode) {
        Iteration iteration = taskNode.getTask().getIteration();
        if(iteration == null) {
            return;
        }
        
       int iterationId = iteration.getId();
       BacklogTimesheetNode parentNode = sheetData.getBacklogNode(iterationId);
       
       if(parentNode != null && parentNode.getTaskNodes().contains(taskNode)) {
           return;
       }
       
       if(parentNode == null) {
           parentNode = new BacklogTimesheetNode(iteration);
           sheetData.addNode(parentNode);
       } 
       parentNode.addChild(taskNode);
    }
    protected void attachStoryNodeToBacklogNode(TimesheetData sheetData, StoryTimesheetNode storyNode) {
        Backlog backlog = storyNode.getStory().getBacklog();
        if(backlog == null) {
            return;
        }
        
        int backlogId = backlog.getId();
        BacklogTimesheetNode parentNode = sheetData.getBacklogNode(backlogId);
        
        if(parentNode != null && parentNode.getStoryNodes().contains(storyNode)) {
            return;
        }
        
        if(parentNode == null) {
            parentNode = new BacklogTimesheetNode(backlog);
            sheetData.addNode(parentNode);
        }
        parentNode.addChild(storyNode);
    }
    
    protected void attachBacklogNodeToBacklogNode(TimesheetData sheetData, BacklogTimesheetNode backlogNode) {
        Backlog parentBacklog = backlogNode.getBacklog().getParent();
        if(parentBacklog == null) {
            return;
        }
        
        int backlogId = parentBacklog.getId();
        
        BacklogTimesheetNode parentNode = sheetData.getBacklogNode(backlogId);
        
        if(parentNode != null && parentNode.getBacklogNodes().contains(backlogNode)) {
            return;
        }
        if(parentNode == null) {
            parentNode = new BacklogTimesheetNode(parentBacklog);
            sheetData.addNode(parentNode);
        }
        parentNode.addChild(backlogNode);
    }
    
    protected void linkTasks(TimesheetData sheetData) {
        Collection<TaskTimesheetNode> taskNodes = sheetData.getTaskNodes();
        
        for(TaskTimesheetNode node : taskNodes) {
            Story parentStory = node.getTask().getStory();
            //directly under an iteration
            if(parentStory == null) {
                this.attachTaskNodeToIterationNode(sheetData, node);
            } else {
                this.attachTaskNodeToStoryNode(sheetData, node);
            }
        }
    }
    protected void linkStories(TimesheetData sheetData) {
        Collection<StoryTimesheetNode> storyNodes = sheetData.getStoryNodes();
        
        for(StoryTimesheetNode node : storyNodes) {
            this.attachStoryNodeToBacklogNode(sheetData, node);
        }
    }
    
    protected void  linkBacklogs(TimesheetData sheetData) {
        Collection<BacklogTimesheetNode> backlogNodes = new ArrayList<BacklogTimesheetNode>();
        backlogNodes.addAll(sheetData.getBacklogNodes());
        for(BacklogTimesheetNode node : backlogNodes) {
            if(node.getBacklog() instanceof Iteration) {
                this.attachBacklogNodeToBacklogNode(sheetData, node);
            }
        }
        backlogNodes.addAll(sheetData.getBacklogNodes());
        for(BacklogTimesheetNode node : backlogNodes) {
            if(node.getBacklog() instanceof Project) {
                this.attachBacklogNodeToBacklogNode(sheetData, node);
            }
        }
        backlogNodes.addAll(sheetData.getBacklogNodes());
        for(BacklogTimesheetNode node : backlogNodes) {
            if(node.getBacklog() instanceof Product) {
                this.attachBacklogNodeToBacklogNode(sheetData, node);
            }
        }
    }
    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.hourEntryDAO = hourEntryDAO;
    }

}
