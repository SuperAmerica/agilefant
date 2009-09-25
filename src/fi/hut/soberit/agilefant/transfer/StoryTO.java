package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.BeanCopier;
import fi.hut.soberit.agilefant.util.StoryMetrics;

public class StoryTO extends Story {

    // Additional fields
    private StoryMetrics metrics;

    public StoryTO(Story story) {
        BeanCopier.copy(story, this);
    }

    public void setMetrics(StoryMetrics metrics) {
        this.metrics = metrics;
    }

    public StoryMetrics getMetrics() {
        return metrics;
    }

}
