package fi.hut.soberit.agilefant.web;


import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

/* Note: this is just a mockup for timesheet actions */

public class TimesheetAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = 6404856922329136680L;
    
    public String create() {
        return Action.SUCCESS;
    }
    
    public String delete() {
       
        return Action.SUCCESS;
    }
    
    
    public String edit() {
      
        return Action.SUCCESS;
    }
    
    public String store() {
        
        return Action.SUCCESS;
    }
    
    
    
}
