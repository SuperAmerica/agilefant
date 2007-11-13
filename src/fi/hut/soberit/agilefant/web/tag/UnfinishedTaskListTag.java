package fi.hut.soberit.agilefant.web.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.UserDAO;

public class UnfinishedTaskListTag extends SpringTagSupport {
	private static final long serialVersionUID = 7824612382511977328L;

	public static final String UNFINISHED_TASK_LIST_KEY = "unfinishedTaskList";

	private UserDAO userDAO;

	private int id;

	@Override
	public int doStartTag() throws JspException {

		userDAO = (UserDAO) super.getApplicationContext().getBean("userDAO");

		super.getPageContext().setAttribute(
				UnfinishedTaskListTag.UNFINISHED_TASK_LIST_KEY,
				userDAO.getUnfinishedTasks(userDAO.get(id)));

		return Tag.EVAL_BODY_INCLUDE;
	}

	public void setUserId(int id) {
		this.id = id;
	}
}
