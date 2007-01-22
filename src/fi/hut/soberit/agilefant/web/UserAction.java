package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class UserAction extends ActionSupport implements CRUDAction{

	private static final long serialVersionUID = 284890678155663442L;
	private int userId;
	private User user;
	private UserDAO userDAO;
	private String password1;
	private String password2;

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
	
	/*
	 * For testing
	 */
	protected UserDAO getUserDAO() {
		return userDAO;
	}
	
	/**
	 * Adds a test user for developement purposes.
	 * There's a link to this action in the login page.
	 * <br><br>
	 * full name: Teppo Testi<br>
	 * username: test<br>
	 * password: test<br>
	 * 
	 * @author Turkka Äijälä
	 */
	public String addTestUser() {
		// if already exists, just return
		if(userDAO.getUser("test") != null)
			return Action.SUCCESS;
		
		User testUser = new User();
				
		testUser.setFullName("Teppo Testi");
		testUser.setLoginName("test");		
		testUser.setPassword(SecurityUtil.MD5("test"));
		
		userDAO.store(testUser);
				
		return Action.SUCCESS;
	}
	
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
}
