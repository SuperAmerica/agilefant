package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Collection;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.util.BeanCopier;
import flexjson.JSON;

public class TaskTO extends Task {
   
    // Helper fields
    private long effortSpent;    

    public TaskTO() {};
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
    
    @JSON(include=false)
    public Collection<User> getWorkingOnTask() {
        ArrayList<User> returned = new ArrayList<User>();
        for (WhatsNextEntry e: getWhatsNextEntries()) {
            returned.add(e.getUser());
        }
        
        return returned;
    }
}

