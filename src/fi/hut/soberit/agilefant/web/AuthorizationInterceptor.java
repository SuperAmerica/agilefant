package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

@Component("authorizationInterceptor")
public class AuthorizationInterceptor implements Interceptor {

    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    private BacklogBusiness backlogBusiness;
   
    @Override
    public void destroy() {}

    @Override
    public void init() {}

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();
        boolean accessDenied = false;
        
        //matrix authorizations
        if (action instanceof BacklogAction){
            accessDenied = checkAccess(((BacklogAction) action).getBacklogId()); 
        } else if (action instanceof ProductAction) {
            accessDenied = checkAccess(((ProductAction) action).getProductId());  
        } else if (action instanceof ProjectAction) {
            accessDenied = checkAccess(((ProjectAction) action).getProjectId());      
        } else if (action instanceof IterationAction) {
            accessDenied = checkAccess(((IterationAction) action).getIterationId());
        } else {
            //admin authorizations
            User currentUser = SecurityUtil.getLoggedUser();
            boolean isAdmin = currentUser.isAdmin();
            
            if(isAdmin){
                //admin has access to any actions
                return invocation.invoke();
            } else {
                if(action instanceof AccessAction || 
                   action instanceof DatabaseExportAction ||
                   action instanceof SettingAction){
                         return "notadmin";
                } else {
                    return invocation.invoke();
                }
            }
        }
        
        if (accessDenied) return "noauth";
            return invocation.invoke();
    }
    
    // check from the backlogId if the associated product is accessible for the current user    
    private boolean checkAccess(int backlogId){
        Product product = (backlogBusiness.getParentProduct(backlogBusiness.retrieve(backlogId)));
        User user = SecurityUtil.getLoggedUser();
        Collection<Team> teams = user.getTeams();
        for (Iterator<Team> iter = teams.iterator(); iter.hasNext();){
            Team team = (Team) iter.next();
            if (team.getProducts().contains(product)) {
                return false; 
            }
        }
        return true;
    }

}