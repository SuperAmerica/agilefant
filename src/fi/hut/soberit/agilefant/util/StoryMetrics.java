package fi.hut.soberit.agilefant.util;

public class StoryMetrics {

    private long effortLeft;
    private long effortSpent;
    private long originalEstimate;
    private int doneTasks;
    private int totalTasks;

    public int getDoneTasks() {
        return doneTasks;
    }

    public long getEffortLeft() {
        return effortLeft;
    }

    public void setEffortLeft(long effortLeft) {
        this.effortLeft = effortLeft;
    }

    public long getEffortSpent() {
        return effortSpent;
    }

    public void setEffortSpent(long effortSpent) {
        this.effortSpent = effortSpent;
    }

    public long getOriginalEstimate() {
        return originalEstimate;
    }

    public void setOriginalEstimate(long originalEstimate) {
        this.originalEstimate = originalEstimate;
    }

    public void setDoneTasks(int doneTasks) {
        this.doneTasks = doneTasks;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

}
