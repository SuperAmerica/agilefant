package fi.hut.soberit.agilefant.business;

import java.util.Map;

import fi.hut.soberit.agilefant.util.BacklogTimesheetNode;

public interface TimesheetBusiness {

    /** Generate a tree that represents the hour report timesheet. Supports limiting hour entries
     * by backlog, time interval and user. 
     * 
     * @param backlogIds List of the id's of the backlogs that should be included in the tree
     */
    public void generateTree(int[] backlogIds);

    /** Get the HourEntryBusiness instance. It is used to fetch the hour entries for
     * given backlog items and backlogs. 
     * 
     * @return
     */
    public HourEntryBusiness getHourEntryBusiness();
    
    /** The map contains all nodes that are already in the tree, mapped by backlog id.
     *  It is used to avoid creating duplicate instances of the same node, and to group
     *  backlogs properly.
     */
    public Map<Integer, BacklogTimesheetNode> getNodes();
}
