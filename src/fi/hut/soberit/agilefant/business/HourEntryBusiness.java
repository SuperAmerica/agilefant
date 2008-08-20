package fi.hut.soberit.agilefant.business;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.model.User;

/**
 * Business interface for handling functionality related to Hour Entries
 * @author kjniiran
 *
 */
public interface HourEntryBusiness {

    /**
     * Creates one entry for each of the selected users
     * @param hourEntry the hour entry that we well "copy"
     * @param userIds the IDs of the users that we are adding entries for
     */
    public void addHourEntryForMultipleUsers(TimesheetLoggable parent,HourEntry hourEntry, Set<Integer> userIds);
   
    /**
     * Format date.
     * 
     * @todo Move to static system wide util.
     * @param date String representing a date and time in format yyyy-mm-dd HH:MM
     * @return Date object set to the given moment.
     * @throws ParseException
     */
    public Date formatDate(String date) throws ParseException;
    
    /**
     * Store new entry to a given parent or alter an existing entry. If give
     * hour entry is persisted to the database this method will update the
     * information and set parent to the given parent. If hour entry is not
     * persisted a new entry will be created.
     * 
     * @param parent    Owner of the hour entry.
     * @param hourEntry  Entry information.
     * @return Persisted object.
     * @throws IllegalArgumentException
     */
    public HourEntry store(TimesheetLoggable parent, HourEntry hourEntry)
            throws IllegalArgumentException;
    
    /**
     * Add hour entry for current user
     * 
     * @param parent Owner for the entry.
     * @param effort Effort to be stored in the entry.
     */
    public void addEntryForCurrentUser(TimesheetLoggable parent, AFTime effort);
    
    /**
     * Get HourEntry or any of the hour entry sub-types by given unique id. 
     * 
     * @param id
     * @return Requested HourEntry or null if there's no entry with given id.
     */
    public HourEntry getHourEntryById(int id);
    
    /**
     * Remove entry by id.
     * 
     * @param id
     */
    public void remove(int id);
    
    /**
     * Get all hour entries attached to a given backlog item.
     *
     * @param parent
     * @return A list of BacklogItemHourEntries or an empty list if 
     *         no items were found. 
     */
    public List<BacklogItemHourEntry> getEntriesByParent(BacklogItem parent);
    
    /**
     * Get all hour entries attached to a given backlog.
     * 
     * @param parent
     * @return A list of BacklogHourEntries or an empty list if 
     *         no items were found. 
     */
    public List<BacklogHourEntry> getEntriesByParent(Backlog parent);
    
    /**
     * Loads hour entry sums to backlog's BLIs.
     * 
     * @param parent Parent backlog for BLIs
     * @return Total reported hour by backlog item indexed by backlog item id.
     */
    public Map<Integer,AFTime> getSumsByBacklog(Backlog parent);

    /**
     * Load spent effort sums to backlog items under a specific backlog. 
     * 
     * @param parent
     */
    public void loadSumsToBacklogItems(Backlog parent);
    
    /**
     * Get spent effort sums grouped by backlog's iteration goals.
     * 
     * @param parent Iteration owning the iteration goals.
     * @return MAp indexed by iteration goal ids
     */
    public Map<Integer, AFTime> getSumsByIterationGoal(Backlog parent);
    
    /**
     * Remove all hour entries under given parent.
     * 
     * @param parent
     */
    public void removeHourEntriesByParent(TimesheetLoggable parent);

    /**
     * Get total spent effort by given filters.
     * 
     * @param user
     * @param start
     * @param end
     * @return
     */
    public AFTime getEffortSumByUserAndTimeInterval(User user, Date start, Date end);
    
    /**
     * Get total spent effort by given filters.
     * 
     * @see getEffortSumByUserAndTimeInterval(User user, Date start, Date end)
     * @return Total spent effort
     */
    public AFTime getEffortSumByUserAndTimeInterval(User user, String startDate, String endDate);
    
    /**
     * Determinate whether given user is associated with any of the hours reports in the system.
     * 
     * @param user
     * @return true if association exists, false otherwise.
     */
    public boolean isAssociatedWithHourReport(User user);
    
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
   
}
