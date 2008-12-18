package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for a DAO of a User.
 * 
 * @see GenericDAO
 */
public interface UserDAO extends GenericDAO<User> {

    /** Get user by name. */
    public User getUser(String name);
 
    /**
     * Get all enabled users
     * @return all enabled users
     */
    public List<User> getEnabledUsers();
    
    /**
     * Get all disabled users
     * @return all disabled users
     */
    public List<User> getDisabledUsers();
}
