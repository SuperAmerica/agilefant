package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for a DAO of a User.
 * 
 * @see GenericDAO
 */
public interface UserDAO extends GenericDAO<User> {

    /** Get user by name. */
    public User getUser(String name);
 
}
