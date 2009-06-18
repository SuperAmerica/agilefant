package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import flexjson.JSON;

public class ProjectDataContainer {

    private List<Story> stories = new ArrayList<Story>();
    
    @JSON(include=true)
    public List<Story> getStories() {
        return stories;
    }
    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

}
