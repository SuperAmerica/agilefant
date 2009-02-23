package fi.hut.soberit.agilefant.web;



import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.rememberme.RememberMeAuthenticationToken;

import com.opensymphony.webwork.interceptor.PrincipalAware;
import com.opensymphony.webwork.interceptor.PrincipalProxy;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.AgilefantUserDetails;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;
import flexjson.JSONSerializer;

/**
 * 
 * @author Vesa Pirila / Spider
 * @author Pasi Pekkanen
 *
 */
public class TimesheetAction extends ActionSupport implements PrincipalAware {

    private static final long serialVersionUID = -8988740967426943267L;
    
    private TimesheetBusiness timesheetBusiness;
    
    private UserBusiness userBusiness;

    private Set<Integer> productIds = new HashSet<Integer>();
    
    private Set<Integer> projectIds = new HashSet<Integer>();
    
    private Set<Integer> iterationIds = new HashSet<Integer>();
    
    private List<BacklogTimesheetNode> products;
    
    private List<Integer> selected = new ArrayList<Integer>();
    
    private List<User> selUser = new ArrayList<User>();
    
    private UserDAO userDAO;

    private int[] backlogIds;

    private String startDate;

    private String endDate;
    
    private String interval;
    
    private Set<Integer> userIds = new HashSet<Integer>();
    
    private int backlogSelectionType = 0;
    
    private boolean onlyOngoing = false;
    
    private AFTime totalSpentTime;

    private int currentUserId = 0;
    
    public int[] getBacklogIds() {
        return backlogIds;
    }

    public void setBacklogIds(int[] backlogIds) {
        this.backlogIds = backlogIds;
    }
    
    /**
     * Needed for xwork's execAndWait as action is executed in a different
     * thread than the wait page. Thus no static threadLocal based principals
     * (such as those in SecurityUtil) can be used.
     */
    public void setPrincipalProxy(PrincipalProxy principalProxy) {
        Principal principal = principalProxy.getUserPrincipal();
        AgilefantUserDetails ud;
        if (principal instanceof RememberMeAuthenticationToken) {
            ud = (AgilefantUserDetails) ((RememberMeAuthenticationToken) principal)
                    .getPrincipal();
        } else {
            ud = (AgilefantUserDetails) ((UsernamePasswordAuthenticationToken) principal)
                    .getPrincipal();
        }
        currentUserId = ud.getUserId();
        
    }
    
    private List<Integer> selectedBacklogs() {
        List<Integer> ret = new ArrayList<Integer>();
        if(this.projectIds.contains(-1)) {
            ret.addAll(this.productIds);
        } else if(this.iterationIds.contains(-1)) {
            ret.addAll(this.projectIds);
        } else {
            if(this.projectIds.size() == 0) {
                ret.addAll(this.productIds);
            } else if(this.iterationIds.size() == 0) {
                ret.addAll(this.projectIds);
            } else {
                ret.addAll(this.iterationIds);
            }
        }
        return ret;
    }
    public String initialize() {
        return Action.SUCCESS;
    }
    public String generateTree(){
        List<Integer> ids = null;
        Set<Integer> users = new HashSet<Integer>();
        if(backlogSelectionType == 0) {
            ids = this.selectedBacklogs();
            users.addAll(userIds);
        } else {
            Collection<Backlog> tmp = userBusiness.getOngoingBacklogsByUser(currentUserId);
            ids = new ArrayList<Integer>();
            for(Backlog bl : tmp) {
                ids.add(bl.getId());
            }
            //only for current user:
            users.add(currentUserId);
        }
        if(ids == null || ids.size() == 0) {
            addActionError("No backlogs selected.");
            return Action.ERROR;
        }
        try{
            products = timesheetBusiness.generateTree(ids, startDate, endDate, users);
            totalSpentTime = timesheetBusiness.calculateRootSum(products);
        }catch(IllegalArgumentException e){
            addActionError(e.getMessage());
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public TimesheetBusiness getTimesheetBusiness() {
        return timesheetBusiness;
    }

    public List<BacklogTimesheetNode> getProducts() {
        return products;
    }

    /**
     * This should not be used anywhere
     * @param products
     */
    public void setProducts(List<BacklogTimesheetNode> products) {
        this.products = products;
    }

    public void setTimesheetBusiness(TimesheetBusiness timesheetBusiness) {
        this.timesheetBusiness = timesheetBusiness;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public List<Integer> getSelected() {
        this.selected.clear();
        for(int sel : backlogIds) {
            this.selected.add(sel);
        }
        return selected;
    }

    public List<User> getSelUser() {
        this.selUser.clear();
        for(int sel: userIds) {
            this.selUser.add(userDAO.get(sel));
        }
        return selUser;
    }

    public AFTime getTotalSpentTime() {
        return totalSpentTime;
    }

    public void setTotalSpentTime(AFTime totalSpentTime) {
        this.totalSpentTime = totalSpentTime;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public Set<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(Set<Integer> productIds) {
        this.productIds = productIds;
    }

    public Set<Integer> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(Set<Integer> projectIds) {
        this.projectIds = projectIds;
    }

    public Set<Integer> getIterationIds() {
        return iterationIds;
    }

    public void setIterationIds(Set<Integer> iterationIds) {
        this.iterationIds = iterationIds;
    }
    
    public String getJSONProducts() {
        return new JSONSerializer().serialize(this.productIds);
    }
    public String getJSONProjects() {
        return new JSONSerializer().serialize(this.projectIds);
    }
    public String getJSONIterations() {
        return new JSONSerializer().serialize(this.iterationIds);
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public int getBacklogSelectionType() {
        return backlogSelectionType;
    }

    public void setBacklogSelectionType(int backlogSelectionType) {
        this.backlogSelectionType = backlogSelectionType;
    }

    public boolean isOnlyOngoing() {
        return onlyOngoing;
    }

    public void setOnlyOngoing(boolean onlyOngoing) {
        this.onlyOngoing = onlyOngoing;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }
    
    
}
