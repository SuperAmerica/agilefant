package fi.hut.soberit.agilefant.web.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.StoryMetrics;

@Component("storyWidget")
@Scope("prototype")
public class StoryWidget extends CommonWidget {

    private static final long serialVersionUID = 7810437122662724707L;

    private Story story;
    private StoryMetrics storyMetrics;
    
    @Autowired
    private StoryBusiness storyBusiness;
    
    @Override
    public String execute() {
        story = storyBusiness.retrieve(getObjectId());
        storyMetrics = storyBusiness.calculateMetrics(story);
        return SUCCESS;
    }

    public Story getStory() {
        return story;
    }
    
    public StoryMetrics getStoryMetrics() {
        return storyMetrics;
    }
}
