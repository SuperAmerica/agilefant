package fi.hut.soberit.agilefant.transfer;

import java.util.EnumMap;
import java.util.Map;

import fi.hut.soberit.agilefant.model.StoryState;

public class IterationRowMetrics {
    
    private Map<StoryState, Integer> stateDistribution = new EnumMap<StoryState, Integer>(StoryState.class);
    
    private int daysLeft = 0;
    
    private int storyCount = 0;
    
    private int doneStoryCount = 0;
    
    private int doneStoryPercentage = 0;
    
    private int totalDays = 0;
    
    public Map<StoryState, Integer> getStateDistribution() {
        return stateDistribution;
    }
    
    public void setStateData(Map<StoryState, Integer> data) {
        this.storyCount = 0;
        for (Map.Entry<StoryState, Integer> entry : data.entrySet()) {
            this.storyCount += entry.getValue();
        }
        this.stateDistribution = data;
        this.doneStoryCount = this.stateDistribution.get(StoryState.DONE);
        if (this.storyCount > 0) {
            this.doneStoryPercentage = this.doneStoryCount * 100 / this.storyCount;
        }
    }
    
    public int getStateData(StoryState state) {
        return this.stateDistribution.get(state);
    }
    
    public int getDaysLeft() {
        return daysLeft;
    }
    
    public void setDaysLeft(int daysLeft) {
        this.daysLeft = daysLeft;
    }
    
    public int getDaysLeftPercentage() {
        return (totalDays == 0) ? 0 : daysLeft * 100 / totalDays;
    }
    
    public int getStoryCount() {
        return storyCount;
    }
    
    public int getDoneStoryCount() {
        return doneStoryCount;
    }
    
    public int getDoneStoryPercentage() {
        return doneStoryPercentage;
    }
    
    public int getTotalDays() {
        return totalDays;
    }
    
    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }
    
}
