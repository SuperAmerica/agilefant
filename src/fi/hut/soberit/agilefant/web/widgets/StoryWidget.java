package fi.hut.soberit.agilefant.web.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Story;

@Component("storyWidget")
@Scope("prototype")
public class StoryWidget extends CommonWidget {

    private static final long serialVersionUID = 7810437122662724707L;

    private Story story;
    
    @Autowired
    private StoryBusiness storyBusiness;
    
    @Override
    public String execute() {
        story = storyBusiness.retrieve(getObjectId());
        return SUCCESS;
    }

    public Story getStory() {
        return story;
    }
}
