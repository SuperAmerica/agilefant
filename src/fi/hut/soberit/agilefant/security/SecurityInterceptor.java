package fi.hut.soberit.agilefant.security;

import java.util.Collection;
import java.util.Iterator;

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
            //check matrix operations
            //TODO whitelisting may be easier than blacklisting?
            if(requestUrl.contains("/storeNewUser")
                    || requestUrl.contains("/deleteTeam")
                    || requestUrl.contains("/storeTeam")
                    || requestUrl.contains("/storeNewTeam")
                    || requestUrl.contains("/retrieveAllProducts")){
                access = false;
            } else if(requestUrl.matches("ajax/storeUser.action")){
                //check if ID is of current user, and what is being stored
                //can't set user.admin or team
                int id = Integer.parseInt(getParamFromUrl("id", requestUrl));
                if(id == user.getId()){
                    //check not setting user.admin
                    access = true;
                }
            }
            //OMG there's a lot!
            //I think most important ones are in web/static/js/dynamics/model/*
        }
                
        if(access)
            return invocation.invoke();
        else
            return "noauth";
    }

    private String getParamFromUrl(String param, String url) {
        if(url != null) {
            int tokenStart = url.indexOf(param + "=");
            String token = url.substring(tokenStart + param.length() + 1);
            return token;
        }
        else 
            return "";
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
