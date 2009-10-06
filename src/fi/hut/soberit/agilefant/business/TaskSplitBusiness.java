package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Task;

public interface TaskSplitBusiness {
    /**
     * Split a task and create new tasks accordingly.
     * 
     * @param original original task to split
     * @param newTasks the new tasks to create
     * @return the split task
     */
    public Task splitTask(Task original, Collection<Task> newTasks);
}
