package fi.hut.soberit.agilefant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

@Entity
/**
 * WorkType is a category for a work to be done, reported in Task level.
 * Percentages of WorkTypes done may be telling whether
 * the company is doing what is thought it should be doing.
 */
public class WorkType {	
	
	private int id;
	private String name;
	private String description;
	private ActivityType activityType;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable = false)	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@ManyToOne
	public ActivityType getActivityType() {
		return activityType;
	}
	
	public void setActivityType(ActivityType activityType) {
		this.activityType = activityType;
	}
	
	@Type(type="text")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(nullable=false)
	@Type(type="truncated_string")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}
