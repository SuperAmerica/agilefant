package fi.hut.soberit.agilefant.db;

import java.util.Date;
import java.util.List;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.User;

public interface HourEntryDAO extends GenericDAO<HourEntry> {
    
    /**
     * Returns the total effort spent sum between start and end date for the specified user.
     */
    public AFTime getEffortSumByUserAndTimeInterval(User user, Date start, Date end);
    
    /**
     * Get all hour entries associated with give user.
     * 
     * @param user
     * @return List of hour entries associated with given user.
     */
    public List<HourEntry> getHourEntriesByUser(User user);
}
