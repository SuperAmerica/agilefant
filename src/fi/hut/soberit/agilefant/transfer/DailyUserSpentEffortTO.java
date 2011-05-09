package fi.hut.soberit.agilefant.transfer;

import org.joda.time.DateTime;

import flexjson.JSON;

public class DailyUserSpentEffortTO {
    private long assignedEffort;
    private long unassignedEffort;
    private long totalEffort;
    private DateTime date;
    
    @JSON
    public long getAssignedEffort() {
        return assignedEffort;
    }
    
    public void setAssignedEffort(long assignedEffort) {
        this.assignedEffort = assignedEffort;
    }
    
    @JSON
    public long getUnassignedEffort() {
        return unassignedEffort;
    }
    
    public void setUnassignedEffort(long unassignedEffort) {
        this.unassignedEffort = unassignedEffort;
    }
    
    @JSON
    public DateTime getDate() {
        return date;
    }
    
    public void setDate(DateTime date) {
        this.date = date;
    }
    
    @JSON
    public long getTotalEffort() {
        return this.totalEffort;
    }
    
    public void setTotalEffort(long effort) {
        this.totalEffort = effort;
    }
    
    
}
