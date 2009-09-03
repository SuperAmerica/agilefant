package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class DailyWorkTaskTO extends Task {
    public enum TaskClass {
        CURRENT,
        NEXT,
    }
    
    private TaskClass taskClass;
    
    public DailyWorkTaskTO(Task task, TaskClass clazz) {
        BeanCopier.copy(task, this);
        
        this.taskClass = clazz;
    }

    public void setTaskClass(TaskClass clazz) {
        this.taskClass = clazz;
    }

    public TaskClass getTaskClass() {
        return taskClass;
    }
}
