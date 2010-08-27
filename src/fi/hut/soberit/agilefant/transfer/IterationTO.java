package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BeanCopier;

@XmlRootElement(name="iteration")
@XmlAccessorType( XmlAccessType.NONE )
public class IterationTO extends Iteration implements LeafStoryContainer, Scheduled {

    private ScheduleStatus scheduleStatus;
    
    private List<StoryTO> rankedStories = new ArrayList<StoryTO>();
    
    private Set<User> assignees;
    
    public IterationTO() {}
    
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

    @XmlElementWrapper(name = "assignees")
    @XmlElement(name = "user")
    public Set<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<User> assignees) {
        this.assignees = assignees;
    }

    @XmlElementWrapper(name="leafStories")
    @XmlElement(name="story")
    public List<StoryTO> getLeafStories() {
        return this.rankedStories;
    }

    public void setLeafStories(List<StoryTO> leafStories) {
        this.rankedStories = leafStories;
        
    }    
}
