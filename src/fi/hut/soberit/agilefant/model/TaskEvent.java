package fi.hut.soberit.agilefant.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Hibernate entity bean representing a task.
 * <p>
 * TaskEvent is something that's happened to a task. It might 
 * be eg. an assignee change, a comment, or logged work.
 * <p>
 * Since this is an abstract class, the actual subclass defines 
 * the event type. The subclasses/event types are:
 * <p>
 * AssignEvent - assignee change <br>
 * TaskComment - comment of task  <br>
 * EstimateHistoryEvent - task's effort estimate changed <br>
 * PerformedWork - work done/added towards the task <br>
 */
@Entity
// inheritance implemented in db using a single table
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
// subclass types discriminated using string column
@DiscriminatorColumn(
    name="eventType",
    discriminatorType=DiscriminatorType.STRING
)
public abstract class TaskEvent {
	
	private int id;
	private User actor;
	private Task task;
	private Date created;
	
	public TaskEvent() {}
	
	public TaskEvent(User actor, Task task, Date created) {
		super();
		this.actor = actor;
		this.task = task;
		this.created = created;
	}

	/** 
	 * Get the id of this object.
	 * <p>
	 * The id is unique among all task events. 
	 */
	// tag this field as the id
	@Id
	// generate automatically
	@GeneratedValue(strategy=GenerationType.AUTO)
	// not nullable
	@Column(nullable = false)
	public int getId() {
		return id;
	}
	
	/** 
	 * Set the id of this object.
	 * <p>
	 * You shouldn't normally call this.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/** Set the user whose task this is. */
	@ManyToOne
	public User getActor() {
		return actor;
	}

	/** Get the user whose task this is. */	
	public void setActor(User actor) {
		this.actor = actor;
	}
	
	/** Get the creation date. */
	public Date getCreated() {
		return created;
	}
	
	/** Set the creation date. */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/** Get the task, under which this event is. */
	@ManyToOne
	@JoinColumn (nullable = false)	
	public Task getTask() {
		return task;
	}
	
	/** Set the task, under which this event is. */
	public void setTask(Task task) {
		this.task = task;
	}
}