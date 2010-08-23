package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TaskSplitBusiness;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

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
    
    private List<User> responsibles = new ArrayList<User>();
    
    private Collection<Task> newTasks = new ArrayList<Task>(); 
    
    public String split() {
        original.setResponsibles(new HashSet<User>(responsibles));
        original = taskSplitBusiness.splitTask(original, newTasks);
        return Action.SUCCESS;
    }
    
    public void initializePrefetchedData(int objectId) {
        original = taskBusiness.retrieveDetached(objectId);
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

    public List<User> getResponsibles() {
        return responsibles;
    }

    public void setResponsibles(List<User> responsibles) {
        this.responsibles = responsibles;
    }
}
