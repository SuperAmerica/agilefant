package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;

public class IterationGoalMetrics {
    private AFTime effortLeft = new AFTime(0);
    private AFTime effortSpent = new AFTime(0);
    private AFTime originalEstimate = new AFTime(0);
    private int doneTasks = 0;
    private int totalTasks = 0;
    
    public IterationGoalMetrics() {
        
    }
    public AFTime getEffortLeft() {
        return effortLeft;
    }
    public void setEffortLeft(AFTime effortLeft) {
        this.effortLeft = effortLeft;
    }
    public AFTime getEffortSpent() {
        return effortSpent;
    }
    public void setEffortSpent(AFTime effortSpent) {
        this.effortSpent = effortSpent;
    }
    public AFTime getOriginalEstimate() {
        return originalEstimate;
    }
    public void setOriginalEstimate(AFTime originalEstimate) {
        this.originalEstimate = originalEstimate;
    }
    public void setDoneTasks(int doneTasks) {
        this.doneTasks = doneTasks;
    }
    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }
    public int getDoneTasks() {
        return doneTasks;
    }
    public void addTask(BacklogItem bli) {
        this.totalTasks++;
        if(bli.getState() == State.DONE) {
            this.doneTasks++;
        }
    }
    public int getTotalTasks() {
        return totalTasks;
    }
}
