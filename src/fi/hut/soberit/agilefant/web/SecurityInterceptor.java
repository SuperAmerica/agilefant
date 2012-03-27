package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

@Component("securityInterceptor")
public class SecurityInterceptor implements Interceptor {

    @Autowired
    private BacklogBusiness backlogBusiness;
    
    @Autowired
    private IterationBusiness iterationBusiness;
    
    @Override
    public void destroy() {
    }

    @Override
    public void init() {
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        System.out.println("URL: " + ServletActionContext.getRequest().getRequestURL().toString());
        HttpServletRequest req = ServletActionContext.getRequest();
        String requestUrl = ServletActionContext.getRequest().getRequestURL().toString();
        
        User user = SecurityUtil.getLoggedUser();
        boolean admin = user.isAdmin();
        boolean readOnly = user.getName().equals("readonly");
        boolean access = false;
        
        if(admin){
            //if admin, everything is fine
            access = true;
        } else if(readOnly){
            //check read only operations
            if(requestUrl.contains("/ROIterationHistoryByToken") 
                    || requestUrl.contains("/ROIterationMetricsByToken")
                    || requestUrl.contains(("/ROIterationData"))){
                access = true;
            }
        } else {
            //check non-admin and matrix operations
            if(requestUrl.contains("/storeNewUser")
                    || requestUrl.contains("/deleteTeam")
                    || requestUrl.contains("/storeTeam")
                    || requestUrl.contains("/storeNewTeam")
                    || requestUrl.contains("/retrieveAllProducts")){
                access = false;
            } else if(requestUrl.contains("ajax/storeUser.action")){
                //check if ID is of current user, and what is being stored
                //can't set user.admin or team
                Map params = req.getParameterMap();
                boolean attemptAdmin = params.containsKey("user.admin");
                int id = Integer.parseInt(((String[]) params.get("userId"))[0]);
                
                if(id == user.getId() && !attemptAdmin){
                    //check not setting user.admin
                    access = true;
                }
            } else {
                access = true;
            }
        }
                
        if(access)
            return invocation.invoke();
        else
            return "noauth";
    }
    
    // check from the backlogId if the associated product is accessible for the current user    
    private boolean checkAccess(int backlogId){
        User user = SecurityUtil.getLoggedUser();
        Collection<Team> teams = user.getTeams();
        
        Product product = (backlogBusiness.getParentProduct(backlogBusiness.retrieve(backlogId)));
        if(product == null){
            //standalone iteration
            Iteration iteration = iterationBusiness.retrieve(backlogId);
            if(iteration.isStandAlone()){
                for (Iterator<Team> iter = teams.iterator(); iter.hasNext();){
                    Team team = (Team) iter.next();
                    if (team.getIterations().contains(iteration)) {
                        return true; 
                    }
                }
                return false;
            }
        }

        for (Iterator<Team> iter = teams.iterator(); iter.hasNext();){
            Team team = (Team) iter.next();
            if (team.getProducts().contains(product)) {
                return true; 
            }
        }
        return false;
    }
}
