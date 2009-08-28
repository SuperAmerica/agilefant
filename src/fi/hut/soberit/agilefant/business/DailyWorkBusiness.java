package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public interface DailyWorkBusiness {
    public void setTaskDAO(TaskDAO dao);
    
    /**
     * Retrieves the list of tasks assigned
     * to the user.
     */
    public Collection<Task> getDailyTasksForUser(User user);
}
