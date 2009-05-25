package fi.hut.soberit.agilefant.web.tag;

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;

public class IterationGoalListTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729034L;

    private BacklogBusiness backlogBusiness;

    private int backlogId;

    @Override
    public int doStartTag() throws JspException {
        backlogBusiness = requireBean("backlogBusiness");
        Backlog backlog;
        try {
            backlog = backlogBusiness.getBacklog(backlogId);
        } catch (ObjectNotFoundException e) {
            throw new JspException(e);
        }
        if (backlog instanceof Iteration) {
            Collection<IterationGoal> goals = ((Iteration) backlog)
                    .getIterationGoals();
            super.getPageContext().setAttribute(super.getId(), goals);
        }
        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }
}
