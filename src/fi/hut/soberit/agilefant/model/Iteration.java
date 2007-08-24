package fi.hut.soberit.agilefant.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * A Hibernate entity bean which represents an iteration. 
 * <p>
 * Conceptually, an iteration is a type of a backlog. A iteration-backlog 
 * represents work (backlog items, tasks) to be done during 
 * an iteration. Iteration is a time period, a conceptual tool, used to divide and 
 * manage work. It's usually a few weeks in length.
 * <p>
 * Since a deliverable is a backlog, it can contain backlog items, which, in turn,
 * are smaller containers for work. An iteration is a part of a bigger work container, 
 * the deliverable.
 * <p>
 * An iteration is part of a deliverable. Start- and ending dates can be 
 * defined, as well as effort estimate and already performed effort.  
 * <p>
 * An iteration can contain some iteration goals to which underlying backlog items
 * can be bound to. Iteration goals are higher level concepts. Multiple backlog items 
 * can work towards a single iteration goal. 
 * 
 * @see fi.hut.soberit.agilefant.model.Backlog
 * @see fi.hut.soberit.agilefant.model.BacklogItem
 * @see fi.hut.soberit.agilefant.model.Iteration
 * @see fi.hut.soberit.agilefant.model.IterationGoal
 */
@Entity
public class Iteration extends Backlog implements PageItem, EffortContainer {
	
	private Date startDate;
	private Date endDate;
    private Deliverable deliverable;
    private AFTime performedEffort;
    private AFTime effortEstimate;
    private Collection<IterationGoal> iterationGoals = 
    	new HashSet<IterationGoal>();
//	private User owner;
    
	private Log logger = LogFactory.getLog(getClass());
	
    @Transient
    public double getCompletionEstimationPercentage() {
    	// Estimate percentage of completion by calculating
    	// total time from performed effort + work left, and
    	// dividing performed effort with it, to get fraction of completion. 
    	
    	long performed = getPerformedEffort().getTime();
    	long estimate = getEffortEstimate().getTime();
    	
    	long total = performed + estimate;
    	
    	return 100.0*(double)performed/(double)total;
    }
    
    /** The deliverable, under which this iteration is. */
	@ManyToOne 
	//@JoinColumn (nullable = false)
	public Deliverable getDeliverable() {
		return deliverable;
	}
	public void setDeliverable(Deliverable deliverable) {
		this.deliverable = deliverable;
	}
	
	//@Column(nullable = false)
	public Date getEndDate() {
	    return endDate;
	}	
	public void setEndDate(Date endDate) {
	    this.endDate = endDate;
	}	
	public void setEndDate(String endDate, String dateFormat) 
			throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
	    df.setLenient(true);
	    this.endDate = df.parse( endDate);
	}

	//@Column(nullable = false)
	public Date getStartDate() {
	    return startDate;
	}
	public void setStartDate(Date startDate) {
	    this.startDate = startDate;
	}
	public void setStartDate(String startDate, String dateFormat) 
			throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
	    df.setLenient(true);
	    this.startDate = df.parse( startDate);
	}
	
	/** {@inheritDoc} */
	@Transient
	public Collection<PageItem> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/** {@inheritDoc} */
	@Transient
	public PageItem getParent() {
		// TODO Auto-generated method stub
		return getDeliverable();
	}
	
	/** {@inheritDoc} */
	@Transient
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Type(type="af_time")
	@Formula(value="(select SUM(t.effortEstimate) from Task t " +
			"INNER JOIN BacklogItem bi ON t.backlogItem_id = bi.id " +
			"where bi.backlog_id = id)")
	public AFTime getEffortEstimate() {
		return this.effortEstimate;
	}
	
	protected void setEffortEstimate(AFTime effortEstimate){
		this.effortEstimate = effortEstimate;
	}

	/** {@inheritDoc} */
	@Type(type="af_time")
	@Formula(value="(select SUM(e.effort) from TaskEvent e " +
			"INNER JOIN Task t ON e.task_id = t.id " +
			"INNER JOIN BacklogItem bi ON t.backlogItem_id = bi.id " +
			"where e.eventType = 'PerformedWork' and bi.backlog_id = id)")
	public AFTime getPerformedEffort() {
		return performedEffort;
	}
	
	protected void setPerformedEffort(AFTime performedEffort){
		this.performedEffort = performedEffort;
	}
	
	@OneToMany(mappedBy="iteration")
	@OrderBy("priority, id")
	public Collection<IterationGoal> getIterationGoals() {
		return iterationGoals;
	}
	public void setIterationGoals(Collection<IterationGoal> iterationGoals) {
		this.iterationGoals = iterationGoals;
	}

}