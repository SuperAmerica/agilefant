package fi.hut.soberit.agilefant.web.tag;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;


public class HourEntryTag extends SpringTagSupport {

    private static final long serialVersionUID = 692273515131699157L;

    private HourEntryBusiness hourEntryBusiness;
    
    private TimesheetLoggable target;
    
    @Override
    @SuppressWarnings("all")
    public int doStartTag() throws JspException {
        List list = null;
        hourEntryBusiness = (HourEntryBusiness) super.getApplicationContext().getBean(
            "hourEntryBusiness");

        
        if(target instanceof BacklogItem) {
            BacklogItem item = (BacklogItem) target;
            list = (List<BacklogItemHourEntry>) hourEntryBusiness.getEntriesByParent( item );
        } else if(target instanceof Project) {
            Backlog item = (Backlog) target;
            list = (List<BacklogHourEntry>) hourEntryBusiness.getEntriesByParent( item );
        }
   
        /*
        for( HourEntry h : list ){
            System.out.println( "-->" + h.getId() );
        }
        */
        super.getPageContext().setAttribute(super.getId(), list);
        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setTarget(TimesheetLoggable target) {
        //System.out.println( ">>" + target.timesheetType() + " " + target.getId() );
        this.target = target;
    }
}
