package fi.hut.soberit.agilefant.web.tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;

public class ResponsibleColumnTag extends SpringTagSupport {
    private static final long serialVersionUID = 1810440984222729034L;

    private int storyId;
    private StoryBusiness storyBusiness;
    private ProjectBusiness projectBusiness;

    @Override
    public int doStartTag() throws JspException {
        storyBusiness = requireBean("storyBusiness");
        projectBusiness = requireBean("projectBusiness");

        Story story = storyBusiness.retrieve(storyId);

        Project project;

        Collection<User> assignedUsers = new ArrayList<User>();

        if (story.getBacklog() instanceof Iteration) {
            project = (Project)story.getBacklog().getParent();
            assignedUsers = projectBusiness.getAssignedUsers(project);
        } else if (story.getBacklog() instanceof Project) {
            project = (Project)story.getBacklog();
            assignedUsers = projectBusiness.getAssignedUsers(project);
        } else {
            project = null;
        }

        String printString = "<span>";

        int i = 0;

        for (User user : story.getResponsibles()) {
            if (!assignedUsers.contains(user) && project != null) {
                printString += "<span class=\"unassigned\">" + user.getInitials().trim() + "</span>";
            } else {
                printString += user.getInitials().trim();
            }

            if (i != (story.getResponsibles().size() - 1)) {
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

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }
    
    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }
}
