package fi.hut.soberit.agilefant.web.tag;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;


public class HourEntryTag extends SpringTagSupport {

    private static final long serialVersionUID = 692273515131699157L;

    private HourEntryBusiness hourEntryBusiness;
    
    private BacklogItem target;
    
    @Override
    public int doStartTag() throws JspException {
        
        hourEntryBusiness = (HourEntryBusiness) super.getApplicationContext().getBean(
                "hourEntryBusiness");
        
        List<BacklogItemHourEntry> list = (List<BacklogItemHourEntry>) hourEntryBusiness.getEntriesByBacklogItem( target );
   
        /*
        for( HourEntry h : list ){
            System.out.println( "-->" + h.getId() );
        }
        */
        
        super.getPageContext().setAttribute(super.getId(), list);
        
        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setTarget(BacklogItem target) {
        //System.out.println( ">>" + target.timesheetType() + " " + target.getId() );
        this.target = target;
    }
}
