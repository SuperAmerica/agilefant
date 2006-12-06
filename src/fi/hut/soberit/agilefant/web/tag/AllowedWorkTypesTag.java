package fi.hut.soberit.agilefant.web.tag;

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.WorkType;

public class AllowedWorkTypesTag extends SpringTagSupport {
	
	public static final String WORK_TYPE_LIST_KEY = "workTypeList";
	private BacklogItem backlogItem;
	
	@Override
	public int doStartTag() throws JspException {
		Collection<WorkType> result = null;		
		if (backlogItem.getBacklog() instanceof Product){
			WorkTypeDAO workTypeDAO = (WorkTypeDAO)super.getApplicationContext().getBean("workTypeDAO");
			result = workTypeDAO.getAll();
		} else {
			Deliverable deliverable = null;
			if (backlogItem.getBacklog() instanceof Deliverable){
				deliverable = (Deliverable)backlogItem.getBacklog();
			} else {
				deliverable = ((Iteration)backlogItem.getBacklog()).getDeliverable();
			}
			if (deliverable.getActivityType() != null){
				result = deliverable.getActivityType().getWorkTypes();
			}			
		}
		super.getPageContext().setAttribute(AllowedWorkTypesTag.WORK_TYPE_LIST_KEY, result);
		
		return Tag.EVAL_BODY_INCLUDE;
	}
	
	public void setBacklogItem(BacklogItem backlogItem){
		this.backlogItem = backlogItem;
	}
}
