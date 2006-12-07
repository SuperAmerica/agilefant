package fi.hut.soberit.agilefant.model;

/**
 * Interface for things that are assignable to a user. 
 * 
 * @author Turkka Äijälä
 */
public interface Assignable {
	public User getAssignee();
	public void setAssignee(User assignee);
}
