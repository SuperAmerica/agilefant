package fi.hut.soberit.agilefant.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

@Entity
public class Iteration extends Backlog implements PageItem, EffortContainer {
	
	private Date startDate;
	private Date endDate;
    private Deliverable deliverable;
    private AFTime performedEffort;
    private AFTime effortEstimate;
    private Collection<IterationGoal> iterationGoals = new HashSet<IterationGoal>();
//	private User owner;
	
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
	public void setEndDate(String endDate) throws ParseException {
	    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);	    
	    this.startDate = df.parse( endDate);
	}

	//@Column(nullable = false)
	public Date getStartDate() {
	    return startDate;
	}
	public void setStartDate(Date startDate) {
	    this.startDate = startDate;
	}
	public void setStartDate(String startDate) throws ParseException {
	    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);	    
	    this.startDate = df.parse( startDate);
	}
	@Transient
	public Collection<PageItem> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}
	@Transient
	public PageItem getParent() {
		// TODO Auto-generated method stub
		return getDeliverable();
	}
	@Transient
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

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