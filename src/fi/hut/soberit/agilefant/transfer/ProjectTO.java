package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BeanCopier;
import flexjson.JSON;

@XmlRootElement(name = "projectto")
@XmlAccessorType(XmlAccessType.NONE)
public class ProjectTO extends Project implements LeafStoryContainer, Scheduled {

    private ScheduleStatus scheduleStatus;
    
    private List<StoryTO> leafStories = new ArrayList<StoryTO>(); 
    
    private List<IterationTO> iterations = new ArrayList<IterationTO>();
    
    private Set<User> assignees;

    public ProjectTO() {};
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

    @XmlElement(name = "user")
    @XmlElementWrapper(name = "assignees")
    public Set<User> getAssignees() {
        return assignees;
    }

    public void setAssignees(Set<User> assignees) {
        this.assignees = assignees;
    }

    @XmlElement(name = "iteration")
    @XmlElementWrapper(name = "iterations")
    public List<IterationTO> getChildIterations() {
        return iterations;
    }

    public void setChildIterations(List<IterationTO> iterations) {
        this.iterations = iterations;
    }
}
