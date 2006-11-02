package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Deliverable extends Backlog {
	
    	private Product product;
	private ActivityType type;
	private Date endDate;
	private Date startDate;
	private Collection<Iteration> iterations = new HashSet<Iteration>();
	private User owner;
	
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
//	@JoinColumn (nullable = false)
	public ActivityType getType() {
	    return type;
	}
	public void setType(ActivityType type) {
	    this.type = type;
	}
}
