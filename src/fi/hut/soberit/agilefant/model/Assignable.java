package fi.hut.soberit.agilefant.model;

/**
 * Interface for things that are assignable to a user. 
 * 
 * @author Turkka Äijälä
 */
public interface Assignable {
	
	/** Get the user assigned to this item. */
	public User getAssignee();
	
	/** Assign this item to a user. */
	public void setAssignee(User assignee);
}
