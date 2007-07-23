package fi.hut.soberit.agilefant.db;

import java.sql.Date;

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
	 * 
	 * @param backlogItemId 
	 * @param date inspection time
	 * @return original estimate of work
	 */
	public AFTime getBLIOriginalEstimate(BacklogItem backlogItem, Date date);
}
