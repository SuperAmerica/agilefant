package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;
import fi.hut.soberit.agilefant.db.hibernate.EmailValidator;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

/**
 * UserAction
 * @author khel
 */
public class UserAction extends ActionSupport implements CRUDAction{

	private static final long serialVersionUID = 284890678155663442L;
	private int userId;
	private User user;
	private UserDAO userDAO;
	private String password1;
	private String password2;
	private String email;

	public String create() {
		userId = 0;
		user = new User();
		return Action.SUCCESS;		
	}

	public String delete() {
		User u = userDAO.get(userId);
		if (u == null){
			super.addActionError(super.getText("user.notFound"));
			return Action.ERROR;
		}
		if(u.getAssignables().size() > 0 || u.getWatchedBacklogItems().size() > 0 
				|| u.getWatchedTasks().size() > 0) {
			super.addActionError(super.getText("user.hasLinkedItems"));
			return Action.ERROR;
		}
		/* Prevent the deletion of administrator*/
		if(userId == 1){
			super.addActionError("User cannot be deleted");
			return Action.ERROR;
		}
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
		String md5Pw = null;
		if (password1.length() == 0 && password2.length() == 0){
			if (storable.getId() == 0){
				super.addActionError(super.getText("user.missingPassword"));
				return;
			}
			md5Pw = storable.getPassword();
		} else {
			if (!password1.equals(password2)){
				password1 = "";
				password2 = "";
				super.addActionError(super.getText("user.passwordsNotEqual"));
				return;
			} else {
				md5Pw = SecurityUtil.MD5(password1);
			}
		}
		User existingUser = userDAO.getUser(this.user.getLoginName());
		if (existingUser != null && existingUser.getId() != storable.getId()){
			super.addActionError(super.getText("user.loginNameInUse"));
			return;
		}

		storable.setFullName(this.user.getFullName());
		storable.setLoginName(this.user.getLoginName());
		storable.setPassword(md5Pw);
		if(this.user.getEmail() != null) {
			EmailValidator e = new EmailValidator();
			if(!e.isValid(this.user.getEmail())) {				
				super.addActionError(super.getText("user.invalidEmail"));
				return;				
			}
		}
		storable.setEmail(this.user.getEmail());
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
	
	/**
	 * Method added for testing.
	 * 
	 * @return UserDAO-object
	 */
/*	protected UserDAO getUserDAO() {
		return userDAO;
	}*/
	
	public String getPassword1() {
		return password1;
	}

	public void setPassword1(String password1) {
		this.password1 = password1;
	}

	public String getPassword2() {
		return password2;
	}

	public void setPassword2(String password2) {
		this.password2 = password2;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
