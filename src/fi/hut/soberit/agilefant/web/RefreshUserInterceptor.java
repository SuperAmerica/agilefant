package fi.hut.soberit.agilefant.web;

import org.apache.log4j.Logger;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

/**
 * Interceptor, which ensures proper user-id is set during each request. Ie.
 * makes getLoggedUser - calls valid for webwork stuff.
 */
public class RefreshUserInterceptor implements Interceptor {

    private static final long serialVersionUID = 1668784370092320107L;

    private Logger log = Logger.getLogger(RefreshUserInterceptor.class);

    private UserDAO userDAO;

    public void destroy() {
    }

    public void init() {
    }

    public String intercept(ActionInvocation invocation) throws Exception {

        int userId;

        try {
            // get the current user id
            userId = SecurityUtil.getLoggedUserId();
        } catch (IllegalStateException e) {
            // no logged user

            log.warn("no user found to be assigned");

            SecurityUtil.setLoggedUser(null);

            return invocation.invoke();
        }

        // get the user object corresponding to the id
        User user = userDAO.get(userId);

        // before the request:
        // set this user as the logged user
        SecurityUtil.setLoggedUser(user);

        // perform request
        String result = invocation.invoke();

        // after the request:
        // reset the logged user
        SecurityUtil.setLoggedUser(null);

        return result;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

}
