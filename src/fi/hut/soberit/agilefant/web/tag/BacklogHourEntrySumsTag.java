package fi.hut.soberit.agilefant.web.tag;

import java.util.List;
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
    
    @Override
    public int doStartTag() throws JspException {
        
        hourEntryBusiness = (HourEntryBusiness) super.getApplicationContext().getBean(
                "hourEntryBusiness");
        
        Map<Integer,AFTime> sums = hourEntryBusiness.getSumsByBacklog( target );
        super.getPageContext().setAttribute(super.getId(), sums);
        
        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setTarget(Backlog target) {
        this.target = target;
    }
    
}
