package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.service.UserManager;

public class LoginAction extends ActionSupport {

	private static final long serialVersionUID = -5808987058405748396L;
	private String name;
	private String password;
	private UserManager userManager;
	
	public String login(){
		User user = userManager.login(name, password);
		if (user == null){
			super.addActionError(super.getText("login.loginFailed"));
			return Action.INPUT;
		}
		// Put user to session so it can be referenced later
		return Action.SUCCESS;
	}
	
	public String logout(){
		//Logout here!
		return Action.SUCCESS;
	}	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
}
