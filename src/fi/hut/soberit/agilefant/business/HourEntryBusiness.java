package fi.hut.soberit.agilefant.business;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
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
     * Store
     */
    public void store(TimesheetLoggable parent, HourEntry hourEntry);
    /**
     * Add hour entry for current user
     */
    public void addEntryForCurrentUser(TimesheetLoggable parent, AFTime effort);
    
    
    public HourEntry getId(int id);
    /**
     * 
     * @param id
     */
    public void remove(int id);
    /**
     * Get all hour entries attached to a backlog item.
     */
    public List<BacklogItemHourEntry> getEntriesByBacklogItem(BacklogItem parent);
    /**
     * 
     */
    public Map<Integer,AFTime> getSumsByBacklog(Backlog parent);
    /**
     * 
     */
    public Map<Integer, AFTime> getSumsByIterationGoal(Backlog parent);
    /**
     * 
     */
    public void removeHourEntriesByBacklogItem( BacklogItem backlog );
    
    public void removeHourEntriesByBacklog( Backlog backlog );
    
    public AFTime getEffortSumByUserAndTimeInterval(User user, Date start, Date end);
    
    public AFTime getEffortSumByUserAndTimeInterval(User user, String startDate, String endDate);
}
