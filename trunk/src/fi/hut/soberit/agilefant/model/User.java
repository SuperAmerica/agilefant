package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.db.hibernate.Email;
import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * Hibernate entity bean representing a user. User represents a person using
 * the webapp: it's more a thing of the implementation than anything conceptual.
 * <p>
 * The user carries information on username, password, full name and email.
 * Also there're different collections of items, where this user is assigned. 
 */
@Entity
public class User implements PageItem {
	
	private int id;
	
	private String password;	
	private String loginName;
	private String fullName;
	private String email;
	
	private Collection<Task> assignments = new HashSet<Task>();	
	private Collection<Backlog> backlogs = new HashSet<Backlog>();
	private Collection<BacklogItem> backlogItems = new HashSet<BacklogItem>();
	private Collection<Task> watchedTasks = new HashSet<Task>();
	private Collection<BacklogItem> watchedBacklogItems = new HashSet<BacklogItem>();

	private AFTime assignmentsTotalEffortEstimate;
	private AFTime assignmentsTotalPerformedEffort;
	
	private AFTime watchedTasksTotalEffortEstimate;
	private AFTime watchedTasksTotalPerformedEffort;	
	
	/** Get backlog items this user watches. */
	@ManyToMany(cascade={CascadeType.PERSIST}, mappedBy="watchers")
	public Collection<BacklogItem> getWatchedBacklogItems() {
		return watchedBacklogItems;
	}

	/** Set backlog items this user watches. */
	public void setWatchedBacklogItems(Collection<BacklogItem> watchedBacklogItems) {
		this.watchedBacklogItems = watchedBacklogItems;
	}

	/** Get Tasks items this user watches. */
	@ManyToMany(cascade={CascadeType.PERSIST}, mappedBy="watchers")
	public Collection<Task> getWatchedTasks() {
		return watchedTasks;
	}

	/** Set Tasks items this user watches. */
	public void setWatchedTasks(Collection<Task> watchedTasks) {
		this.watchedTasks = watchedTasks;
	}

	/** 
	 * Get the id of this object.
	 * <p>
	 * The id is unique among all users. 
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

	/** Get full name. */
	@Type(type="escaped_truncated_varchar")
	public String getFullName() {
		return fullName;
	}
	
	/** Set full name. */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	/** Get login name. */
	@Column(unique=true)
	@Type(type="escaped_truncated_varchar")
	public String getLoginName() {
		return loginName;
	}
	
	/** Set login name. */
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	
	/** Get password. */
	@Type(type="truncated_varchar")
	public String getPassword() {
		return password;
	}
	
	/** Set password. */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/** Get tasks, where the user is assigned. */
    @OneToMany(mappedBy="assignee")
	public Collection<Task> getAssignments() {
		return assignments;
	}

    /** Set tasks, where the user is assigned. */
	public void setAssignments(Collection<Task> assignments) {
		this.assignments = assignments;
	}
	
	/** {@inheritDoc} */
	@Transient
	public Collection<PageItem> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** Get login name. */
	@Transient
	public String getName() {
		// TODO Auto-generated method stub
		return this.loginName;
	}
	
	/** {@inheritDoc} */
	@Transient
	public PageItem getParent() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** {@inheritDoc} */
	@Transient
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

    /** Get backlog items, where the user is assigned. */
	@OneToMany(mappedBy="assignee")
	public Collection<BacklogItem> getBacklogItems() {
		return backlogItems;
	}
	
	/** Set backlog items, where the user is assigned. */
	public void setBacklogItems(Collection<BacklogItem> backlogItems) {
		this.backlogItems = backlogItems;
	}

	/** Get backlogs, where the user is assigned. */
	@OneToMany(mappedBy="assignee")
	public Collection<Backlog> getBacklogs() {
		return backlogs;
	}
	
	/** Set backlogs, where the user is assigned. */
	public void setBacklogs(Collection<Backlog> backlogs) {
		this.backlogs = backlogs;
	}
	
	/** Set all Assignables, where this user is assigned. */
	@Transient
	public Collection<Assignable> getAssignables() {
		Collection<Assignable> collection = new HashSet<Assignable>();
		
		collection.addAll(getAssignments());		
		collection.addAll(getBacklogs());
		collection.addAll(getBacklogItems());
		
		return collection;
	}

	/** 
	 * Get email addresses. Note that the field is validated 
	 * to be a valid a email address: an exception is thrown on store,
	 * if it's invalid.
	 */
	@Column(nullable=true)
	@Email
	@Type(type="truncated_varchar")
	public String getEmail() {
		return email;
	}

