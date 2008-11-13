package fi.hut.soberit.agilefant.business;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for user business.
 * 
 * @author rjokelai
 * 
 */
public interface UserBusiness {

    /**
     * Check if user is creator of any backlog item.
     * 
     * @param user 
     * @return true is user has created backlog items, false otherwise.
     */
    public boolean hasUserCreatedItems(User user);
    
    /**
     * Get backlog items for the user in progress.
     * 
     * @param user
     *                user, whose backlog items are wanted.
     * @return list of backlog items for user.
     */
    public List<BacklogItem> getBacklogItemsInProgress(User user);

    /**
     * Get all unfinished backlog items assigned to user that belong to an
     * ongoing iteration or an ongoing project that hasn't been divided into
     * iterations. The backlog items are returned in a Map where keys
     * are backlogs and values are lists of backlog items. Each list contains
     * the unfinished backlog items that have been assigned to the user in the
     * key backlog.
     * 
     * @param user
     * @return A Map that maps backlogs to backlog item lists.
     * 
     */
    public Map<Backlog, List<BacklogItem>> getBacklogItemsAssignedToUser(
            User user);

    /**
     * Get all unfinished backlog items assigned to user that belong to any
     * iteration or project. The backlog items are returned in a Map where keys
     * are backlogs and values are lists of backlog items. Each list contains
     * the unfinished backlog items that have been assigned to the user in the
     * key backlog.
     * 
     * @param user
     * @return A Map that maps backlogs to backlog item lists.
     * 
     */
    public Map<Backlog, List<BacklogItem>> getAllBacklogItemsAssignedToUser(
            User user);

    /**
     * Get list of all agilefant users.
     * 
     * @return list of agilefant users.
     */
    public List<User> getAllUsers();

    /**
     * Get user by id.
     * 
     * @param userId
     *                id number of the user
     * @return the user with id userId
     */
    public User getUser(int userId);

    /**
     * Get projects and user is assigned to
     */
    public List<Backlog> getUsersBacklogs(User user);
    
    /**
     * Get all enabled users.
     * @return list of enabled users
     */
    public List<User> getEnabledUsers();
    
    /**
     * Get all disabled users
     * @return list of disabled users
     */
    public List<User> getDisabledUsers();
    
    /**
     * Enable a user.
     * @param user
     */
    public void enableUser(User user);
    
    /**
     * Disable a user.
     * @param user
     */
    public void disableUser(User user);
    
    /**
     * Get all users as a JSON array.
     * @return JSON string
     */
    public String getAllUsersAsJSON();
    
    /**
     * Get user as JSON string.
     * @param user
     * @return
     */
    public String getUserJSON(User user);
    
    /**
     * Get user as JSON string.
     * @param user
     * @return
     */
    public String getUserJSON(int userId);
}
