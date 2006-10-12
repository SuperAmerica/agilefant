package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Deliverable {
	
	private int id;
	private String name;
	private ActivityType type;
	private Date endDate;
	private Date startDate;
	private Collection<Sprint> sprints = new HashSet<Sprint>();
	private User owner;

}
