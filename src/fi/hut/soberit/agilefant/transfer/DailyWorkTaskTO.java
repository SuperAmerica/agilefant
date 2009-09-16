package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.BeanCopier;
import fi.hut.soberit.agilefant.web.context.ContextLinkGenerator;
import fi.hut.soberit.agilefant.web.context.ContextLinkGeneratorFactory;

public class DailyWorkTaskTO extends Task {
    public enum TaskClass {
        ASSIGNED,
        NEXT,
        NEXT_ASSIGNED
    }
    
    private TaskClass taskClass;
    private int workQueueRank;
    private String contextLink;
    private String contextDescription;
    
    public DailyWorkTaskTO(Task task) {
        BeanCopier.copy(task, this);
        
        setupContextLinks();
    }

    public DailyWorkTaskTO(Task task, TaskClass clazz, int whatsNextRank) {
        BeanCopier.copy(task, this);
        
        this.taskClass = clazz;
        this.workQueueRank = whatsNextRank;
        
        setupContextLinks();
    }

    private void setupContextLinks() {
        ContextLinkGenerator<Task> linkGenerator = ContextLinkGeneratorFactory
            .getInstance().getContextLinkGenerator(Task.class);
        
        if (linkGenerator == null) {
            return;
        }
        this.contextLink = linkGenerator.createLink();
        Story story = this.getStory();
        if (story != null) {
            this.contextDescription = "Story: " + story.getName();
        }
        else {
            Iteration iteration = this.getIteration();
            if (iteration != null) {
                this.contextDescription = "Iteration: " + iteration.getName();
            }
            else {
                this.contextDescription = "-";
            }
        }
    }

    public int getWorkQueueRank() {
        return workQueueRank;
    }

    public void setWorkQueueRank(int workQueueRank) {
        this.workQueueRank = workQueueRank;
    }

    public void setTaskClass(TaskClass clazz) {
        this.taskClass = clazz;
    }

    public String getContextLink() {
        return this.contextLink;
    }
    
    public void setContextLink(String link) {
        this.setContextLink(link);
    }
    
    public String getContextDescription() {
        return this.contextDescription;
    }
    
    public void setContextDescription(String description) {
        this.contextDescription = description;
    }
    
    public TaskClass getTaskClass() {
        return taskClass;
    }

    public void setTask(Task task) {
        BeanCopier.copy(task, this);
    }
}
