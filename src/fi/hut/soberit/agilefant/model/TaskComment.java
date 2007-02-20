package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;

import org.hibernate.annotations.Type;

/**
 * Hibernate entity bean representing an event where 
 * a task is commented. Other task events, that wish to carry
 * a comment, extend this class.
 * <p>
 * Contains the comment.
 * 
 * @see fi.hut.soberit.agilefant.model.TaskEvent
 */
@Entity
public class TaskComment extends TaskEvent{
		
	private String comment;

	@Type(type="escaped_text")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
