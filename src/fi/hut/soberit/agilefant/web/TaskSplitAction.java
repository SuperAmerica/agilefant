package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TaskSplitBusiness;
import fi.hut.soberit.agilefant.model.Task;

@Component("taskSplitAction")
@Scope("prototype")
public class TaskSplitAction implements Prefetching {

    @Autowired
    private TaskBusiness taskBusiness;

    @Autowired
    private TaskSplitBusiness taskSplitBusiness;

    @SuppressWarnings("unused")
    @PrefetchId
    private int originalTaskId;
   
    private Task original;
    
    private Collection<Task> newTasks = new ArrayList<Task>(); 
    
    public String split() {
        taskSplitBusiness.splitTask(original, newTasks);
        return Action.SUCCESS;
    }
    
    public void initializePrefetchedData(int objectId) {
        original = taskBusiness.retrieve(objectId);
    }

    /* GETTERS AND SETTERS */
    public Task getOriginal() {
        return original;
    }

    public void setOriginal(Task original) {
        this.original = original;
    }

    public Collection<Task> getNewTasks() {
        return newTasks;
    }

    public void setNewTasks(Collection<Task> newTasks) {
        this.newTasks = newTasks;
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }

    public void setTaskSplitBusiness(TaskSplitBusiness taskSplitBusiness) {
        this.taskSplitBusiness = taskSplitBusiness;
    }
    
    public void setOriginalTaskId(int originalTaskId) {
        this.originalTaskId = originalTaskId;
    }
}
