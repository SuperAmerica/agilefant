package fi.hut.soberit.agilefant.util;

import java.util.ArrayList;
import java.util.Collection;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;

public class StoryTO extends Story {
    
    // Not included in story
    private Collection<ResponsibleContainer> userData = new ArrayList<ResponsibleContainer>();
    
    public StoryTO(Story story) {
        this.setId(story.getId());
        this.setName(story.getName());
        this.setDescription(story.getDescription());
        this.setBacklog(story.getBacklog());
        this.setState(story.getState());
        this.setPriority(story.getPriority());
        this.setCreator(story.getCreator());
        this.setResponsibles(story.getResponsibles());
        
        setTasksAsTOs(story.getTasks());
    }
    
    private void setTasksAsTOs(Collection<Task> tasks) {
        Collection<TaskTO> newTasks = new ArrayList<TaskTO>();
        for (Task task : tasks) {
            newTasks.add(new TaskTO(task));
        }
        this.setTasks(new ArrayList<Task>());
        this.getTasks().addAll(newTasks);
    }
    
    public void setUserData(Collection<ResponsibleContainer> userData) {
        this.userData = userData;
    }

    public Collection<ResponsibleContainer> getUserData() {
        return userData;
    }
}
