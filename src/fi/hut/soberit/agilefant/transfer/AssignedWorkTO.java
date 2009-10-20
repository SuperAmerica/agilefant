package fi.hut.soberit.agilefant.transfer;

import java.util.Collections;
import java.util.List;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import flexjson.JSON;

public class AssignedWorkTO {
    private List<Story> stories           = Collections.emptyList();
    private List<Task>  tasksWithoutStory = Collections.emptyList();

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    @JSON(include=true)
    public List<Story> getStories() {
        return stories;
    }

    public void setTasksWithoutStory(List<Task> tasksWithoutStory) {
        this.tasksWithoutStory = tasksWithoutStory;
    }

    @JSON(include=true)
    public List<Task> getTasksWithoutStory() {
        return tasksWithoutStory;
    }
}
