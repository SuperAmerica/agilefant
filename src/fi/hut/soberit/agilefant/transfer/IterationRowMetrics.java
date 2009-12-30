package fi.hut.soberit.agilefant.transfer;

import java.util.EnumMap;
import java.util.Map;

import fi.hut.soberit.agilefant.model.StoryState;

public class IterationRowMetrics {
    
    private Map<StoryState, Integer> stateDistribution = new EnumMap<StoryState, Integer>(StoryState.class);
    
    private int daysLeft = 0;
    
    private int storyCount = 0;
    
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
    
    public int getStoryCount() {
        return storyCount;
    }
    
    public int getTotalDays() {
        return totalDays;
    }
    
    public void setTotalDays(int totalDays) {
        this.totalDays = totalDays;
    }
    
}
