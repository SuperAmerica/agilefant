package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Collection;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import fi.hut.soberit.agilefant.util.StoryMetrics;

public class StoryTO extends Story {

    // Not included in story
    private Collection<ResponsibleContainer> userData = new ArrayList<ResponsibleContainer>();

    private StoryMetrics metrics;

    public StoryTO(Story story) {
        this.setId(story.getId());
        this.setName(story.getName());
        this.setDescription(story.getDescription());
        this.setBacklog(story.getBacklog());
        this.setState(story.getState());
        this.setPriority(story.getPriority());
        this.setResponsibles(story.getResponsibles());
        this.setStoryPoints(story.getStoryPoints());
    }

    public void setUserData(Collection<ResponsibleContainer> userData) {
        this.userData = userData;
    }

    public Collection<ResponsibleContainer> getUserData() {
        return userData;
    }

    public void setMetrics(StoryMetrics metrics) {
        this.metrics = metrics;
    }

    public StoryMetrics getMetrics() {
        return metrics;
    }

}
