package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;
import fi.hut.soberit.agilefant.transfer.ScheduleStatus;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;

public interface TransferObjectBusiness {

    /**
     * Constructs transfer object based contents of a backlog. 
     */
    public Collection<StoryTO> constructBacklogData(Backlog backlog);
    
    /**
     * Constructs a new transfer object based on given task.
     */
    public TaskTO constructTaskTO(Task task);
    
    /**
     * Constructs a new transfer object based on given task.
     */
    public StoryTO constructStoryTO(Story story);
    
    /**
     * Get all users in AutoCompleteData containers.
     */
    public List<AutocompleteDataNode> constructUserAutocompleteData();
    
    /**
     * Get all teams in AutoCompleteData containers.
     */
    public List<AutocompleteDataNode> constructTeamAutocompleteData();
    
    /**
     * Get all backlogs in AutoCompleteData containers.
     */
    public List<AutocompleteDataNode> constructBacklogAutocompleteData();
    
    /**
     * Get all products in <code>AutoCompleteData</code> containers.
     */
    public List<AutocompleteDataNode> constructProductAutocompleteData();
    
    /**
     * Get all projects in <code>AutoCompleteData</code> containers.
     */
    public List<AutocompleteDataNode> constructProjectAutocompleteData();
    
    /**
     * Get all backlogs in AutoCompleteData containers.
     */
    public List<AutocompleteDataNode> constructCurrentIterationAutocompleteData();

    /**    
     * Checks whether the backlog is ongoing, past or future.
     */
    public ScheduleStatus getBacklogScheduleStatus(Backlog backlog);
    
    /**
     * Constructs a new daily work task transfer object
     */
    public DailyWorkTaskTO constructUnqueuedDailyWorkTaskTO(Task task);

    /**
     * Constructs a new daily work task transfer object for queue entry
     */
    public DailyWorkTaskTO constructQueuedDailyWorkTaskTO(WhatsNextEntry task);
}
