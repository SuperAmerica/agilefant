package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.ProjectDAO;

public class ProjectListTag extends SpringTagSupport {
    private static final long serialVersionUID = -7498764572144094830L;

    private ProjectDAO projectDAO;

    @Override
    public int doStartTag() throws JspException {
        projectDAO = requireBean("projectDAO");
        super.getPageContext().setAttribute(super.getId(),
                projectDAO.getAll());
        return Tag.EVAL_BODY_INCLUDE;
    }
}
