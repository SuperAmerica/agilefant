package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for a DAO of a User.
 * 
 * @see GenericDAO
 */
public interface UserDAO extends GenericDAO<User> {

    User getByLoginName(String loginName);

    /**
     * Get the users by their enabled status.
     * <p>
     * If set to true, gets all enabled users.
     * If set to false, gets all disabled users.
     * @param enabled whether the wanted users are enabled or disabled
     */
    List<User> listUsersByEnabledStatus(boolean enabled);
}
