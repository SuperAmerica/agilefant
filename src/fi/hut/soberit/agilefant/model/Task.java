package fi.hut.soberit.agilefant.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Task {
	 	
	private int id;
	private int severity;
	private int priority;
	private int effortEstimate;
	private String name;
	private String description;
	private Sprint sprint;
	private Date created;
	private User assignee;
	private User creator;
	private Collection<TaskEvent> events = new HashSet<TaskEvent>();
	
	private Collection<PerformedWork> getPerformedWorks(){
		Collection<PerformedWork> result = new ArrayList<PerformedWork>();
		for (TaskEvent event : events){
			if (event instanceof PerformedWork){
				result.add((PerformedWork)event);
			}
		}
		return result;
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
