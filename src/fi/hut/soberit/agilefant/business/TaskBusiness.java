package fi.hut.soberit.agilefant.business;

import java.util.Set;

import fi.hut.soberit.agilefant.model.Task;

public interface TaskBusiness extends GenericBusiness<Task> {

    /**
     * Populates and stores a task.
     * @param storyId the parent story's id, or zero if none.
     * @return the newly stored task
     */
    public Task storeTask(Task task, int iterationId, int storyId, Set<Integer> userIds);
}
