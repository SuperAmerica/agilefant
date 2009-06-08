package fi.hut.soberit.agilefant.business;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.model.User;

/**
 * Business interface for handling functionality related to Hour Entries
 * 
 * @author kjniiran
 * 
 */
public interface HourEntryBusiness extends GenericBusiness<HourEntry> {

    /**
     * Creates one entry for each of the selected users
     * 
     * @param hourEntry
     *            the hour entry that we well "copy"
     * @param userIds
     *            the IDs of the users that we are adding entries for
     */
    public void addHourEntryForMultipleUsers(TimesheetLoggable parent,
            HourEntry hourEntry, Set<Integer> userIds);

    /**
     * Update multiple hour entries
     * 
     * @param userIds
     * @param dates
     * @param efforts
     * @param descriptions
     */
    public void updateMultiple(Map<Integer, String[]> userIds,
            Map<Integer, String[]> dates, Map<Integer, String[]> efforts,
            Map<Integer, String[]> descriptions);

    
    HourEntry store(TimesheetLoggable parent, HourEntry hourEntry);

    List<BacklogHourEntry> retrieveByParent(Backlog item);

    long calculateSumByUserAndTimeInterval(User user,
            DateTime startDate, DateTime endDate);

}
