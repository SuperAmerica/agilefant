package fi.hut.soberit.agilefant.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

/**
 * Hibernate entity bean representing an event where 
 * some work effort is logged to a task. Since a PerformWork
 * bean is also a (inherits from) EstimateHistoryEvent, it also
 * carries information of a change of effort estimate. 
 * <p>
 * Contains amount of effort done, work type and date.
 * <p>
 * Since the class inherits from TaskComment, 
 * some comment text may accompany the new time estimate.
 *
 * @see fi.hut.soberit.agilefant.model.TaskEvent
 * @see fi.hut.soberit.agilefant.model.TaskComment
 * @see fi.hut.soberit.agilefant.model.EstimateHistoryEvent
 */
@Entity
public class PerformedWork extends EstimateHistoryEvent {
	
	private AFTime effort;
	private WorkType workType;
	private Date workDate;
	
	public PerformedWork() {}

	public PerformedWork(User actor, Task task, Date created, AFTime newEstimate, AFTime effort, Date workDate) {
		super(actor, task, created, newEstimate);
		this.effort = effort;
		this.workDate = workDate;
	}

	/** Work type */
	@ManyToOne
	public WorkType getWorkType() {
		return workType;
	}
	
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}
	
	/** Amount of work done */
	@Type(type="af_time")
	public AFTime getEffort() {
		return effort;
	}

	public void setEffort(AFTime effort) {
		this.effort = effort;
	}

	/** date of work */
	public Date getWorkDate() {
		return workDate;
	}

	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
}
