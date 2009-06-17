package fi.hut.soberit.agilefant.web;



import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.rememberme.RememberMeAuthenticationToken;
import org.springframework.stereotype.Component;

import com.opensymphony.webwork.interceptor.PrincipalAware;
import com.opensymphony.webwork.interceptor.PrincipalProxy;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.security.AgilefantUserDetails;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;
import fi.hut.soberit.agilefant.util.TimesheetData;

/**
 * 
 * @author Vesa Pirila / Spider
 * @author Pasi Pekkanen
 *
 */
@Component("timesheetAction")
public class TimesheetAction extends ActionSupport implements PrincipalAware {

    private static final long serialVersionUID = -8988740967426943267L;
    
    private TimesheetBusiness timesheetBusiness;
    
    private Set<Integer> productIds = new HashSet<Integer>();
    
    private Set<Integer> projectIds = new HashSet<Integer>();
    
    private Set<Integer> iterationIds = new HashSet<Integer>();
    
    private List<BacklogTimesheetNode> products;
    
    private DateTime startDate;

    private DateTime endDate;
    
    private String interval;
    
    private Set<Integer> userIds = new HashSet<Integer>();
    
    private int currentUserId = 0;
    
    private boolean onlyOngoing = true;
    
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
    /*
     * -1 in product, project or iteration id array remarks "select all" option
     */
    public Set<Integer> getSelectedBacklogs() {
        Set<Integer> ret = new HashSet<Integer>();
        if(this.projectIds.contains(-1)) {
            if(this.onlyOngoing) {
                ret.addAll(this.projectIds);
            } else {
                ret.addAll(this.productIds);
            }
        } else if(this.iterationIds.contains(-1)) {
             if(this.onlyOngoing) {
                ret.addAll(this.iterationIds);
            } else {
                ret.addAll(this.projectIds);
            }
        } else {
            if(this.projectIds.size() == 0) {
                ret.addAll(this.productIds);
            } else if(this.iterationIds.size() == 0) {
                ret.addAll(this.projectIds);
            } else {
                ret.addAll(this.iterationIds);
            }
        }
        ret.remove(-1);
        return ret;
    }
    public String initialize() {
        this.interval = "TODAY";
        this.onlyOngoing = false;
        this.userIds.add(this.currentUserId);
        return Action.SUCCESS;
    }
    public String generateTree(){
        Set<Integer> selectedBacklogIds = this.getSelectedBacklogs();
        if(selectedBacklogIds == null || selectedBacklogIds.size() == 0) {
            addActionError("No backlogs selected.");
            return Action.ERROR;
        }
        TimesheetData dataSheet = timesheetBusiness.generateTimesheet(selectedBacklogIds, startDate, endDate, this.userIds);
        products = timesheetBusiness.getRootNodes(dataSheet);
        return Action.SUCCESS;
    }

    public TimesheetBusiness getTimesheetBusiness() {
        return timesheetBusiness;
    }

    public void setTimesheetBusiness(TimesheetBusiness timesheetBusiness) {
        this.timesheetBusiness = timesheetBusiness;
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

    public List<BacklogTimesheetNode> getProducts() {
        return products;
    }

    public void setProducts(List<BacklogTimesheetNode> products) {
        this.products = products;
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DateTime startDate) {
        this.startDate = startDate;
    }

    public DateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DateTime endDate) {
        this.endDate = endDate;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(int currentUserId) {
        this.currentUserId = currentUserId;
    }

    public boolean isOnlyOngoing() {
        return onlyOngoing;
    }

    public void setOnlyOngoing(boolean onlyOngoing) {
        this.onlyOngoing = onlyOngoing;
    }
    
}
