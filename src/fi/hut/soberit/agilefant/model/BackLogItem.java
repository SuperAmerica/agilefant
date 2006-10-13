package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

public class BackLogItem {
	
	private int id;
	private String name;
	private String description;
	private Sprint sprint;
	private Collection<Task> tasks = new HashSet<Task>();

}
