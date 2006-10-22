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
public class Deliverable {
	
	private int id;
	private String name;
	private String description;
	private ActivityType type;
	private Date endDate;
	private Date startDate;
	private Collection<Sprint> sprints = new HashSet<Sprint>();
	private User owner;
	
	@Column(nullable = false)
	public Date getEndDate() {
	    return endDate;
	}
	public void setEndDate(Date endDate) {
	    this.endDate = endDate;
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
	
	@Column(nullable = false)
	public String getName() {
	    return name;
	}
	public void setName(String name) {
	    this.name = name;
	}
	
	/*public User getOwner() {
	    return owner;
	}
	public void setOwner(User owner) {
	    this.owner = owner;
	}*/
	
	@OneToMany	
	public Collection<Sprint> getSprints() {
	    return sprints;
	}
	public void setSprints(Collection<Sprint> sprints) {
	    this.sprints = sprints;
	}
	
	@Column(nullable = false)
	public Date getStartDate() {
	    return startDate;
	}
	public void setStartDate(Date startDate) {
	    this.startDate = startDate;
	}
	
	@ManyToOne
	@JoinColumn (nullable = false)
	public ActivityType getType() {
	    return type;
	}
	public void setType(ActivityType type) {
	    this.type = type;
	}
	@Column
	public String getDescription() {
	    return description;
	}
	public void setDescription(String description) {
	    this.description = description;
	}

	
}
