package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

public class ActivityType {
	
	private int id;
	private String name;
	private String description;
	private Collection<WorkType> workTypes = new HashSet<WorkType>();

}
