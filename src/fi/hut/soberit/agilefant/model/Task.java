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
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

@Entity
public class Task implements PageItem {
	 	
	private int id;
	private int severity;
	private int priority;
	private AFTime effortEstimate;
	private AFTime performedEffort;
	private String name;
	private String description;
	private BacklogItem backlogItem;
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
	@OrderBy(value="created")
	public Collection<TaskEvent> getEvents() {
	    return events;
	}

	public void setEvents(Collection<TaskEvent> events) {
	    this.events = events;
	}

	@Type(type="af_time")
	public AFTime getEffortEstimate() {
	    return effortEstimate;
	}

	@Type(type="af_time")
	@Formula(value="(select SEC_TO_TIME(SUM(TIME_TO_SEC(e.effort))) from TaskEvent e " +
			"where e.eventType = 'PerformedWork' and e.task_id = id)")
	public AFTime getPerformedEffort(){
		return performedEffort;
	}
	
	public void setPerformedEffort(AFTime performedEffort){
		this.performedEffort = performedEffort;
	}

	public void setEffortEstimate(AFTime effortEstimate) {
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
	public BacklogItem getBacklogItem() {
	    return backlogItem;
	}

	public void setBacklogItem(BacklogItem backlogItem) {
	    this.backlogItem = backlogItem;
	}
	@Transient
	public Collection<PageItem> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	@Transient
	public PageItem getParent() {
		return getBacklogItem();
	}
	@Transient
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}
}