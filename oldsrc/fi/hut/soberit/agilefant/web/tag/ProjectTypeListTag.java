package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.ProjectTypeBusiness;

public class ProjectTypeListTag extends SpringTagSupport {
   
    private static final long serialVersionUID = -1408209198511330494L;

    public static final String PROJECTTYPE_LIST_KEY = "projectTypeList";

    private ProjectTypeBusiness projectTypeBusiness;

    @Override
    protected void retrieveSingletons() {
        projectTypeBusiness = requireBean("projectTypeBusiness");
    }

    @Override
    public int doStartTag() throws JspException {
        super.getPageContext().setAttribute(ProjectTypeListTag.PROJECTTYPE_LIST_KEY,
                projectTypeBusiness.getAll());
        return Tag.EVAL_BODY_INCLUDE;
    }
}
