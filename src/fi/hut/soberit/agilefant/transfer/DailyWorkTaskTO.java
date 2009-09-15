package fi.hut.soberit.agilefant.transfer;

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
    
    public DailyWorkTaskTO(Task task) {
        BeanCopier.copy(task, this);
    }


    public DailyWorkTaskTO(Task task, TaskClass clazz, int whatsNextRank) {
        BeanCopier.copy(task, this);
        
        this.taskClass = clazz;
        this.workQueueRank = whatsNextRank;
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

    public TaskClass getTaskClass() {
        return taskClass;
    }

    public void setTask(Task task) {
        BeanCopier.copy(task, this);
    }
}
