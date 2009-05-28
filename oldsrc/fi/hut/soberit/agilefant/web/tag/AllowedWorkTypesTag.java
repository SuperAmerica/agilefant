package fi.hut.soberit.agilefant.web.tag;

import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.WorkType;

public class AllowedWorkTypesTag extends SpringTagSupport {

    private static final long serialVersionUID = 3055272530897380319L;
    private BacklogItem backlogItem;

    @Override
    public int doEndTag() throws JspException {
        super.getPageContext().removeAttribute(super.getId());
        return Tag.EVAL_PAGE;
    }

    @Override
    public int doStartTag() throws JspException {
        Collection<WorkType> result = null;
        if (backlogItem.getBacklog() instanceof Product) {
            WorkTypeDAO workTypeDAO = requireBean("workTypeDAO");
            result = workTypeDAO.getAll();
        } else {
            Project project = null;
            if (backlogItem.getBacklog() instanceof Project) {
                project = (Project) backlogItem.getBacklog();
            } else {
                project = ((Iteration) backlogItem.getBacklog())
                        .getProject();
            }
            if (project.getProjectType() != null) {
                result = project.getProjectType().getWorkTypes();
            }
        }
        super.getPageContext().setAttribute(super.getId(), result);

        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setBacklogItem(BacklogItem backlogItem) {
        this.backlogItem = backlogItem;
    }
}
