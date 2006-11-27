package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.security.SecurityUtil;

public class CurrentUserTag extends SpringTagSupport {
	
	public static final String CURRENT_USER_KEY = "currentUser";
	
	@Override
	public int doStartTag() throws JspException {
		super.getPageContext().setAttribute(CurrentUserTag.CURRENT_USER_KEY, SecurityUtil.getLoggedUser());
		return Tag.EVAL_BODY_INCLUDE;
	}
}
