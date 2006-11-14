package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;

public class UserAction extends ActionSupport implements CRUDAction{
	
	private int userId;
	private User user;
	private UserDAO userDAO;

	public String create() {
		// TODO Auto-generated method stub
		return null;
	}

	public String delete() {
		// TODO Auto-generated method stub
		return null;
	}

	public String edit() {
		// TODO Auto-generated method stub
		return null;
	}

	public String store() {
		// TODO Auto-generated method stub
		return null;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}
