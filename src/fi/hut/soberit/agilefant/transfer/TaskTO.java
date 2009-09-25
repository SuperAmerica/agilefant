package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.BeanCopier;
import flexjson.JSON;

public class TaskTO extends Task {
   
    // Helper fields
    private long effortSpent;    

    public TaskTO(Task task) {
        BeanCopier.copy(task, this);
    }
    
    @JSON(include = true)
    public long getEffortSpent() {
        return effortSpent;
    }

    public void setEffortSpent(long effortSpent) {
        this.effortSpent = effortSpent;
    }

}

