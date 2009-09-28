package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Task;

public class DailyWorkTaskTO extends TaskTO {
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
        super(task);
    }
    
    public DailyWorkTaskTO(Task task, TaskClass taskClass, int workQueueRank) {
        super(task);
        this.taskClass = taskClass;
        this.workQueueRank = workQueueRank;
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
}
