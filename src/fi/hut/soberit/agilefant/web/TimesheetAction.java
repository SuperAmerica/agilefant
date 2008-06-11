package fi.hut.soberit.agilefant.web;


import java.util.List;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;

/**
 * 
 * @author Vesa Pirila / Spider
 *
 */
public class TimesheetAction extends ActionSupport {

    private static final long serialVersionUID = -8988740967426943267L;
    
    private TimesheetBusiness timesheetBusiness;

    private List<BacklogTimesheetNode> products;

    private int[] backlogIds;

    private String startDate;

    private String endDate;

    public int[] getBacklogIds() {
        return backlogIds;
    }

    public void setBacklogIds(int[] backlogIds) {
        this.backlogIds = backlogIds;
    }
    
    public String generateTree(){
        try{
            products = timesheetBusiness.generateTree(backlogIds, startDate, endDate);
        }catch(IllegalArgumentException e){
            // TODO: Add error message
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
}
