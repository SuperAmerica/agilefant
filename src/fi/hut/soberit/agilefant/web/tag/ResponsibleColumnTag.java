package fi.hut.soberit.agilefant.web.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;

public class ResponsibleColumnTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729034L;

    private int backlogItemId;
    private BacklogItemDAO backlogItemDAO;
    private BacklogBusiness backlogBusiness;

    @Override
    public int doStartTag() throws JspException {
        backlogItemDAO = (BacklogItemDAO) super.getApplicationContext()
                .getBean("backlogItemDAO");
        backlogBusiness = (BacklogBusiness) super.getApplicationContext()
                .getBean("backlogBusiness");

        BacklogItem bli = backlogItemDAO.get(backlogItemId);

        Backlog backlog;

        Collection<User> assignedUsers = new ArrayList<User>();

        if (bli.getBacklog() instanceof Iteration) {
            backlog = ((Iteration) bli.getBacklog()).getProject();
            assignedUsers = backlogBusiness.getUsers(backlog, true);
        } else if (bli.getBacklog() instanceof Project) {
            backlog = bli.getBacklog();
            assignedUsers = backlogBusiness.getUsers(backlog, true);
        } else {
            backlog = null;
        }

        String printString = "<span>";

        int i = 0;

        for (User user : bli.getResponsibles()) {
            if (!assignedUsers.contains(user) && backlog != null) {
                /*printString +=
                    "<a href=\"dailyWork.action?userId=" + user.getId() + "\" class=\"unassigned\">"    
                    + user.getInitials().trim() + "</a>";
                 */
                printString += "<span class=\"unassigned\">" + user.getInitials().trim() + "</span>";
            } else {
                /*
                printString += "<a href=\"dailyWork.action?userId=" + user.getId() + "\">"
                    + user.getInitials().trim() + "</a>";
                */
                printString += user.getInitials().trim();
            }

            if (i != (bli.getResponsibles().size() - 1)) {
                printString += ", ";
            }
            i++;
        }

        printString += "</span>";

        try {
            super.getPageContext().getOut().print(printString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Tag.EVAL_BODY_INCLUDE;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }
}
