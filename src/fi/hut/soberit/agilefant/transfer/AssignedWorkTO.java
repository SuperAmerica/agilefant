package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.List;

import fi.hut.soberit.agilefant.model.Task;
import flexjson.JSON;

public class AssignedWorkTO {
    private List<StoryTO> stories           = new ArrayList<StoryTO>();
    private List<Task>  tasksWithoutStory = new ArrayList<Task>();

    public void setStories(List<StoryTO> stories) {
        this.stories = stories;
    }

    @JSON(include=true)
    public List<StoryTO> getStories() {
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
