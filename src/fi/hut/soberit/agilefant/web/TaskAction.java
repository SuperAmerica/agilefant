package fi.hut.soberit.agilefant.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.model.Task;

@Component("taskAction")
@Scope("prototype")
public class TaskAction extends ActionSupport {

    private static final long serialVersionUID = 7699657599039468223L;
    private Task task;

    public String create() {
        setTask(new Task());
        return Action.SUCCESS;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task getTask() {
        return task;
    }
}
