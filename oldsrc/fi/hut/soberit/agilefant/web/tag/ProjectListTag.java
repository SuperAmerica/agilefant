package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.ProjectBusiness;

public class ProjectListTag extends SpringTagSupport {
    private static final long serialVersionUID = -7498764572144094830L;

    private ProjectBusiness projectBusiness;

    @Override
    public int doStartTag() throws JspException {
        projectBusiness = requireBean("projectBusiness");
        super.getPageContext().setAttribute(super.getId(),
                projectBusiness.getAll());
        return Tag.EVAL_BODY_INCLUDE;
    }

}
