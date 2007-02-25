package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.Date;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for a DAO of a User.
 * 
 * @see GenericDAO
 */
public interface UserDAO extends GenericDAO<User> {
	
	/** Get user by name. */
	public User getUser(String name);
	
	/** Get all unfinished tasks assigned to a user. */
	public Collection<Task> getUnfinishedTasks(User user);
	
	/** Get all unfinished tasks watched by a user. */
	public Collection<Task> getUnfinishedWatchedTasks(User user);
	
	public Collection<Task> getUnfinishedTasksByTime(User user, Date start, Date end);
	public Collection<BacklogItem> getUnfinishedBacklogitemsByTime(User user, Date start, Date end);
}
