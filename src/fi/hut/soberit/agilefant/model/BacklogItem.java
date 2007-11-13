package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * Hibernate entity bean representing a back log. A Backlog is a work log that
 * can contain several tasks. A backlog item itself is in turn contained in a
 * backlog.
 * <p>
 * If the backlog, the backlog item belongs in, is an iteration, the backlog
 * item can be bound to an iteration goal.
 * <p>
 * Backlog item is a unit which, within a Cycle of Control model, is in interest
 * of project manager and the workers within her team.
 * <p>
 * Project manager is interested mainly in efforts that are still due to within
 * each Backlog item, to be able to check wether the workload of her team
 * (including each team member) seems to be too low, adequate or too high.
 * <p>
 * BacklogItem may be assigned to a named person, who then sees it and is later
 * interested in the progress of the BacklogItem.
 * <p>
 * Workers are mainly interested in BacklogItems as things to be done, usually
 * this means the sub-parts of the BacklogItems, namely Tasks, which have been
 * assigned to them. To know better, which BacklogItem should be tackled next,
 * there is a priority attached to a BacklogItem.
 * 
 * @see fi.hut.soberit.agilefant.model.Backlog
 * @see fi.hut.soberit.agilefant.model.Task
 */
@Entity
public class BacklogItem implements PageItem, Assignable, EffortContainer {

	private int id;

	private Priority priority;

	private String name;

	private String description;

	private Backlog backlog;

	private Collection<Task> tasks = new HashSet<Task>();

	private Collection<Task> realTasks;

	private AFTime allocatedEffort;

	private AFTime effortEstimate;

	private AFTime performedEffort;

	private User assignee;

	private TaskStatus status = TaskStatus.NOT_STARTED;

	private Map<Integer, User> watchers = new HashMap<Integer, User>();

	private IterationGoal iterationGoal;

	private Task placeHolder;

	private AFTime bliOrigEst;

	private AFTime taskSumOrigEst;

	private AFTime bliEffEst;

	private AFTime taskSumEffEst;

	private AFTime effortLeft;

	/**
	 * Sum of bliEffEst and taskSumEffEst.
	 */
	private AFTime totalEffortLeft;

	/**
	 * Sum sums of bliOrigEst and taskSumOrigEst.
	 */
	private AFTime totalOrigEst;

	private Log logger = LogFactory.getLog(this.getClass());

	/** Total effort estimate (time), summed from tasks. */
	@Type(type = "af_time")
	@Formula(value = "(select SUM(t.effortEstimate) from Task t where t.backlogItem_id = id)")
	public AFTime getEffortEstimate() {
		return effortEstimate;
	}

	protected void setEffortEstimate(AFTime taskEffortLeft) {
		this.effortEstimate = taskEffortLeft;
	}

	/** Amount of effort (time) allocated for this backlog item. */
	@Type(type = "af_time")
	@Column(name = "remainingEffortEstimate")
	public AFTime getAllocatedEffort() {
		return allocatedEffort;
	}

	public void setAllocatedEffort(AFTime remainingEffortEstimate) {
		this.allocatedEffort = remainingEffortEstimate;
	}

	/** Effort left for this backlog item (from placeholder task */
	@Transient
	public AFTime getEffortLeft() {
		return effortLeft;
	}

	public void setEffortLeft(AFTime effortLeft) {
		this.effortLeft = effortLeft;
	}

	@Type(type = "escaped_text")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the id of this object.
	 * <p>
	 * The id is unique among all backlog items.
	 */
	// tag this field as the id
	@Id
	// generate automatically
	@GeneratedValue(strategy = GenerationType.AUTO)
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

