package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Schedulable;

public interface Scheduled extends Schedulable {
    public ScheduleStatus getScheduleStatus();
}
