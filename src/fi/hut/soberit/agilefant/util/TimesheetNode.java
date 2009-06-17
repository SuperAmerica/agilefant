package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.List;

import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.HourEntry;

/**
 * TimesheetNode is the superclass for nodes in the tree that represents a time sheet.
 * In addition to holding child nodes, it calculates the cumulative spent effort
 * in each branch.
 *   
 * @author Pasi Pekkanen, Vesa Pirilä
 *
 */
public abstract class TimesheetNode {
    List<HourEntry> hourEntries = new ArrayList<HourEntry>();
    ExactEstimate effortSum = new ExactEstimate(0);
    
    public TimesheetNode() {
        
    }
    public abstract boolean hasChildren();
    public abstract List<? extends TimesheetNode> getChildren();
    public abstract String getName();
    public abstract int getId();
    
    public void addHourEntry(HourEntry entry) {
        this.hourEntries.add(entry);
    }
    
    public List<HourEntry> getHourEntries() {
        return this.hourEntries;
    }
}