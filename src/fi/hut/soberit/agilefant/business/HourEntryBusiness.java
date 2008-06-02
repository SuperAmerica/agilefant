package fi.hut.soberit.agilefant.business;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;

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
    public void addHourEntryForMultipleUsers(TimesheetLoggable parent,HourEntry hourEntry, int[] userIds);
    /**
     * Format date
     */
    public Date formatDate(String date) throws ParseException;
    /**
     * Store
     */
    public void store(TimesheetLoggable parent, HourEntry hourEntry);
    
    public HourEntry getId(int id);
    /**
     * 
     * @param id
     */
    public void remove(int id);
    /**
     * 
     */
    public List<BacklogItemHourEntry> getEntriesByBacklogItem(BacklogItem parent);
    /**
     * 
     */
    public Map<Integer,AFTime> getSumsByBacklog(Backlog parent);
    /**
     * 
     */
    public void removeHourEntryByBacklogID( BacklogItem backlog );
}
