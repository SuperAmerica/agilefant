package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class IterationTO extends Iteration {

    private ScheduleStatus scheduleStatus;
    
    public IterationTO(Iteration iter) {
        BeanCopier.copy(iter, this);
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }
}
