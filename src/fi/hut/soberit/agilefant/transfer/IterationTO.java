package fi.hut.soberit.agilefant.transfer;

import java.util.List;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class IterationTO extends Iteration {

    private ScheduleStatus scheduleStatus;
    
    private List<Story> rankedStories;
    
    public IterationTO(Iteration iter) {
        BeanCopier.copy(iter, this);
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }

    public List<Story> getRankedStories() {
        return rankedStories;
    }

    public void setRankedStories(List<Story> rankedStories) {
        this.rankedStories = rankedStories;
    }    
}
