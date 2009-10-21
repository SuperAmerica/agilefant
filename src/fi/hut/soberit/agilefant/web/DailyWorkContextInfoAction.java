package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.LinkedList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

@Component("dailyWorkContextInfoAction")
@Scope("prototype")
public class DailyWorkContextInfoAction extends ActionSupport {
    private static final long serialVersionUID = 2599224272717369900L;
    private Task task;
    private int taskId;
    private Iteration iteration;
    private LinkedList<Story> stories = new LinkedList<Story>();
    
    @Autowired
    private TaskBusiness taskBusiness;
    
    public String retrieve() {
        task = taskBusiness.retrieve(taskId);
        
        iteration = task.getIteration();
        if (iteration == null) {
            iteration = (Iteration)(task.getStory().getBacklog());
        }
        
        Story story = task.getStory();
        while (story != null) {
            stories.addFirst(story);
            story = story.getParent();
        }
        
        return Action.SUCCESS;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }
    
    public Task getTask() {
        return task;
    }
    
    public Iteration getIteration() {
        return iteration;
    }
    
    public Collection<Story> getStories() {
        return stories;
    }
}