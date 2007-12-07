package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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

    /**
     * Get user's unfished tasks inside certain timeframe.
     * <p>
     * Remember that Java Date represents a moment in time in millisecond
     * accuracy. If your timeframe is expressed in days, "start" should be the
     * first millisecond of the first day, and "end" the last millisecond of the
     * last day.
     * 
     * @param user
     *                the user, whose tasks are wanted
     * @param start
     *                starting millisecond of the timeframe
     * @param start
     *                ending millisecond of the timeframe
     * @return collection of tasks matching the restrictions
     */
    public Collection<Task> getUnfinishedTasksByTime(User user, Date start,
            Date end);

    /**
     * Get user's backlog items inside certain timeframe.
     * <p>
     * Remember that Java Date represents a moment in time in millisecond
     * accuracy. If your timeframe is expressed in days, "start" should be the
     * first millisecond of the first day, and "end" the last millisecond of the
     * last day.
     * 
     * @param user
     *                the user, whose backlog items are wanted
     * @param start
     *                starting millisecond of the timeframe
     * @param start
     *                ending millisecond of the timeframe
     * @return collection of tasks matching the restrictions
     */
    public Collection<BacklogItem> getBacklogItemsByTime(User user, Date start,
            Date end);

    /**
     * Get backlog items assigned to user.
     * 
     * @param user
     *                the user, whose backlog items are wanted.
     * @return list of backlog items assigned to user.
     */
    public List<BacklogItem> getBacklogItemsInProgress(User user);
}
