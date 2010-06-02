package fi.hut.soberit.agilefant.transfer;

public class StoryTreeBranchMetrics {
    public long doneLeafPoints = 0;
    public long leafPoints = 0;
    public long estimatedDonePoints = 0;
    public long estimatedPoints = 0;
    
    
    public long getDoneLeafPoints() {
        return doneLeafPoints;
    }
    public long getLeafPoints() {
        return leafPoints;
    }
    public long getEstimatedDonePoints() {
        return estimatedDonePoints;
    }
    public long getEstimatedPoints() {
        return estimatedPoints;
    }
}
