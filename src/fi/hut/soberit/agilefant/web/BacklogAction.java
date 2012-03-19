package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

@Component("backlogAction")
@Scope("prototype")
public class BacklogAction extends ActionSupport {
    private static final long serialVersionUID = 8061288993804046816L;

    private int backlogId;

    private Set<Integer> userIds = new HashSet<Integer>();

    private Backlog backlog;
    
    private Collection<Story> stories;
    
    private Collection<Backlog> backlogs;

    @Autowired
    private BacklogBusiness backlogBusiness;

    
    public String retrieve() {
        backlog = backlogBusiness.retrieve(backlogId);
        return Action.SUCCESS;
    }
    
    public String retrieveStories() {
        backlog = backlogBusiness.retrieve(backlogId);
        stories = backlog.getStories();
        return Action.SUCCESS;
    }
    
    public String addAssignees() {
        this.backlogBusiness.addAssignees(backlogId, userIds);
        this.backlog = this.backlogBusiness.retrieve(backlogId);
        return Action.SUCCESS;
    }
    
    /**
     * Gets all sub backlogs or all products if backlog not found.
     * @return
     */
    public String retrieveSubBacklogs() {
        if (backlogId == 0) {
            // TODO: Retrieve standalone iterations
            backlogs = backlogBusiness.retrieveAllStandAloneIterations();
        }
        else {
            backlog = backlogBusiness.retrieveIfExists(backlogId);
            backlogs = backlogBusiness.getChildBacklogs(backlog);
        }
        return Action.SUCCESS;
    }


    public String resolveResult() {
        backlog = backlogBusiness.retrieveIfExists(backlogId);
        if (backlog instanceof Product) {
            return "product";
        }
        else if (backlog instanceof Project) {
            return "project";
        }
        else if (backlog instanceof Iteration) {
            return "iteration";
        }
        return "selectBacklog";
    }
    
    
    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public Set<Integer> getUserIds() {
        return userIds;
    }

    public void setUserIds(Set<Integer> userIds) {
        this.userIds = userIds;
    }
    
    public Backlog getBacklog() {
        return backlog;
    }

    public int getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }
    
    public Collection<Story> getStories() {
        return stories;
    }

    public Collection<Backlog> getBacklogs() {
        return backlogs;
    }
}
