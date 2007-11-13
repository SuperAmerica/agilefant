package fi.hut.soberit.agilefant.service;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;

public class UserManagerImpl implements UserManager {

	private UserDAO userDAO;

	public User login(String name, String password) {
		User user = userDAO.getUser(name);
		if (user != null && user.getPassword().equals(password)) {
			return user;
		} else {
			return null;
		}
	}
}
