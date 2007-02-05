package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public interface UserDAO extends GenericDAO<User> {
	
	public User getUser(String name);
	public Collection<Task> getUnfinishedTasks(User user);
	public Collection<Task> getUnfinishedWatchedTasks(User user);
}
