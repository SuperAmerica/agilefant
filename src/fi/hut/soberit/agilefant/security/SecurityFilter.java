package fi.hut.soberit.agilefant.security;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.GenericFilterBean;

import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;

public class SecurityFilter extends GenericFilterBean {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        
        // Our passed-in requests and responses are HttpServlet requests and responses.
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest reqt = (HttpServletRequest) request;
        
        String requestUrl = reqt.getRequestURL().toString();
        
        User user = SecurityUtil.getLoggedUser();
        
        if(user == null){
            //TODO this should not be null!
        } else {
            boolean admin = user.isAdmin();
            boolean readOnly = user.getName().equals("readonly");
            boolean access = false;
            
            if(admin){
              //if admin, everything is fine
              chain.doFilter(request, response);
            } else {         
                if(readOnly){
                    //check readOnly operations
                    if(requestUrl.contains("/ROIterationHistoryByToken") 
                            || requestUrl.contains("/ROIterationMetricsByToken")
                            || requestUrl.contains(("/ROIterationData"))){
                        access = true;
                    }
                    //TODO whitelisting may be easier than blacklisting!
                    if(requestUrl.contains("/storeNewUser")
                            || requestUrl.contains("/deleteTeam")
                            || requestUrl.contains("/storeTeam")
                            || requestUrl.contains("/storeNewTeam")
                            || requestUrl.contains("/retrieveAllProducts")){
                        access = false;
                    }
                } else {
                    if(requestUrl.matches("ajax/storeUser.action")){
                        //check if ID is of current user, and what is being stored
                        //can't set user.admin or team
                        int id = Integer.parseInt(getParamFromUrl("id", requestUrl));
                        
                    }
                }
            
                //OMG there's a lot!
                //I think most important ones are in web/static/js/dynamics/model/*
                
            
                if(access)
                    chain.doFilter(request, response);
                else
                    resp.sendRedirect("/agilefant/login.jsp");
            }
        }
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
        //Product product = (backlogBusiness.getParentProduct(backlogBusiness.retrieve(backlogId)));
        User user = SecurityUtil.getLoggedUser();
        Collection<Team> teams = user.getTeams();
        for (Iterator<Team> iter = teams.iterator(); iter.hasNext();){
            Team team = (Team) iter.next();
            Collection<Product> products = team.getProducts();
            for (Iterator iterator = products.iterator(); iterator.hasNext();) {
                Product product = (Product) iterator.next();
                if(product.getId() == backlogId){
                    return true;
                }
            }
            /*if (team.getProducts().contains(product)) {
                return false; 
            }*/
        }
        return false;
    }
}
