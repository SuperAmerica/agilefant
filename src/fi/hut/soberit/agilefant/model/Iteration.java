package fi.hut.soberit.agilefant.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import fi.hut.soberit.agilefant.web.page.PageItem;

@Entity
public class Iteration extends Backlog implements PageItem {
	
	private Date startDate;
	private Date endDate;
    private Deliverable deliverable;
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
}