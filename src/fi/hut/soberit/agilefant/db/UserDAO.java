package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for a DAO of a User.
 * 
 * @see GenericDAO
 */
public interface UserDAO extends GenericDAO<User> {

    /**
     * Get the user by login name.
     * 
     * Is case-sensitive.
     */
    User getByLoginName(String loginName);
    
    /**
     * Get the user by login name.
     * 
     * Ignores character case.
     */
    User getByLoginNameIgnoreCase(String loginName);

    /**
     * Get the users by their enabled status.
     * <p>
     * If set to true, gets all enabled users.
     * If set to false, gets all disabled users.
     * @param enabled whether the wanted users are enabled or disabled
     */
    List<User> listUsersByEnabledStatus(boolean enabled);
}
