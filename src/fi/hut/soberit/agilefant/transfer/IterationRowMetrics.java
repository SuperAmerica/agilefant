package fi.hut.soberit.agilefant.transfer;

import java.util.EnumMap;
import java.util.Map;

import fi.hut.soberit.agilefant.model.StoryState;

public class IterationRowMetrics {
    
    private Map<StoryState, Integer> stateDistribution = new EnumMap<StoryState, Integer>(StoryState.class);
    
    public Map<StoryState, Integer> getStateDistribution() {
        return stateDistribution;
    }
    
    public void setStateData(Map<StoryState, Integer> data) {
        this.stateDistribution = data;
    }
    
    public int getStateData(StoryState state) {
        return this.stateDistribution.get(state);
    }
}
