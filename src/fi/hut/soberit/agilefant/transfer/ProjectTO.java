package fi.hut.soberit.agilefant.transfer;

import java.util.List;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.BeanCopier;
import flexjson.JSON;

public class ProjectTO extends Project {

    private ScheduleStatus scheduleStatus;
    
    private List<Story> leafStories; 
    
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
    public List<Story> getLeafStories() {
        return leafStories;
    }

    public void setLeafStories(List<Story> leafStories) {
        this.leafStories = leafStories;
    }

}
