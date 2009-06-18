package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;

public interface TransferObjectBusiness {

    /**
     * Constructs transfer object based contents of a backlog. 
     * @param assignedUsers project assignees
     */
    public Collection<StoryTO> constructBacklogDataWithUserData(Backlog backlog, Collection<User> assignedUsers);
    
    
    /**
     * Constructs a new transfer object based on given task.
     * <p>
     * Will inject <code>UserData</code>
     * @param assignedUsers collection of users assigned to project
     */
    public TaskTO constructTaskTO(Task task, Collection<User> assignedUsers);
    
    /**
     * Constructs a new transfer object based on given task.
     * <p>
     * Will inject <code>UserData</code>.
     */
    public TaskTO constructTaskTO(Task task);
    
    /**
     * Constructs a new transfer object based on given story.
     * <p>
     * Will inject <code>UserData</code>.
     */
    public StoryTO constructStoryTO(Story story, Collection<User> assignedUsers);
}
