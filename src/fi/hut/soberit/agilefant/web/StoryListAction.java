package fi.hut.soberit.agilefant.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.StoryFilters;

@Component("storyListAction")
@Scope("prototype")
public class StoryListAction {
    
    private StoryFilters storyFilters = new StoryFilters(null, null, null);
    private List<StoryTO> stories;
    private int objectId;
    @Autowired
    private ProjectBusiness projectBusiness;
    
    
    public String projectLeafStories() {
        stories = projectBusiness.retrieveLeafStories(objectId, storyFilters);
        return Action.SUCCESS;
    }


    public StoryFilters getStoryFilters() {
        return storyFilters;
    }


    public void setStoryFilters(StoryFilters storyFilters) {
        this.storyFilters = storyFilters;
    }


    public List<StoryTO> getStories() {
        return stories;
    }


    public void setStories(List<StoryTO> stories) {
        this.stories = stories;
    }


    public int getObjectId() {
        return objectId;
    }


    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

}
