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
}
