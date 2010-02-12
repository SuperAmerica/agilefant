package fi.hut.soberit.agilefant.transfer;

import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BeanCopier;
import flexjson.JSON;

public class ProjectTO extends Project {

    private ScheduleStatus scheduleStatus;
    
    private List<StoryTO> leafStories; 
    
    private Set<User> assignees;

    public ProjectTO(Project project) {
        BeanCopier.copy(project, this);
    }
    
    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }

    @JSON
    public List<StoryTO> getLeafStories() {
        return leafStories;
    }

    public void setLeafStories(List<StoryTO> leafStories) {
        this.leafStories = leafStories;
    }

    public Set<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<User> assignees) {
        this.assignees = assignees;
    }

}
