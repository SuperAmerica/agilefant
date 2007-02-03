package fi.hut.soberit.agilefant.service;

import fi.hut.soberit.agilefant.model.User;

/**
 * TODO comments kheleniu - Interface for what?
 */
public interface UserManager {
	
	public User login(String name, String password);
}
