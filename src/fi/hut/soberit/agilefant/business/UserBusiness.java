package fi.hut.soberit.agilefant.business;

import java.util.List;

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
     * Get backlog items for the user in progress.
     * 
     * @param user
     *                user, whose backlog items are wanted.
     * @return list of backlog items for user.
     */
    public List<BacklogItem> getBacklogItemsInProgress(User user);

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
}
