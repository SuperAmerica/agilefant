package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.IterationDAO;

public class IterationListTag extends SpringTagSupport {
    private static final long serialVersionUID = 91424526293052792L;

    private IterationDAO iterationDAO;

    @Override
    public int doStartTag() throws JspException {
        iterationDAO = requireBean("iterationDAO");
        super.getPageContext().setAttribute(super.getId(),
                iterationDAO.getAll());
        return Tag.EVAL_BODY_INCLUDE;
    }
}
