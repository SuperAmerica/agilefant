package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class DailyWorkTaskTO extends Task {
    public enum TaskClass {
        CURRENT,
        NEXT,
    }
    
    private TaskClass taskClass;
    private int whatsNextRank;
    
    public DailyWorkTaskTO() {
        
    }

    public DailyWorkTaskTO(Task task, TaskClass clazz, int whatsNextRank) {
        BeanCopier.copy(task, this);
        
        this.taskClass = clazz;
        this.whatsNextRank = whatsNextRank;
    }
    

    public int getWhatsNextRank() {
        return whatsNextRank;
    }

    public void setWhatsNextRank(int whatsNextRank) {
        this.whatsNextRank = whatsNextRank;
    }


    public void setTaskClass(TaskClass clazz) {
        this.taskClass = clazz;
    }

    public TaskClass getTaskClass() {
        return taskClass;
    }
}
