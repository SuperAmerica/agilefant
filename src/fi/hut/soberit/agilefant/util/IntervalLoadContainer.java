package fi.hut.soberit.agilefant.util;

import org.joda.time.Interval;


public class IntervalLoadContainer {
    private Interval interval;
    private long assignedLoad = 0L;
    private long totalLoad = 0L;
    private long basellineLoad = 0L;
    private long unassignedLoad = 0L;
    private long workHours = 0L;

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
    public Interval getInterval() {
        return interval;
    }
    public void setInterval(Interval interval) {
        this.interval = interval;
    }
    public long getBasellineLoad() {
        return basellineLoad;
    }
    public void setBasellineLoad(long basellineLoad) {
        this.basellineLoad = basellineLoad;
    }
    public long getUnassignedLoad() {
        return unassignedLoad;
    }
    public void setUnassignedLoad(long unassignedLoad) {
        this.unassignedLoad = unassignedLoad;
    }
}
