package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;

public class IterationGoalListTag extends SpringTagSupport{
	private static final long serialVersionUID = 1810440984222729034L;
	private Backlog backlog;
		
	@Override
	public int doStartTag() throws JspException {
		if (backlog instanceof Iteration) {
			super.getPageContext().setAttribute(super.getId(), ((Iteration)backlog).getIterationGoals());
		}
		return Tag.EVAL_BODY_INCLUDE;
	}
	public void setBacklog(Backlog backlog) {
		this.backlog = backlog;
	}
}
