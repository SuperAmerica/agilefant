package fi.hut.soberit.agilefant.model;

import org.joda.time.DateTime;

public interface Schedulable {
    public DateTime getStartDate();
    public DateTime getEndDate();
}
