package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

@Entity
public class BacklogItem {
	
	private int id;
	private String name;
	private String description;
	private Backlog backlog;
	private Collection<Task> tasks = new HashSet<Task>();
	private AFTime remainingEffortEstimate;
	private AFTime taskEffortLeft;

	@Type(type="af_time")
	@Formula(value="(select SEC_TO_TIME(SUM(TIME_TO_SEC(t.effortEstimate))) from task t where t.backlogItem_id = id)")
	public AFTime getTaskEffortLeft() {
		return taskEffortLeft;
	}
	
	public void setTaskEffortLeft(AFTime taskEffortLeft) {
		this.taskEffortLeft = taskEffortLeft;
	}
	
	@Type(type="af_time")
	public AFTime getRemainingEffortEstimate() {
		return remainingEffortEstimate;
	}
	
	public void setRemainingEffortEstimate(AFTime remainingEffortEstimate) {
		this.remainingEffortEstimate = remainingEffortEstimate;
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
	
	//@Column(nullable = false)
	public String getName() {
	    return name;
	}
	public void setName(String name) {
	    this.name = name;
	}
	
	@OneToMany(mappedBy="backlogItem")
	public Collection<Task> getTasks() {
	    return tasks;
	}
	public void setTasks(Collection<Task> tasks) {
	    this.tasks = tasks;
	}
	
	@ManyToOne
	@JoinColumn (nullable = false)
	public Backlog getBacklog() {
	    return backlog;
	}
	public void setBacklog(Backlog backlog) {
	    this.backlog = backlog;
	}	
}
