package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import org.hibernate.annotations.Type;

/**
 * Hibernate entity bean representing an event where 
 * time estimate for a task is changed.
 * <p>
 * Contains the new estimate.
 * <p>
 * Since the class inherits from TaskComment, 
 * some comment text may accompany the new time estimate.
 *
 * @see fi.hut.soberit.agilefant.model.TaskEvent
 * @see fi.hut.soberit.agilefant.model.TaskComment
 */
@Entity
public class EstimateHistoryEvent extends TaskComment {
	
	private AFTime newEstimate;

	@Type(type="af_time")	
	public AFTime getNewEstimate() {
		return newEstimate;
	}

	public void setNewEstimate(AFTime newEstimate) {
		this.newEstimate = newEstimate;
	}
}
