package fi.hut.soberit.agilefant.util;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;

public class TimesheetExportRowData {
    private HourEntry entry;
    private Story story = null;
    private Task task = null;
    private Iteration iteration = null;
    private Project project = null;
    private Product product = null;
    
    public TimesheetExportRowData(TaskHourEntry entry) {
        this.entry = entry;
        this.task = entry.getTask();
        if(this.task.getStory() != null) {
            this.story = this.task.getStory();
            this.iteration = this.story.getIteration();
            this.setBacklogs(this.story.getBacklog());
        } else {
            this.setBacklogs(this.task.getIteration());
        }
    }
    public TimesheetExportRowData(BacklogHourEntry entry) {
        this.entry = entry;
        this.setBacklogs(entry.getBacklog());
    }
    public TimesheetExportRowData(StoryHourEntry entry) {
        this.entry = entry;
        this.story = entry.getStory();
        this.iteration = this.story.getIteration();
        this.setBacklogs(this.story.getBacklog());
    }
    private void setBacklogs(Backlog parent) {
        if(parent instanceof Iteration) {
            this.iteration = (Iteration)parent;
            if (parent.isStandAlone())
                return;
            this.project = (Project)parent.getParent();
            this.product = (Product)parent.getParent().getParent();
        } else if(parent instanceof Project) {
            this.project = (Project)parent;
            this.product = (Product)parent.getParent();
        } else {
            this.product = (Product)parent;
        }
    }
    public User getUser() {
        return this.entry.getUser();
    }
    public String getDescription() {
        return this.entry.getDescription();
    }
    public DateTime getDate() {
        return this.entry.getDate();
    }
    public long getEffort() {
        return this.entry.getMinutesSpent();
    }
    public Task getTask() {
        return this.task;
    }
    public Story getStory() {
        return this.story;
    }
    public Iteration getIteration() {
        return this.iteration;
    }
    public Project getProject() {
        return this.project;
    }
    public Product getProduct() {
        return this.product;
    }
}
