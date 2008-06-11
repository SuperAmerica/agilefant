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
import fi.hut.soberit.agilefant.model.Project;
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
     * Format date
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
     * 
     */
    public Map<Integer,AFTime> getSumsByBacklog(Backlog parent);

    /**
     * Loads hour entry sums to backlog's BLIs.
     */
    public void loadSumsToBacklogItems(Backlog parent);
    
    /**
     * 
     */
    public Map<Integer, AFTime> getSumsByIterationGoal(Backlog parent);
    
    /**
     * Remove all hour entries under given parent.
     * 
     * @param parent
     */
    public void removeHourEntriesByParent(TimesheetLoggable parent);

    
    public AFTime getEffortSumByUserAndTimeInterval(User user, Date start, Date end);
    
    public AFTime getEffortSumByUserAndTimeInterval(User user, String startDate, String endDate);
}
