package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

public class Sprint {
	
	private int id;
	private String name;
	private String description;
	private Date startDate;
	private Date endDate;
	private Deliverable deliverable;
	private Collection<Task> tasks = new HashSet<Task>();
	private User owner;

}
