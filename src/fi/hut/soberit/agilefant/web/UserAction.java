package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;

public class UserAction extends ActionSupport implements CRUDAction{
	
	private int userId;
	private User user;
	private UserDAO userDAO;

	public String create() {
		userId = 0;
		user = new User();
		return Action.SUCCESS;
		
	}

	public String delete() {
		userDAO.remove(userId);
		return Action.SUCCESS;
	}

	public String edit() {
		user = userDAO.get(userId);
		if (user == null){
			super.addActionError(super.getText("user.notFound"));
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}

	public String store() {
		User storable = new User();
		if (userId > 0){
			storable = userDAO.get(userId);
			if (storable == null){
				super.addActionError(super.getText("user.notFound"));
				return Action.ERROR;
			}
		}
		this.fillStorable(storable);
		if (super.hasActionErrors()){
			return Action.ERROR;
		}
		userDAO.store(storable);
		return Action.SUCCESS;
	}
	
	protected void fillStorable(User storable){
		storable.setFullName(this.user.getFullName());
		storable.setLoginName(this.user.getLoginName());
		storable.setPassword(this.user.getPassword());		
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
