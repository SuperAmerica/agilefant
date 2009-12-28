package fi.hut.soberit.agilefant.transfer;

import java.util.Collections;
import java.util.List;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

public class MyAssignmentsTO {

    private List<Project> projects = Collections.emptyList();
    private List<Story> stories = Collections.emptyList();
    private List<Task> tasks = Collections.emptyList();
    private List<Task> tasksWithoutStory = Collections.emptyList();

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public List<Story> getStories() {
        return stories;
    }

    public void setStories(List<Story> stories) {
        this.stories = stories;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public List<Task> getTasksWithoutStory() {
        return tasksWithoutStory;
    }

    public void setTasksWithoutStory(List<Task> tasksWithoutStory) {
        this.tasksWithoutStory = tasksWithoutStory;
    }

}
