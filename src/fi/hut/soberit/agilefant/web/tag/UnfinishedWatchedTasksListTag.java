package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class UnfinishedWatchedTasksListTag extends SpringTagSupport {
	private static final long serialVersionUID = 8445060838827472218L;
	public static final String UNFINISHED_TASK_LIST_KEY = "unfinishedWatchedTasksList";
	private UserDAO userDAO;
		
	@Override
	public int doStartTag() throws JspException {
		
		userDAO = (UserDAO)super.getApplicationContext().getBean("userDAO");
		
		super.getPageContext().setAttribute(
				UnfinishedWatchedTasksListTag.UNFINISHED_TASK_LIST_KEY, 
				userDAO.getUnfinishedWatchedTasks( SecurityUtil.getLoggedUser() ));
		
		return Tag.EVAL_BODY_INCLUDE;
	}
}
