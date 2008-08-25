package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.ProjectTypeDAO;

public class ProjectTypeListTag extends SpringTagSupport {
   
    private static final long serialVersionUID = -1408209198511330494L;

    public static final String PROJECTTYPE_LIST_KEY = "projectTypeList";

    private ProjectTypeDAO projectTypeDAO;

    @Override
    public int doStartTag() throws JspException {
        projectTypeDAO = (ProjectTypeDAO) super.getApplicationContext().getBean(
                "projectTypeDAO");
        super.getPageContext().setAttribute(ProjectTypeListTag.PROJECTTYPE_LIST_KEY,
                projectTypeDAO.getAll());
        return Tag.EVAL_BODY_INCLUDE;
    }
}
