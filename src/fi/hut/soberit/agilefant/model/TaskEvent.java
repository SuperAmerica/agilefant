package fi.hut.soberit.agilefant.model;

import java.util.Date;

public abstract class TaskEvent {
	
	private int id;
	private User actor;
	private Task task;
	private Date created;

}
