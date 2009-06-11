package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.Set;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

public interface TaskBusiness extends GenericBusiness<Task> {

    /**
     * Populates and stores a task.
     * @param storyId the parent story's id, or zero if none.
     * @return the newly stored task
     */
    public Task storeTask(Task task, int iterationId, int storyId, Set<Integer> userIds);
    
    public Collection<ResponsibleContainer> getTaskResponsibles(Task task);

    public Task resetOriginalEstimate(int taskId);

    public Task move(int taskId, int iterationId, int storyId);
}
