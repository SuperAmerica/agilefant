package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class TaskComment extends TaskEvent{
	
	private String comment;

	@Column (nullable = false)
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
