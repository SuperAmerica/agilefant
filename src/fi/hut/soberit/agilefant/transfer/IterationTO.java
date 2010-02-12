package fi.hut.soberit.agilefant.transfer;

import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class IterationTO extends Iteration {

    private ScheduleStatus scheduleStatus;
    
    private List<StoryTO> rankedStories;
    
    private Set<User> assignees;
    
    public IterationTO(Iteration iter) {
        BeanCopier.copy(iter, this);
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }

    public List<StoryTO> getRankedStories() {
        return rankedStories;
    }

    public void setRankedStories(List<StoryTO> rankedStories) {
        this.rankedStories = rankedStories;
    }

    public Set<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<User> assignees) {
        this.assignees = assignees;
    }    
}
