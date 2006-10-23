package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Entity;
import javax.persistence.Transient;

@Entity
public class Task {
	 	
	private int id;
	private int severity;
	private int priority;
	private int effortEstimate;
	private String name;
	private String description;
	private BackLogItem backLogItem;
	private Date created;
	private User assignee;
	private User creator;
	private Collection<TaskEvent> events = new HashSet<TaskEvent>();

	@Transient
	private Collection<PerformedWork> getPerformedWorks(){
		Collection<PerformedWork> result = new ArrayList<PerformedWork>();
		for (TaskEvent event : events){
			if (event instanceof PerformedWork){
				result.add((PerformedWork)event);
			}
		}
		return result;
	}

	public String getDescription() {
	    return description;
	}

	public void setDescription(String description) {
	    this.description = description;
	}

	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable = false)	
	public int getId() {
	    return id;
	}

	public void setId(int id) {
	    this.id = id;
	}

	public String getName() {
	    return name;
	}

	public void setName(String name) {
	    this.name = name;
	}

	public Date getCreated() {
	    return created;
	}

	public void setCreated(Date created) {
	    this.created = created;
	}

	@ManyToOne
	public User getCreator() {
	    return creator;
	}

	public void setCreator(User creator) {
	    this.creator = creator;
	}

	@ManyToOne
	public User getAssignee() {
	    return assignee;
	}

	public void setAssignee(User assignee) {
	    this.assignee = assignee;
	}

	@OneToMany(mappedBy="task")
	public Collection<TaskEvent> getEvents() {
	    return events;
	}

	public void setEvents(Collection<TaskEvent> events) {
	    this.events = events;
	}

	@Column(nullable = true)	
	public int getEffortEstimate() {
	    return effortEstimate;
	}

	public void setEffortEstimate(int effortEstimate) {
	    this.effortEstimate = effortEstimate;
	}

	@Column(nullable = true)
	public int getPriority() {
	    return priority;
	}
	
	public void setPriority(int priority) {
	    this.priority = priority;
	}

	@Column(nullable = true)
	public int getSeverity() {
	    return severity;
	}
		
	public void setSeverity(int severity) {
	    this.severity = severity;
	}

	@ManyToOne
	@JoinColumn (nullable = false)	
	public BackLogItem getBackLogItem() {
	    return backLogItem;
	}

	public void setBackLogItem(BackLogItem backLogItem) {
	    this.backLogItem = backLogItem;
	}
}