package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;

public interface DailyWorkBusiness {
    public void setTaskDAO(TaskDAO dao);
    
    /**
     * Retrieves the list of tasks assigned
     * to the user.
     */
    public Collection<DailyWorkTaskTO> getCurrentTasksForUser(User user);
    
    /**
     * Retrieves the rank ordered list of next tasks for user
     */
    public Collection<DailyWorkTaskTO> getNextTasksForUser(User user);

    /**
     * Retrieves the list of tasks assigned
     * to the user.
     */
    public Collection<DailyWorkTaskTO> getAllCurrentTasksForUser(User user);
}
