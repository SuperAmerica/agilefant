package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

@Entity
/**
 * Backlog item is a unit which, within a Cycle of Control model, is in interest of 
 * project manager and the workers within her team.
 * Project manager is interested mainly in efforts that are still due to within 
 * each Backlog item, to be able to check wether the workload of her team
 * (including each team member) seems to be too low, adequate or too high.
 * BacklogItem may be assigned to a named person, who then sees it and is later
 * interested in the progress of the BacklogItem.  
 * Workers are mainly interested in BacklogItems as things to be done, usually
 * this means the sub-parts of the BacklogItems, namely Tasks, which have been
 * assigned to them. To know better, which BacklogItem should be tackled next,
 * there is a priority attached to a BacklogItem.
 */
public class BacklogItem implements PageItem, Assignable, EffortContainer {
	
	private int id;
	private Priority priority;
	private String name;
	private String description;
	private Backlog backlog;
	private Collection<Task> tasks = new HashSet<Task>();
	private AFTime allocatedEffort;
	private AFTime effortEstimate;
	private AFTime performedEffort;
	private User assignee;
	private BacklogItemStatus status = BacklogItemStatus.NOT_STARTED;
	private Map<Integer, User> watchers = new HashMap<Integer, User>();
	private IterationGoal iterationGoal;

	@Type(type="af_time")
	@Formula(value="(select SUM(t.effortEstimate) from Task t where t.backlogItem_id = id)")
	public AFTime getEffortEstimate() {
		return effortEstimate;
	}
	
	protected void setEffortEstimate(AFTime taskEffortLeft) {
		this.effortEstimate = taskEffortLeft;
	}
	
	@Type(type="af_time")
	@Column(name="remainingEffortEstimate")
	public AFTime getAllocatedEffort() {
		return allocatedEffort;
	}
	
	public void setAllocatedEffort(AFTime remainingEffortEstimate) {
		this.allocatedEffort = remainingEffortEstimate;
	}
	
	@Type(type="text")
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
	@Type(type="truncated_string")
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
	
	@Transient
	public Collection<PageItem> getChildren() {
		Collection<PageItem> c = new HashSet<PageItem>(this.tasks.size());
		c.addAll(this.tasks);
		return c;
	}
	@Transient
	public PageItem getParent() {
		//TODO: do some checks
		return (PageItem)getBacklog();
	}
	@Transient
	public boolean hasChildren() {
		return this.tasks.size() > 0 ? true : false;
	}
		
	@Type(type="fi.hut.soberit.agilefant.db.hibernate.EnumUserType",
			parameters = {
				@Parameter(name="useOrdinal", value="true"),
				@Parameter(name="enumClassName", value="fi.hut.soberit.agilefant.model.Priority")
			}
	)
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}	
	
	@ManyToOne
	public User getAssignee() {
		return assignee;
	}
	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}
	
	@Type(type="af_time")
	@Formula(value="(select SUM(e.effort) from TaskEvent e " +
			"INNER JOIN Task t ON e.task_id = t.id " +
			"where e.eventType = 'PerformedWork' and t.backlogItem_id = id)")	
	public AFTime getPerformedEffort() {
		return performedEffort;
	}
	
	protected void setPerformedEffort(AFTime performedEffort){
		this.performedEffort = performedEffort;
	}

	@Type(type="fi.hut.soberit.agilefant.db.hibernate.EnumUserType",
			parameters = {
				@Parameter(name="useOrdinal", value="true"),
				@Parameter(name="enumClassName", value="fi.hut.soberit.agilefant.model.BacklogItemStatus")
			}
	)	
	public BacklogItemStatus getStatus() {
		return status;
	}

	public void setStatus(BacklogItemStatus status) {
		this.status = status;
	}

	@ManyToMany()
	@MapKey()
	public Map<Integer, User> getWatchers() {
		return watchers;
	}

	public void setWatchers(Map<Integer, User> watchers) {
		this.watchers = watchers;
	}

	@ManyToOne
	@JoinColumn (nullable = true)
	public IterationGoal getIterationGoal() {
		return iterationGoal;
	}

	public void setIterationGoal(IterationGoal iterationGoal) {
		this.iterationGoal = iterationGoal;
	}
}
