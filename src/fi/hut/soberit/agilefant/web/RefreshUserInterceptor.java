package fi.hut.soberit.agilefant.web;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.hibernate.UserDAOHibernate;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import flexjson.JSONSerializer;

/**
 * Interceptor, which ensures proper user-id is set during each request. Ie.
 * makes getLoggedUser - calls valid for struts stuff.
 */
@Component("refreshUserInterceptor")
@Scope("prototype")
public class RefreshUserInterceptor implements Interceptor {

    private static final long serialVersionUID = 1668784370092320107L;

    private Logger log = Logger.getLogger(RefreshUserInterceptor.class);
    
    private static boolean isUnderReadOnlyAction = false;

    @Autowired
    private UserBusiness userBusiness;

    public void destroy() {
    }

    public void init() {
    }
    
    public static String getStackTrace(Throwable aThrowable) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        aThrowable.printStackTrace(printWriter);
        return result.toString();
      }

    public String intercept(ActionInvocation invocation) throws Exception {

        int userId;
        Object action = invocation.getAction();
        
        if(action instanceof ExceptionHandler){
            //actually print the message for debugging purposes
            String trace = getStackTrace(((ExceptionHandler)action).getException());            
            return "";
        }
        
        //TODO FINNUCKS: this logs out a current user on one of 
        //these actions and sets it to the read only user.
        //Need to check ID and ... ?
        if(action instanceof ROIterationAction || (isUnderReadOnlyAction && (
                action instanceof ChartAction
                || action instanceof IterationAction
                || action instanceof IterationHistoryAction
                || action instanceof StoryAction))){
            
            isUnderReadOnlyAction = true;
            
            //log in read only user if we got to here
            UserDAOHibernate userDao = new UserDAOHibernate();
            
            SessionFactory sessionFactory = null;
            try {
                sessionFactory = (SessionFactory) new InitialContext().lookup("hibernateSessionFactory");
                userDao.setSessionFactory(sessionFactory);
            } catch (NamingException e) {
                e.printStackTrace();
            }
            Session session = sessionFactory.openSession();
            
            User user = userDao.getByLoginName("readonly");
            
            SecurityUtil.setLoggedUser(user);
            
            //push current user to the value stack
            invocation.getStack().set("currentUser", user);
            invocation.getStack().set("currentUserJson", new JSONSerializer().serialize(user));
            
            session.disconnect();
            session.close();
            
            // perform request
            String result = invocation.invoke();

            // after the request:
            // reset the logged user
            SecurityUtil.setLoggedUser(null);

            return result;
        }
                
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
        User user = userBusiness.retrieve(userId);

        // check that user hasn't been removed during the session
        if (user == null) {
            SecurityUtil.logoutCurrentUser();
        }
        // check that user hasn't been disabled during the session
        if (!user.isEnabled()) {
            SecurityUtil.logoutCurrentUser();
        }

        // before the request:
        // set this user as the logged user
        SecurityUtil.setLoggedUser(user);
        
        //push current user to the value stack
        invocation.getStack().set("currentUser", user);
        invocation.getStack().set("currentUserJson", new JSONSerializer().serialize(user));
        
        // perform request
        String result = invocation.invoke();

        // after the request:
        // reset the logged user
        SecurityUtil.setLoggedUser(null);

        return result;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

}
