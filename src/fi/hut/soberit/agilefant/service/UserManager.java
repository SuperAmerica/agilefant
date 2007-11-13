package fi.hut.soberit.agilefant.service;

import fi.hut.soberit.agilefant.model.User;

/**
 * Provides extra methods (now avalable in DAO) for user management.
 * 
 */
public interface UserManager {

    /**
     * Checks users login.
     * 
     * @param name
     *                User name of the user.
     * @param password
     *                Password of the user.
     * @return User object if name and password mathches.
     */
    public User login(String name, String password);
}
