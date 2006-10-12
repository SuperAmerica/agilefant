package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

public class Project {
	
	private int id;
	private String name;
	private String description;
	private Collection<Deliverable> deliverables = new HashSet<Deliverable>();
}
