package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;

import org.hibernate.annotations.Type;

@Entity
public class TaskComment extends TaskEvent{
		
	private String comment;

	@Type(type="text")
	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
