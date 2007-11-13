package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;

/**
 * Interface for a DAO of a BacklogItem.
 * 
 * @see GenericDAO
 */
public interface BacklogItemDAO extends GenericDAO<BacklogItem> {

	/**
	 * Returns the list of all tasks of the given backlog item excluding the
	 * placeholder task.
	 * 
	 * @param backlogItemId
	 *            the ID of the backlog item the task belong to
	 * @return list of all tasks excluding the placeholder task
	 */
	public List<Task> getRealTasks(BacklogItem backlogItem);

	/**
	 * Returns the effort left value of the backlog item. Calculated by summing
	 * effort left values of all tasks including the placeholder task. The
	 * values are taken from <strong>task</strong>, not from effort history.
	 * 
	 * @param backlogItem
	 *            the backlog item we want the effort left value for
	 * @return the effort left value for the backlog item
	 */
	public AFTime getBLIEffortLeft(BacklogItem backlogItem);

	/**
	 * Returns the effort left value of the backlog item. Calculated by summing
	 * effort left values of all tasks except the placeholder task. The values
	 * are taken from <strong>task</strong>, not from effort history.
	 * 
	 * @param backlogItem
	 *            the backlog item we want the task sum effort left value for
	 * @return the task sum effort left value for the backlog item
	 */
	public AFTime getTaskSumEffortLeft(BacklogItem backlogItem);
}
