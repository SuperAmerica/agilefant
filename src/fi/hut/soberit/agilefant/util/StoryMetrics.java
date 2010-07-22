package fi.hut.soberit.agilefant.util;

public class StoryMetrics {

    private long effortLeft;
    private long effortSpent;
    private long originalEstimate;

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

}
