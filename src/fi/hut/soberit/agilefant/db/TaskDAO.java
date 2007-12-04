package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.State;

/**
 * Interface for a DAO of a Task.
 * 
 * @see GenericDAO
 */
public interface TaskDAO extends GenericDAO<Task> {

    /**
     * Get all tasks of the given backlog item, which have one of the given
     * states.
     * 
     * @param bli
     *                backlog item, tasks of which to find
     * @param states
     *                array of accepted states
     * @return all tasks matching the criteria
     */
    public Collection<Task> getTasksByStateAndBacklogItem(BacklogItem bli,
            State[] states);

    /**
     * Get all tasks, which have one of the given states.
     * 
     * @param states
     *                array of accepted states
     * @return all tasks matching the criteria
     */
    public Collection<Task> getTasksByState(State[] states);
}
