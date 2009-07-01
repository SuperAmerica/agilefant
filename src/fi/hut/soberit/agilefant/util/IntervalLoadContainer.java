package fi.hut.soberit.agilefant.util;

import org.joda.time.DateTime;


public class IntervalLoadContainer {
    private DateTime start;
    private DateTime end;
    private long assignedLoad = 0L;
    private long totalLoad = 0L;
    private long workHours = 0L;
    public DateTime getStart() {
        return start;
    }
    public void setStart(DateTime start) {
        this.start = start;
    }
    public DateTime getEnd() {
        return end;
    }
    public void setEnd(DateTime end) {
        this.end = end;
    }
    public long getAssignedLoad() {
        return assignedLoad;
    }
    public void setAssignedLoad(long assignedLoad) {
        this.assignedLoad = assignedLoad;
    }
    public long getTotalLoad() {
        return totalLoad;
    }
    public void setTotalLoad(long totalLoad) {
        this.totalLoad = totalLoad;
    }
    public long getWorkHours() {
        return workHours;
    }
    public void setWorkHours(long workHours) {
        this.workHours = workHours;
    }
}
