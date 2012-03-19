package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.ExactEstimate;

public class ProjectMetrics {

    private int storyPoints = 0;
    private int completedStoryPoints = 0;
    private int storyPointsCompletedPercentage = 0;
    private int numberOfStories = 0;
    private int numberOfDoneStories = 0;
    private int completedStoriesPercentage = 0;
    private int daysLeft = 0;
    private int totalDays = 0;
    private int daysLeftPercentage = 0;
    private ExactEstimate effortSpent = new ExactEstimate(0);
    private ExactEstimate originalEstimate = new ExactEstimate(0);
    private int effortSpentPercentage = 0;
    private int totalValue = 0;
    private int completedValue = 0;
    private int valuePercentage = 0;

    public void setStoryPoints(int storyPoints) {
        this.storyPoints = storyPoints;
    }

    public int getStoryPoints() {
        return storyPoints;
    }

    public int getCompletedStoryPoints() {
        return completedStoryPoints;
    }

    public void setCompletedStoryPoints(int completedStoryPoints) {
        this.completedStoryPoints = completedStoryPoints;
    }

    public int getStoryPointsCompletedPercentage() {
        return storyPointsCompletedPercentage;
    }

    public void setStoryPointsCompletedPercentage(int storyPointsCompletedPercentage) {
        this.storyPointsCompletedPercentage = storyPointsCompletedPercentage;
    }

    public int getNumberOfStories() {
        return numberOfStories;
    }

    public void setNumberOfStories(int numberOfStories) {
        this.numberOfStories = numberOfStories;
    }

    public int getNumberOfDoneStories() {
        return numberOfDoneStories;
    }

    public void setNumberOfDoneStories(int numberOfDoneStories) {
        this.numberOfDoneStories = numberOfDoneStories;
    }

    public int getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }

    public int getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }

    public int getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public void setDaysLeftPercentage(int daysLeftPercentage) {
        this.daysLeftPercentage = daysLeftPercentage;
    }

    public int getCompletedStoriesPercentage() {
        return completedStoriesPercentage;
    }

    public void setCompletedStoriesPercentage(int completedStoriesPercentage) {
        this.completedStoriesPercentage = completedStoriesPercentage;
    }
    
    public void setEffortSpent(ExactEstimate effortSpent) {
        this.effortSpent = effortSpent;
    }
    
    public ExactEstimate getEffortSpent() {
        return effortSpent;
    }
    
    public void setEfforSpentPercentage(int effortSpentPercentage) {
        this.effortSpentPercentage = effortSpentPercentage;
    }
    
    public int getEffortSpentPercentage() {
        return this.effortSpentPercentage;
    }

    public void setOriginalEstimate(ExactEstimate originalEstimate) {
        this.originalEstimate = originalEstimate;
    }
    
    public ExactEstimate getOriginalEstimate() {
        return originalEstimate;
    }
    
    public int getTotalValue() {
        return totalValue;
    }
    
    public void setTotalValue(int totalValue) {
        this.totalValue = totalValue;
    }
    
    public int getCompletedValue() {
        return completedValue;
    }
    
    public void setCompletedValue(int completedValue) {
        this.completedValue = completedValue;
    }
    
    public int getValuePercentage() {
        return valuePercentage;
    }
    
    public void setValuePercentage(int valuePercentage) {
        this.valuePercentage = valuePercentage;
    }
}
