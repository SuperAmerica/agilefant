package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.DailySpentEffort;

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

    /**
     * Gets all task, story and backlog hour entries for an iteration
     * and calculates their sum.
     */
    long calculateSumOfIterationsHourEntries(Iteration iteration);
    
    List<BacklogHourEntry> retrieveByParent(Backlog item);

    long calculateSumByUserAndTimeInterval(User user,
            DateTime startDate, DateTime endDate);
    
    long calculateSumByUserAndTimeInterval(int userId,
            DateTime startDate, DateTime endDate);
    
    long calculateSum(Collection<? extends HourEntry> hourEntries);
    
    public List<HourEntry> getEntriesByUserAndTimeInterval(int userId, DateTime startDate,
            DateTime endDate);
    
    public List<HourEntry> getEntriesByUserAndDay(LocalDate day, int userId);
    
    public List<DailySpentEffort> getDailySpentEffortByWeek(LocalDate week, int userId);
    
    public List<DailySpentEffort> getDailySpentEffortByInterval(DateTime start, DateTime end, int userId);
    
    public long calculateWeekSum(LocalDate week, int userId);

}
