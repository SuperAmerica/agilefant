package fi.hut.soberit.agilefant.web;


import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TimesheetBusiness;

/* Note: this is just a mockup for timesheet actions */

public class TimesheetAction extends ActionSupport {

    private static final long serialVersionUID = -8988740967426943267L;
    
    private TimesheetBusiness timesheetBusiness;
    private int[] backlogIds;

    public int[] getBacklogIds() {
        return backlogIds;
    }

    public void setBacklogIds(int[] backlogIds) {
        this.backlogIds = backlogIds;
    }
    
    public String generateTree(){
        timesheetBusiness.generateTree(backlogIds);
        return Action.SUCCESS;
    }

    public TimesheetBusiness getTimesheetBusiness() {
        return timesheetBusiness;
    }

    public void setTimesheetBusiness(TimesheetBusiness timesheetBusiness) {
        this.timesheetBusiness = timesheetBusiness;
    }
}
