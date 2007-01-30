package fi.hut.soberit.agilefant.web.tag;

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;

public class IterationGoalListTag extends SpringTagSupport{
	private static final long serialVersionUID = 1810440984222729034L;
	private BacklogDAO backlogDAO;
	private int backlogId; 
		
	@Override
	public int doStartTag() throws JspException {
		backlogDAO = (BacklogDAO)super.getApplicationContext().getBean("backlogDAO");
		Backlog backlog = backlogDAO.get(backlogId);

		if (backlog instanceof Iteration) {
			IterationGoal dummy = new IterationGoal();
			dummy.setName("-");
			dummy.setId(0);
			
			Collection<IterationGoal> goals = ((Iteration)backlog).getIterationGoals();
			goals.add(dummy);
			super.getPageContext().setAttribute(super.getId(), goals);
		}
		return Tag.EVAL_BODY_INCLUDE;
	}
	
	public void setBacklogId(int backlogId) {
		this.backlogId = backlogId;
	}
}
