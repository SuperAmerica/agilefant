package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import javax.persistence.Entity;

public class Sprint {
	
	private int id;
	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private Deliverable deliverable;
	private Collection<BackLogItem> backLogs = new HashSet<BackLogItem>();
	private User owner;
	public Deliverable getDeliverable() {
		return deliverable;
	}
	public void setDeliverable(Deliverable deliverable) {
		this.deliverable = deliverable;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
