package fi.hut.soberit.agilefant.web.tag;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Task;

public class HourReportTaskListTag extends SpringTagSupport {
    private static final long serialVersionUID = 7824612382511977328L;

    private UserDAO userDAO;

    private int userId;

    @Override
    public int doEndTag() throws JspException {
        // kludge to make separate forms for displaytag
        userDAO = (UserDAO) super.getApplicationContext().getBean("userDAO");

        Collection<Task> taskList = userDAO.getUnfinishedTasks(userDAO
                .get(userId));

        ArrayList<ArrayList<Task>> newTaskList = new ArrayList<ArrayList<Task>>();

        for (Task task : taskList) {
            ArrayList<Task> list = new ArrayList<Task>();
            list.add(task);
            newTaskList.add(list);
        }
        super.getPageContext().setAttribute(super.getId(), newTaskList);

        return Tag.EVAL_PAGE;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
