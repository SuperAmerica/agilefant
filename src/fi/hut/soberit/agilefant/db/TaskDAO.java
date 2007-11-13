package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskStatus;

/**
 * Interface for a DAO of a Task.
 * 
 * @see GenericDAO
 */
public interface TaskDAO extends GenericDAO<Task> {

	/**
	 * Get all tasks of the given backlog item, which have one of the given
	 * statuses.
	 * 
	 * @param bli
	 *            backlog item, tasks of which to find
	 * @param statuses
	 *            array of accepted statuses
	 * @return all tasks matching the criteria
	 */
	public Collection<Task> getTasksByStatusAndBacklogItem(BacklogItem bli,
			TaskStatus[] statuses);

	/**
	 * Get all tasks, which have one of the given statuses.
	 * 
	 * @param statuses
	 *            array of accepted statuses
	 * @return all tasks matching the criteria
	 */
	public Collection<Task> getTasksByStatus(TaskStatus[] statuses);
}
