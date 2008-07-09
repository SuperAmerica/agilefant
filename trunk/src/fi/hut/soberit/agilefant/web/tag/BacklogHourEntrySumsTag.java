package fi.hut.soberit.agilefant.web.tag;

import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;

public class BacklogHourEntrySumsTag extends SpringTagSupport {

    private static final long serialVersionUID = -2379325290467231410L;

    private HourEntryBusiness hourEntryBusiness;
    
    private Backlog target;
    
    private String groupBy = "BacklogItem";
    

    @Override
    public int doStartTag() throws JspException {
        
        hourEntryBusiness = (HourEntryBusiness) super.getApplicationContext().getBean(
                "hourEntryBusiness");
        
        Map<Integer,AFTime> sums = null;
        if(groupBy.equals("BacklogItem")) {
            sums = hourEntryBusiness.getSumsByBacklog( target ); 
        } else if(groupBy.equals("IterationGoal")) {
            sums = hourEntryBusiness.getSumsByIterationGoal( target );
        }
        
        super.getPageContext().setAttribute(super.getId(), sums);
        
        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setTarget(Backlog target) {
        this.target = target;
    }
    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }
}
