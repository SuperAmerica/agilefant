package fi.hut.soberit.agilefant.business;

import java.util.List;
import java.util.Map;

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
     * @return The root nodes (Products) of the forest. 
     */
    public List<BacklogTimesheetNode> generateTree(int[] backlogIds);


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
}
