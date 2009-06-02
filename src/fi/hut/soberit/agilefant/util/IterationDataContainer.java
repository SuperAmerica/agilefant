package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import flexjson.JSON;

public class IterationDataContainer {

    private List<Story> stories = new ArrayList<Story>();
    private Collection<Task> tasksWithoutStory = new ArrayList<Task>();
    
    @JSON(include=true)
    public List<Story> getStories() {
        return stories;
    }
    public void setStories(List<Story> stories) {
        this.stories = stories;
    }
    @JSON(include=true)
    public Collection<Task> getTasksWithoutStory() {
        return tasksWithoutStory;
    }
    public void setTasksWithoutStory(Collection<Task> collection) {
        this.tasksWithoutStory = collection;
    }
}
