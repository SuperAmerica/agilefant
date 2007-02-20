package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Hibernate entity bean representing an event where 
 * assignee of a task changes.
 * <p>
 * Contains old and new assignees.
 * 
 * @see fi.hut.soberit.agilefant.model.TaskEvent
 */
@Entity
public class AssignEvent extends TaskEvent {

	private User oldAssignee;
	private User newAssignee;
		
	@ManyToOne
	public User getNewAssignee() {
		return newAssignee;
	}
	
	public void setNewAssignee(User newAssignee) {
		this.newAssignee = newAssignee;
	}
	
	@ManyToOne
	public User getOldAssignee() {
		return oldAssignee;
	}
	
	public void setOldAssignee(User oldAssignee) {
		this.oldAssignee = oldAssignee;
	}
}
