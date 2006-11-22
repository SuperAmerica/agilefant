package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.UserDAO;

public class UserListTag extends SpringTagSupport {

	public static final String USER_LIST_KEY = "userList";
	private UserDAO userDAO;
		
	@Override
	public int doStartTag() throws JspException {
		userDAO = (UserDAO)super.getApplicationContext().getBean("userDAO");
		super.getPageContext().setAttribute(UserListTag.USER_LIST_KEY, userDAO.getAll());
		return Tag.EVAL_BODY_INCLUDE;
	}
}
