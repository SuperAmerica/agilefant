package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.model.BacklogItem;

public class BacklogItemById extends SpringTagSupport {

    private static final long serialVersionUID = -3657463719204991267L;
    private int backlogItemId;

    @Override
    public int doStartTag() throws JspException {
        BacklogItemBusiness backlogItemBusiness = requireBean("backlogItemBusiness");

        BacklogItem bli = null;
        if (backlogItemId > 0) {
            bli = backlogItemBusiness.getBacklogItem(backlogItemId);
        }
        if (bli == null) {
            bli = new BacklogItem();
        }
        super.getPageContext().setAttribute(super.getId(), bli);
        return Tag.EVAL_BODY_INCLUDE;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }
}
