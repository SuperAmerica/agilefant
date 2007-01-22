package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

@Entity
public class Task implements PageItem, Assignable, EffortContainer {
	 	
	private int id;
	private Priority priority;
	private TaskStatus status = TaskStatus.NOT_STARTED;
	private AFTime effortEstimate;
	private AFTime performedEffort;
	private String name;
	private String description;
	private BacklogItem backlogItem;
	private Date created;
	private User assignee;
	private User creator;
	private Collection<TaskEvent> events = new HashSet<TaskEvent>();
	private Map<Integer, User> watchers = new HashMap<Integer, User>();

	private Collection<PracticeAllocation> practices = new HashSet<PracticeAllocation>();
	
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
	@Formula(value="(select SUM(e.effort) from TaskEvent e " +
			"where e.eventType = 'PerformedWork' and e.task_id = id)")
	public AFTime getPerformedEffort(){
		return performedEffort;
	}
	
	protected void setPerformedEffort(AFTime performedEffort){
		this.performedEffort = performedEffort;
	}

	public void setEffortEstimate(AFTime effortEstimate) {
	    this.effortEstimate = effortEstimate;
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

	@Type(type="fi.hut.soberit.agilefant.db.hibernate.EnumUserType",
			parameters = {
				@Parameter(name="useOrdinal", value="true"),
				@Parameter(name="enumClassName", value="fi.hut.soberit.agilefant.model.TaskStatus")
			}
	)	
	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public void useTemplate(PracticeTemplate template) {
		
		ArrayList<PracticeAllocation> practiceAllocations = new ArrayList<PracticeAllocation>();
		
		for(Practice p : template.getPractices()) {
			practiceAllocations.add( new PracticeAllocation(p, this) );
		}
		
		setPractices(practiceAllocations);
	}

	@OneToMany(mappedBy="task")
	public Collection<PracticeAllocation> getPractices() {
		return practices;
	}

	public void setPractices(Collection<PracticeAllocation> practices) {
		this.practices = practices;
	}
	
	@ManyToMany()
	@MapKey()
	public Map<Integer, User> getWatchers() {
		return watchers;
	}

	public void setWatchers(Map<Integer, User> watchers) {
		this.watchers = watchers;
	}	
}