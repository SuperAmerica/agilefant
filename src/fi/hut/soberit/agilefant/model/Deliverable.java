package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.annotations.Formula;
import org.hibernate.annotations.Type;

import fi.hut.soberit.agilefant.web.page.PageItem;

@Entity
public class Deliverable extends Backlog implements PageItem, EffortContainer {
	
    private Product product;
	private ActivityType activityType;
	private Date endDate;
	private Date startDate;
	private Collection<Iteration> iterations = new HashSet<Iteration>();
	private User owner;
	private AFTime effortEstimate;
	private AFTime performedEffort;
	
	@ManyToOne
	//@JoinColumn (nullable = true)
	public Product getProduct() {
	    return product;
	}
	public void setProduct(Product product) {
	    this.product = product;
	}
	
	@ManyToOne
	public User getOwner() {
	    return owner;
	}
	public void setOwner(User owner) {
	    this.owner = owner;
	}
	
	@OneToMany(mappedBy="deliverable")
	public Collection<Iteration> getIterations() {
	    return iterations;
	}
	public void setIterations(Collection<Iteration> iterations) {
	    this.iterations = iterations;
	}
	
	//@Column(nullable = false)
	public Date getStartDate() {
	    return startDate;
	}
	public void setStartDate(Date startDate) {
	    this.startDate = startDate;
	}

	//@Column(nullable = false)
	public Date getEndDate() {
	    return endDate;
	}
	public void setEndDate(Date endDate) {
	    this.endDate = endDate;
	}
	
	@ManyToOne
	public ActivityType getActivityType() {
	    return activityType;
	}
	public void setActivityType(ActivityType activityType) {
	    this.activityType = activityType;
	}
	@Transient
	public Collection<PageItem> getChildren() {
		Collection<PageItem> c = new HashSet<PageItem>(this.iterations.size());
		c.addAll(this.iterations);
		return c;
	}
	@Transient
	public PageItem getParent() {
		return getProduct();
	}
	@Transient
	public boolean hasChildren() {
		return this.iterations.size() > 0 ? true : false;
	}
	
	public AFTime getEffortEstimate() {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected void setEffortEstimate(AFTime effortEstimate){
		this.effortEstimate = effortEstimate;
	}

	@Type(type="af_time")
	@Formula(value="(select SUM(e.effort) " + 
			"from TaskEvent e, Task t, BacklogItem bi, Backlog b "+
			"where e.eventType = 'PerformedWork' " +
			"and e.task_id = t.id " +
			"and t.backlogItem_id = bi.id " +
			"and bi.backlog_id = b.id " +
			"and (" +
				"bi.backlog_id = id " +
				"or b.deliverable_id = id))")
	public AFTime getPerformedEffort() {
		return performedEffort;
	}
	
	protected void setPerformedEffort(AFTime performedEffort){
		this.performedEffort = performedEffort;
	}
}
