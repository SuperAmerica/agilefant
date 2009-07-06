package fi.hut.soberit.agilefant.transfer;

import java.util.Collections;
import java.util.List;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.TimesheetNode;

public class TaskTimesheetNode extends TimesheetNode {
    Task task;

    
    public TaskTimesheetNode(Task task) {
        super();
        this.task = task;
    }
    @Override
    public List<? extends TimesheetNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return this.task.getName();
    }

    @Override
    public boolean getHasChildren() {
        return false;
    }
    @Override
    public int getId() {
        return task.getId();
    }
    
    public Task getTask() {
        return this.task;
    }

}
