package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Entity
public class ActivityType {
	
	private int id;
	private String name;
	private String description;
	private Collection<WorkType> workTypes = new HashSet<WorkType>();
	
	@Id 
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(nullable = false)
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Column(nullable=false, unique=true)
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
}