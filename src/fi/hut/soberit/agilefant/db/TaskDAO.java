package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public interface TaskDAO extends GenericDAO<Task> {
    public List<Task> getStoryTasksByUserAndTimeframe(User user, DateTime startDate, DateTime endDate);
    public List<Task> getIterationTasksByUserAndTimeframe(User user, DateTime startDate, DateTime endDate);
    public Map<Integer, Integer> getNumOfResponsiblesByTask(Set<Integer> taskIds);
    public List<Task> getUnassignedTasksByStoryResponsibles(User user, DateTime startDate, DateTime endDate);
    
}
