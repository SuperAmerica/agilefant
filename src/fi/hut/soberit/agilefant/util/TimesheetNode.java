package fi.hut.soberit.agilefant.util;

import java.util.List;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.HourEntry;

/**
 * TimesheetNode is the superclass for nodes in the tree that represents a time sheet.
 * In addition to holding child nodes, it calculates the cumulative spent effort
 * in each branch.
 *   
 * @author Vesa Pirila
 *
 */
public abstract class TimesheetNode {
    
    protected List<BacklogTimesheetNode> childBacklogs;
    protected List<BacklogItemTimesheetNode> childBacklogItems;
    protected List<? extends HourEntry> hourEntries;
    private AFTime spentHours, hoursForChildBacklogItems, hoursForChildBacklogs, hourTotal;

    /**
     * Get the effort marked directly to this node
     * @return The effort marked directly to this node
     */
    public AFTime getSpentHours(){
        if(spentHours == null){
            spentHours = new AFTime(0);
            
            if(hourEntries != null){
                for(HourEntry hourEntry : hourEntries)
                    spentHours.add(hourEntry.getTimeSpent());
            }
        }
        
        return spentHours;
    }
    
    /**
     * Get the total sum of effort spent for this node's child backlog items
     * @return The total sum of effort spent for this node's child backlog items
     */
    public AFTime getHoursForChildBacklogItems(){
        if(hoursForChildBacklogItems == null){
            hoursForChildBacklogItems = new AFTime(0);
            
            if(childBacklogItems != null){
                for(TimesheetNode child : childBacklogItems)
                    hoursForChildBacklogItems.add(child.getHourTotal());
            }
        }
        
        return hoursForChildBacklogItems;
    }
    
    /**
     * Get the total sum of effort spent for this node's child backlogs
     * @return The total sum of effort spent for this node's child backlogs
     */
    public AFTime getHoursForChildBacklogs(){
        if(hoursForChildBacklogs == null){
            hoursForChildBacklogs = new AFTime(0);
            
            if(childBacklogs != null)
                for(TimesheetNode child : childBacklogs)
                    hoursForChildBacklogs.add(child.getHourTotal());
        }
        
        return hoursForChildBacklogs;
    }
    
    /**
     * Get the total of effort spent for this node and all its children. 
     * @return The total of effort spent for this node and its children.
     */
    public AFTime getHourTotal(){
        if(hourTotal == null){
            hourTotal = new AFTime(0);

            hourTotal.add(getHoursForChildBacklogs());
            hourTotal.add(getHoursForChildBacklogItems());
            hourTotal.add(getSpentHours());
        }
        
        return hourTotal;
    }
 
    /**
     * Get this node's child backlogs
     * @return This node's child backlogs
     */
    public List<BacklogTimesheetNode> getChildBacklogs() {
        return childBacklogs;
    }
    
    /**
     * Get this node's child backlog items
     * @return This node's child backlog items
     */
    public List<BacklogItemTimesheetNode> getChildBacklogItems() {
        return childBacklogItems;
    }

    public List<? extends HourEntry> getHourEntries(){
        return this.hourEntries;
    }
    
    /**
     * A debug function printing the contents of the node to System.out.
     */
    public abstract void print();
}