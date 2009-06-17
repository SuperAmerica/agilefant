package fi.hut.soberit.agilefant.util;

import java.util.List;

import fi.hut.soberit.agilefant.model.Task;

public class TaskTimesheetNode extends TimesheetNode {
    Task task;

    
    public TaskTimesheetNode(Task task) {
        super();
        this.task = task;
    }
    @Override
    public List<? extends TimesheetNode> getChildren() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean hasChildren() {
        // TODO Auto-generated method stub
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
