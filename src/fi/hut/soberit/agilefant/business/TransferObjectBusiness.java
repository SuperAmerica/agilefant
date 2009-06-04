package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.StoryTO;
import fi.hut.soberit.agilefant.util.TaskTO;

public interface TransferObjectBusiness {

    /**
     * Constructs transfer object based contents of an iteration. 
     * @param iteration
     * @param assignedUsers TODO
     * @return
     */
    public Collection<StoryTO> constructIterationDataWithUserData(Iteration iteration, Collection<User> assignedUsers);
    
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
     * Will fetch project assignee information.
     */
    public TaskTO constructTaskTO(Task task);
}
