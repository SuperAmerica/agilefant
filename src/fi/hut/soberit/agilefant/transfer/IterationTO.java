package fi.hut.soberit.agilefant.transfer;

import org.joda.time.Interval;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.util.BeanCopier;

public class IterationTO extends Iteration {

    private ScheduleStatus scheduleStatus;
    
    public IterationTO(Iteration iter) {
        BeanCopier.copy(iter, this);
        updateScheduleStatus();
    }
    
    private void updateScheduleStatus() {
        Interval interval = new Interval(this.getStartDate(), this.getEndDate());
        if (interval.isBeforeNow()) {
            this.setScheduleStatus(ScheduleStatus.PAST);
        }
        else if (interval.isAfterNow()) {
            this.setScheduleStatus(ScheduleStatus.FUTURE);
        }
        else {
            this.setScheduleStatus(ScheduleStatus.ONGOING);
        }
    }

    public void setScheduleStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public ScheduleStatus getScheduleStatus() {
        return scheduleStatus;
    }
}
