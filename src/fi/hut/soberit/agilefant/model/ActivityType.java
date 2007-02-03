package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import org.hibernate.annotations.Type;
import org.hibernate.validator.Range;

@Entity
public class ActivityType {
	
	private int id;
	private String name;
	private String description;
	private Collection<WorkType> workTypes = new HashSet<WorkType>();
	private int targetSpendingPercentage = 0;
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable = false)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	@Type(type="text")
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(nullable=false, unique=true)
	@Type(type="truncated_string")
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany(mappedBy="activityType")
	public Collection<WorkType> getWorkTypes() {
		return workTypes;
	}
	
	public void setWorkTypes(Collection<WorkType> workTypes) {
		this.workTypes = workTypes;
	}

	@Range(min=0, max=100)
	public int getTargetSpendingPercentage() {
		return targetSpendingPercentage;
	}

	public void setTargetSpendingPercentage(int targetSpendingPercentage) {
		this.targetSpendingPercentage = targetSpendingPercentage;
	}
}