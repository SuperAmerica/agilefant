package fi.hut.soberit.agilefant.db;

import java.util.Date;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.TaskEvent;

/**
 * Interface for a DAO of a Practice.
 * 
 * @see GenericDAO
 */
public interface TaskEventDAO extends GenericDAO<TaskEvent> {

	/**
	 * Returns the original estimate of a backlog item in the time period
	 * starting form the given startDate. <br/> If backlog item was created
	 * before the startDate the original estimate is the last effort estimate
	 * before the startDate. <br/> If backlog item was created after the
	 * startDate the original estimate is the first effort estimate given to the
	 * backlog item.
	 * 
	 * @param backlogItem
	 *            the backlog item for which we want the original estimate for
	 * @param date
	 *            the start date of the period we want the original estimate for
	 * @return the original estimate of the backlog item
	 */
	public AFTime getBLIOriginalEstimate(BacklogItem backlogItem, Date date);

	/**
	 * Returns the sum of original estimates of a backlog item's tasks in the
	 * time period starting form the given startDate. <br/> If backlog item was
	 * created before the startDate the original estimate is the last effort
	 * estimate before the startDate. <br/> If backlog item was created after
	 * the startDate the original estimate is the first effort estimate given to
	 * the backlog item.
	 * 
	 * @param backlogItem
	 *            the backlog item for which we want the task estimate sum for
	 * @param date
	 *            the start date of the period we want the task estimate sum for
	 * @return the original task sum estimate of the backlog item
	 */
	public AFTime getTaskSumOrigEst(BacklogItem backlogItem, Date date);
}
