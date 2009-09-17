package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class DailyWorkTaskTO extends Task {
    public enum TaskClass {
        ASSIGNED,
        NEXT,
        NEXT_ASSIGNED
    }
    
    private TaskClass taskClass;
    private int workQueueRank;
    private int backlogId;
    private int parentStoryId;
    private String contextName = "";
    
    public DailyWorkTaskTO(Task task) {
        BeanCopier.copy(task, this);

        setupContext();
    }

    public DailyWorkTaskTO(Task task, TaskClass clazz, int whatsNextRank) {
        BeanCopier.copy(task, this);
        
        this.taskClass = clazz;
        this.workQueueRank = whatsNextRank;
        
        setupContext();
    }
    
    private void setupContext() {
        Backlog backlog = null;
        Story story = getStory();
        
        if (story != null) {
            parentStoryId = story.getId();
            
            backlog = story.getBacklog();
            if (backlog != null) {
                this.contextName  = "" + String.valueOf(backlog.getName()) + "> " + String.valueOf(story.getName());
                backlogId = backlog.getId();
            }
        }
        else {
            backlog = getIteration();
            if (backlog != null) {
                this.contextName  = "" + String.valueOf(backlog.getName());
                backlogId = backlog.getId();
            }
        }
    }

    public int getWorkQueueRank() {
        return workQueueRank;
    }

    public void setWorkQueueRank(int workQueueRank) {
        this.workQueueRank = workQueueRank;
    }

    public void setTaskClass(TaskClass clazz) {
        this.taskClass = clazz;
    }

    public int getBacklogId() {
        return this.backlogId;
    }
    
    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }
    
    public int getParentStoryId() {
        return parentStoryId;
    }

    public void setParentStoryId(int parentStoryId) {
        this.parentStoryId = parentStoryId;
    }
    
    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }

    public TaskClass getTaskClass() {
        return taskClass;
    }

    public void setTask(Task task) {
        BeanCopier.copy(task, this);
    }
}
