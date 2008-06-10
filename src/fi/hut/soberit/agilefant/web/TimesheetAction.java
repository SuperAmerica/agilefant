package fi.hut.soberit.agilefant.web;


import java.util.List;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;

/* Note: this is just a mockup for timesheet actions */

public class TimesheetAction extends ActionSupport {

    private static final long serialVersionUID = -8988740967426943267L;
    
    private TimesheetBusiness timesheetBusiness;
    private List<BacklogTimesheetNode> products;
    private int[] backlogIds;

    public int[] getBacklogIds() {
        return backlogIds;
    }

    public void setBacklogIds(int[] backlogIds) {
        this.backlogIds = backlogIds;
    }
    
    public String generateTree(){
        products = timesheetBusiness.generateTree(backlogIds);
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
}
