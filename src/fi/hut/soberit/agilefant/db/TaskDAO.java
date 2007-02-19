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
	
	public Collection<Task> getTasksByStatusAndBacklogItem(BacklogItem bli, TaskStatus[] statuses);
}