	/** 
	 * Set email addresses. Note that the field is validated 
	 * to be a valid a email address: an exception is thrown on store,
	 * if it's invalid. 
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Get summed performed effort for all assigned tasks.  
	 */
	@Type(type="af_time")
	@Formula(value=	"(select sum(e.effort) from TaskEvent e, Task t " +
			"where e.eventType = 'PerformedWork' and t.id = e.task_id and t.assignee_id = id and t.status != 4)")	
	public AFTime getAssignmentsTotalPerformedEffort() {
		return assignmentsTotalPerformedEffort;
	}

	/**
	 * Set summed performed effort for all assigned tasks. 
	 * <p>
	 * You shouldn't normally call this. This setter exists for Hibernate. 
	 */
	public void setAssignmentsTotalPerformedEffort(
			AFTime assignmentsTotalPerformedEffort) {
		this.assignmentsTotalPerformedEffort = assignmentsTotalPerformedEffort;
	}

	/**
	 * Get summed effort estimate for all assigned tasks.  
	 */
	@Type(type="af_time")
	@Formula(value=	"(select SUM(t.effortEstimate) from Task t " +
					"where t.assignee_id = id and t.status != 4)")	
	public AFTime getAssignmentsTotalEffortEstimate() {
		return assignmentsTotalEffortEstimate;
	}

	/**
	 * Set summed effort estimate for all assigned tasks.
	 * <p>
	 * You shouldn't normally call this. This setter exists for Hibernate.  
	 */
	public void setAssignmentsTotalEffortEstimate(
			AFTime assignmentsTotalEffortEstimate) {
		this.assignmentsTotalEffortEstimate = assignmentsTotalEffortEstimate;
	}

	/**
	 * Get summed performed effort for all watched tasks. 
	 */
	@Type(type="af_time")
	
	//@Formula(value=	"select SUM(t.effortEstimate) from Task t, User u " +
	//				"where u in elements(t.watchers) and u.id = this.id")
					//"where id in indices(t.watchers) )")
					//"where user_task.watchers_id = id and user_task.watchedTask_id = id)")
					//"inner join t.watchers as user)")
					//")")
					//"where u in t.watchers and u.id = id)")
					//"where t.watchers_id = id)")
					//"where t.assignee_id = id)")			
					//"INNER JOIN t.watchers as user ON user.id = id)")
	//@Formula(value=	"(select SUM(t.effortEstimate) from Task t, User u " +
	//			 	"where u in elements(t.watchers) and u.id = 1)")
	//@Formula(value="( SELECT SUM(task.effortEstimate) FROM Task task )")

	// it took me more than five hours to figure this out
	@Formula(value="( SELECT SUM(task.effortEstimate) FROM Task task, User user WHERE user.id = id AND (user.id in ( SELECT watchers.watchers_id FROM Task_User watchers WHERE task.id=watchers.watchedTasks_id  ) ) )")
	public AFTime getWatchedTasksTotalEffortEstimate() {
		return watchedTasksTotalEffortEstimate;
	}

	/**
	 * Set summed performed effort for all watched tasks. 
	 * <p>
	 * You shouldn't normally call this. This setter exists for Hibernate. 
	 */
	public void setWatchedTasksTotalEffortEstimate(
			AFTime watchedTasksTotalEffortEstimate) {
		this.watchedTasksTotalEffortEstimate = watchedTasksTotalEffortEstimate;
	}

	/**
	 * Get summed performed effort for all watched tasks.  
	 */	
	@Type(type="af_time")
	@Formula(value="( SELECT SUM(e.effort) FROM TaskEvent e, Task task WHERE " +
			"e.eventType = 'PerformedWork' and task.id = e.task_id and task.assignee_id = id and" +
			"(task.assignee_id in ( SELECT watchers.watchers_id FROM Task_User watchers WHERE task.id=watchers.watchedTasks_id  ) ) )")
	public AFTime getWatchedTasksTotalPerformedEffort() {
		return watchedTasksTotalPerformedEffort;
	}

	/**
	 * Set summed performed effort for all watched tasks.
	 * <p>
	 * You shouldn't normally call this. This setter exists for Hibernate.   
	 */	
	public void setWatchedTasksTotalPerformedEffort(
			AFTime watchedTasksTotalPerformedEffort) {
		this.watchedTasksTotalPerformedEffort = watchedTasksTotalPerformedEffort;
	} 
}
