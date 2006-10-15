package fi.hut.soberit.agilefant.web.util;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;
import com.opensymphony.xwork.spring.interceptor.ActionAutowiringInterceptor;

public class AuthenticationInterceptor implements Interceptor {
	
	public static final String NOT_LOGGED_IN = "notLoggedIn";

	public void destroy() {
	}

	public void init() {
	}

	public String intercept(ActionInvocation invocation) throws Exception {
		if (!checkAuthentication()){
			return AuthenticationInterceptor.NOT_LOGGED_IN;
		}
		return invocation.invoke();
	}
	
	protected boolean checkAuthentication(){
		// Check authentication here
		return true;
	}
}
