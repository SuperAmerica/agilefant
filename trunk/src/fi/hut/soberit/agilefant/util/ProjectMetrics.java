package fi.hut.soberit.agilefant.util;

public class ProjectMetrics extends BacklogMetrics {
    
    private int assignees;
    private int numberOfOngoingIterations;
    private int numberOfAllIterations;
    
    public int getAssignees() {
        return assignees;
    }
    public void setAssignees(int assignees) {
        this.assignees = assignees;
    }
    public int getNumberOfOngoingIterations() {
        return numberOfOngoingIterations;
    }
    public void setNumberOfOngoingIterations(int numberOfOngoingIterations) {
        this.numberOfOngoingIterations = numberOfOngoingIterations;
    }
    public int getNumberOfAllIterations() {
        return numberOfAllIterations;
    }
    public void setNumberOfAllIterations(int numberOfAllIterations) {
        this.numberOfAllIterations = numberOfAllIterations;
    }
            
}
