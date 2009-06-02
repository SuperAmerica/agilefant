package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for user business.
 * 
 * @author rjokelai
 * 
 */
public interface UserBusiness extends GenericBusiness<User> {

    /**
     * Check if user is creator of any stories.
     * 
     * @param user
     * @return true is user has created stories, false otherwise.
     */
    boolean hasUserCreatedStories(User user);

    User retrieveByLoginName(String loginName);

    /**
     * Get all enabled users.
     * 
     * @return list of enabled users
     */
    List<User> getEnabledUsers();

    /**
     * Get all disabled users.
     * 
     * @return list of disabled users
     */
    List<User> getDisabledUsers();

    void disableUser(int id);

    void enableUser(int id);

}
