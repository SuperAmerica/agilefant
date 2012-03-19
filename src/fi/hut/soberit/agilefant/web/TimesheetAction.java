package fi.hut.soberit.agilefant.web;



import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Workbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.business.TimesheetExportBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.BacklogTimesheetNode;
import flexjson.JSONSerializer;

/**
 * 
 * @author Vesa Pirila / Spider
 * @author Pasi Pekkanen
 *
 */
@Component("timesheetAction")
@Scope("prototype")
public class TimesheetAction extends ActionSupport {

    private static final long serialVersionUID = -8988740967426943267L;
    
    @Autowired
    private TimesheetBusiness timesheetBusiness;
    
    @Autowired
    private TimesheetExportBusiness timesheetExportBusiness;

    @Autowired
    private UserBusiness userBusiness;
    
    @Autowired
    private BacklogBusiness backlogBusiness;
    
    private Set<Integer> productIds = new HashSet<Integer>();
    
    private Set<Integer> projectIds = new HashSet<Integer>();
    
    private Set<Integer> iterationIds = new HashSet<Integer>();
    
    private List<BacklogTimesheetNode> products;
    
    private DateTime startDate;

    private DateTime endDate;
    
    private String interval;
    
    private Set<Integer> userIds = new HashSet<Integer>();
        
    private boolean onlyOngoing = false;
    
    private long effortSum = 0;
    
    private ByteArrayOutputStream exportableReport;
   
    
    public Set<Integer> getSelectedBacklogs() {
        if(this.iterationIds.size() > 0) {
            return this.iterationIds;
        } else if(this.projectIds.size() > 0) {
            return this.projectIds;
        } else if(this.productIds.size() > 0) {
            return this.productIds;
        }
        return new HashSet<Integer>();
    }
    public String initialize() {
        this.interval = "TODAY";
        this.onlyOngoing = false;
        return Action.SUCCESS;
    }

    public String generateTree(){
        Set<Integer> selectedBacklogIds = this.getSelectedBacklogs();
        if(selectedBacklogIds == null || selectedBacklogIds.size() == 0) {
            addActionError("No backlogs selected.");
            return Action.ERROR;
        }        
        if (selectedBacklogIds.contains(0))
        {
            // Standalone Iterations
            selectedBacklogIds.remove(0);
            Collection<Backlog> iters = backlogBusiness.retrieveAllStandAloneIterations();
            for (Iterator<Backlog> i = iters.iterator();i.hasNext();){
                selectedBacklogIds.add(i.next().getId());
            }
        }
        products = timesheetBusiness.getRootNodes(selectedBacklogIds, startDate, endDate, this.userIds);
        effortSum = timesheetBusiness.getRootNodeSum(products);
        return Action.SUCCESS;
    }
    
    public String generateExeclReport(){
        Set<Integer> selectedBacklogIds = this.getSelectedBacklogs();
        if(selectedBacklogIds == null || selectedBacklogIds.size() == 0) {
            addActionError("No backlogs selected.");
            return Action.ERROR;
        }        
        if (selectedBacklogIds.contains(0))
        {
            // Standalone Iterations
            selectedBacklogIds.remove(0);
            Collection<Backlog> iters = backlogBusiness.retrieveAllStandAloneIterations();
            for (Iterator<Backlog> i = iters.iterator();i.hasNext();){
                selectedBacklogIds.add(i.next().getId());
            }
        }
        Workbook wb = this.timesheetExportBusiness.generateTimesheet(this, selectedBacklogIds, startDate, endDate, userIds);
        this.exportableReport = new ByteArrayOutputStream();
        try {
            wb.write(this.exportableReport);
        } catch (IOException e) {
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public List<User> getSelectedUsers() {
        if(this.userIds == null) {
            return Collections.emptyList();
        }
        List<User> selectedUsers = new ArrayList<User>();
        for(int userId : this.getUserIds()) {
            User user = this.userBusiness.retrieve(userId);
            if(user != null) {
                selectedUsers.add(user);
            }
        }
        return selectedUsers;
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

    public boolean isOnlyOngoing() {
        return onlyOngoing;
    }

    public void setOnlyOngoing(boolean onlyOngoing) {
        this.onlyOngoing = onlyOngoing;
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
    public long getEffortSum() {
        return effortSum;
    }
    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
    public void setTimesheetExportBusiness(
            TimesheetExportBusiness timesheetExportBusiness) {
        this.timesheetExportBusiness = timesheetExportBusiness;
    }
    public InputStream getSheetData() {
        return new ByteArrayInputStream(this.exportableReport.toByteArray());
    }
    public void setExportableReport(ByteArrayOutputStream exportableReport) {
        this.exportableReport = exportableReport;
    }
    
}
