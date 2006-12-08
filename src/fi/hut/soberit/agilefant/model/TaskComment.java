package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;

@Entity
public class TaskComment extends TaskEvent{
	
	private String comment;

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
