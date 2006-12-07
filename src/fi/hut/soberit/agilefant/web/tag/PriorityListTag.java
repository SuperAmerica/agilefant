package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import fi.hut.soberit.agilefant.model.Priority;

public class PriorityListTag extends TagSupport {

	@Override
	public int doEndTag() throws JspException {
		super.pageContext.removeAttribute(this.getId());
		return Tag.EVAL_PAGE;
	}

	@Override
	public int doStartTag() throws JspException {
		super.pageContext.setAttribute(this.getId(), Priority.values());
		return Tag.EVAL_BODY_INCLUDE;
	}
}