	// @Column(nullable = false)
	@Type(type = "escaped_truncated_varchar")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/** Get the tasks belonging in this backlog item. */
	@OneToMany(mappedBy = "backlogItem")
	@Cascade(CascadeType.DELETE_ORPHAN)
	public Collection<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Collection<Task> tasks) {
		this.tasks = tasks;
	}

	/** The backlog, a part of which this backlogitem is. */
	@ManyToOne
	@JoinColumn(nullable = false)
	public Backlog getBacklog() {
		return backlog;
	}

	public void setBacklog(Backlog backlog) {
		this.backlog = backlog;
	}

	/** {@inheritDoc} */
	@Transient
	public Collection<PageItem> getChildren() {
		Collection<PageItem> c = new HashSet<PageItem>(this.tasks.size());
		c.addAll(this.tasks);
		return c;
	}

	/** {@inheritDoc} */
	@Transient
	public PageItem getParent() {
		// TODO: do some checks
		return (PageItem) getBacklog();
	}

	/** {@inheritDoc} */
	@Transient
	public boolean hasChildren() {
		return this.tasks.size() > 0 ? true : false;
	}

	/** Backlog item priority. */
	@Type(type = "fi.hut.soberit.agilefant.db.hibernate.EnumUserType", parameters = {
			@Parameter(name = "useOrdinal", value = "true"),
			@Parameter(name = "enumClassName", value = "fi.hut.soberit.agilefant.model.Priority") })
	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	/** {@inheritDoc} */
	@ManyToOne
	public User getAssignee() {
		return assignee;
	}

	/** {@inheritDoc} */
	public void setAssignee(User assignee) {
		this.assignee = assignee;
	}

	@Type(type = "af_time")
	@Formula(value = "(select SUM(e.effort) from TaskEvent e "
			+ "INNER JOIN Task t ON e.task_id = t.id "
			+ "where e.eventType = 'PerformedWork' and t.backlogItem_id = id)")
	public AFTime getPerformedEffort() {
		return performedEffort;
	}

	protected void setPerformedEffort(AFTime performedEffort) {
		this.performedEffort = performedEffort;
	}

	// /** Get the status of this backlog item. */
	// @Type(type="fi.hut.soberit.agilefant.db.hibernate.EnumUserType",
	// parameters = {
	// @Parameter(name="useOrdinal", value="true"),
	// @Parameter(name="enumClassName",
	// value="fi.hut.soberit.agilefant.model.BacklogItemStatus")
	// }
	// )
	// public BacklogItemStatus getStatus() {
	// return status;
	// }
	//
	// public void setStatus(BacklogItemStatus status) {
	// this.status = status;
	// }

	/**
	 * Returns the status of the backlog item.
	 * 
	 * @return the status of the backlog item.
	 */
	@Transient
	public TaskStatus getStatus() {
		return this.status;
	}

	/**
	 * Sets the status of the backlog item.
	 * 
	 * @param status
	 *            the status to set for the backlog item
	 */
	public void setStatus(TaskStatus status) {
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
	@JoinColumn(nullable = true)
	public IterationGoal getIterationGoal() {
		return iterationGoal;
	}

	public void setIterationGoal(IterationGoal iterationGoal) {
		this.iterationGoal = iterationGoal;
	}

	/**
	 * Returns the placeholder task of this backlog item
	 * 
	 * @return the placeHolder
	 */
	@OneToOne
	@Cascade(CascadeType.DELETE_ORPHAN)
	public Task getPlaceHolder() {
		return placeHolder;
	}

	/**
	 * Sets the placeholder task of this backlog items
	 * 
	 * @param placeHolder
	 *            the placeHolder to set
	 */
	public void setPlaceHolder(Task placeHolder) {
		this.placeHolder = placeHolder;
	}

	/**
	 * @return the bLIEffEst
	 */
	@Transient
	public AFTime getBliEffEst() {
		return bliEffEst;
	}

	/**
	 * @param effEst
	 *            the bLIEffEst to set
	 */
	public void setBliEffEst(AFTime effEst) {
		bliEffEst = effEst;
	}

	/**
	 * @return the bLIOrigEst
	 */
	@Transient
	public AFTime getBliOrigEst() {
		return bliOrigEst;
	}

	/**
	 * @param origEst
	 *            the bLIOrigEst to set
	 */
	public void setBliOrigEst(AFTime origEst) {
		bliOrigEst = origEst;
	}

	/**
	 * @return the taskSumEffEst
	 */
	@Transient
	public AFTime getTaskSumEffEst() {
		return taskSumEffEst;
	}

	/**
	 * @param taskSumEffEst
	 *            the taskSumEffEst to set
	 */
	public void setTaskSumEffEst(AFTime taskSumEffEst) {
		this.taskSumEffEst = taskSumEffEst;
	}

	/**
	 * @return the taskSumOrigEst
	 */
	@Transient
	public AFTime getTaskSumOrigEst() {
		return taskSumOrigEst;
	}

	/**
	 * @param taskSumOrigEst
	 *            the taskSumOrigEst to set
	 */
	public void setTaskSumOrigEst(AFTime taskSumOrigEst) {
		this.taskSumOrigEst = taskSumOrigEst;
	}

	/**
	 * @return the realTasks
	 */
	@Transient
	public Collection<Task> getRealTasks() {
		return realTasks;
	}

	/**
	 * @param realTasks
	 *            the realTasks to set
	 */
	public void setRealTasks(Collection<Task> realTasks) {
		this.realTasks = realTasks;
	}

	@Transient
	public AFTime getTotalEffortLeft() {
		return totalEffortLeft;
	}

	public void setTotalEffortLeft(AFTime totalEffortLeft) {
		this.totalEffortLeft = totalEffortLeft;
	}

	@Transient
	public AFTime getTotalOrigEst() {
		return totalOrigEst;
	}

	public void setTotalOrigEst(AFTime totalOrigEst) {
		this.totalOrigEst = totalOrigEst;
	}

	/**
	 * Get the backlog items parent backlogs.
	 * 
	 * @param item
	 *            backlog item whose parents are to be get.
	 * @return list of parent backlogs.
	 */
	@Transient
	public List<Backlog> getParentBacklogs() {
		List<Backlog> retlist = new ArrayList<Backlog>();
		Backlog firstParent = getBacklog();

		if (firstParent instanceof Iteration) {
			Deliverable deli = ((Iteration) firstParent).getDeliverable();
			Product prod = deli.getProduct();
			retlist.add(prod);
			retlist.add(deli);
			retlist.add(firstParent);
		} else if (firstParent instanceof Deliverable) {
			Product prod = ((Deliverable) firstParent).getProduct();
			retlist.add(prod);
			retlist.add(firstParent);
		} else if (firstParent instanceof Product) {
			retlist.add(firstParent);
		}

		return retlist;
	}
}
