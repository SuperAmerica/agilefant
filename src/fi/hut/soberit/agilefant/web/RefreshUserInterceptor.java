package fi.hut.soberit.agilefant.web;

import org.apache.log4j.Logger;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class RefreshUserInterceptor implements Interceptor {
	
	private Logger log = Logger.getLogger(RefreshUserInterceptor.class);
	private UserDAO userDAO;

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		User user = SecurityUtil.getLoggedUser();
		if (user != null){
			userDAO.refresh(user);
		} else {
			log.warn("No user found to be refreshed");
		}
		return invocation.invoke();
	}
	
	public void setUserDAO(UserDAO userDAO){
		this.userDAO = userDAO;
	}

}
