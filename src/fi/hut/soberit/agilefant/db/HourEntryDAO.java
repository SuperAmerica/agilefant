package fi.hut.soberit.agilefant.db;

import java.util.Date;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.User;

public interface HourEntryDAO extends GenericDAO<HourEntry> {
    
    public AFTime getEffortSumByUserAndTimeInterval(User user, Date start, Date end);
}
