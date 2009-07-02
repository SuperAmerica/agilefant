package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Interval;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public interface TaskDAO extends GenericDAO<Task> {
    public List<Task> getStoryTasksByUserAndTimeframe(User user, Interval interval);
    public List<Task> getIterationTasksByUserAndTimeframe(User user, Interval interval);
    public Map<Integer, Integer> getNumOfResponsiblesByTask(Set<Integer> taskIds);
    public List<Task> getUnassignedTasksByStoryResponsibles(User user, Interval interval);
    
}
