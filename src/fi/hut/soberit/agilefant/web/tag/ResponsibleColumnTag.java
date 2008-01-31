package fi.hut.soberit.agilefant.web.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;

public class ResponsibleColumnTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729034L;

    private int backlogItemId;
    private BacklogItemDAO backlogItemDAO;
    private BacklogItemBusiness backlogItemBusiness;
    
    @Override
    public int doStartTag() throws JspException {
        backlogItemDAO = (BacklogItemDAO) super.getApplicationContext().getBean(
                "backlogItemDAO");
        backlogItemBusiness = (BacklogItemBusiness) super.getApplicationContext().
            getBean("backlogItemBusiness");
        
        BacklogItem bli = backlogItemDAO.get(backlogItemId);
        
        String printString = backlogItemBusiness.getResponsibleInitialsString(bli);
        
        try {
            super.getPageContext().getOut().print(printString);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }
}
