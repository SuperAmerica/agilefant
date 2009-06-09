package fi.hut.soberit.agilefant.transfer;

import java.util.ArrayList;
import java.util.Collection;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import flexjson.JSON;

public class TaskTO extends Task {
   
    // Helper fields
    
    private Collection<ResponsibleContainer> userData = new ArrayList<ResponsibleContainer>();
    private Collection<HourEntryTO> hourEntries;
    
    public TaskTO(Task task, Collection<TaskHourEntry> hourEntries) {
        this.setId(task.getId());
        this.setName(task.getName());
        this.setDescription(task.getDescription());
        this.setIteration(task.getIteration());
        this.setStory(task.getStory());
        this.setState(task.getState());
        this.setPriority(task.getPriority());
        this.setTodos(task.getTodos());
        this.setEffortLeft(task.getEffortLeft());
        this.setOriginalEstimate(task.getOriginalEstimate());
        this.setResponsibles(task.getResponsibles());
        this.setCreatedDate(task.getCreatedDate());
        this.setCreator(task.getCreator());
        this.setHourEntries(hourEntries);
    }
    
    public void setUserData(Collection<ResponsibleContainer> userData) {
        this.userData = userData;
    }

    @JSON(include = true)
    public Collection<ResponsibleContainer> getUserData() {
        return userData;
    }

    public void setHourEntries(Collection<TaskHourEntry> hourEntries) {
        ArrayList<HourEntryTO> toEntries = new ArrayList<HourEntryTO>();
        for(TaskHourEntry t : hourEntries) {
            toEntries.add(new HourEntryTO(t));
        }
        this.hourEntries = toEntries;
    }

    @JSON(include = true)
    public Collection<HourEntryTO> getHourEntries() {
        return hourEntries;
    }
}

