package fi.hut.soberit.agilefant.business;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;

/**
 * The TimesheetBusiness class contains the logic and methods to generate a spent effort tree.
 * 
 * 
 * @author Vesa Pirila
 */
public interface TimesheetBusiness {

    /** Generate a forest (collection of trees) that represents the hour report timesheet.
     * Supports limiting hour entries by backlog, time interval and user.
     * Note that the sums only include the backlogs that are <b>shown</b>. Even if there are
     * more hour entries in other child backlogs, they are not included.  
     * 
     * @param backlogIds List of the id's of the backlogs that should be included in the tree
     * @param startDateString The start date of the asked interval for the hour entries
     * @param endDateString The end date of the asked interval for the hour entries
     * @return The root nodes (Products) of the forest.
     * @throws IllegalArgumentException If date parsing fails 
     */
    public List<BacklogTimesheetNode> generateTree(int[] backlogIds,
                                                   String startDateString, String endDateString,
                                                   Set<Integer> userIds)
        throws IllegalArgumentException;


    /** Get the HourEntryBusiness instance. It is used to fetch the hour entries for
     * given backlog items and backlogs. 
     * 
     * @return The Spring hourEntryBusiness instance
     */
    public HourEntryBusiness getHourEntryBusiness();
    
    /** The map contains all nodes that are already in the tree, mapped by backlog id.
     *  It is used to avoid creating duplicate instances of the same node, and to group
     *  backlogs properly.
     */
    public Map<Integer, BacklogTimesheetNode> getNodes();
    
    /**
     * Get the hour entries for this backlog item that match the filters entered in the Timesheet query.
     * The filters are stored in the TimesheetBusinessImpl instance. 
     * @param backlogItem The backlog item
     * @return A filtered list of hour entries for the given backlog item 
     */
    public List<? extends HourEntry> getFilteredHourEntries(BacklogItem backlogItem);
    
    /**
     * Get the hour entries for this backlog that match the filters entered in the Timesheet query.
     * The filters are stored in the TimesheetBusinessImpl instance. 
     * @param backlog The backlog
     * @return A filtered list of hour entries for the given backlog 
     */
    public List<? extends HourEntry> getFilteredHourEntries(Backlog backlog);
    
    /**
     * Calculate total spent time from root nodes.
     * 
     * @param roots Top-level nodes.
     * @return Total spent time
     */
    public AFTime calculateRootSum(List<BacklogTimesheetNode> roots);
}
