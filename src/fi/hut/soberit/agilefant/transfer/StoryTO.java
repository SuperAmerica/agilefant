package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.BeanCopier;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import flexjson.JSON;

public class StoryTO extends Story {

    // Additional fields
    private StoryMetrics metrics;
    // Context-specific rank
    private Integer rank;

    public StoryTO(Story story) {
        BeanCopier.copy(story, this);
    }

    public void setMetrics(StoryMetrics metrics) {
        this.metrics = metrics;
    }

    public StoryMetrics getMetrics() {
        return metrics;
    }

    @JSON
    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

}
