package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.util.Pair;

/**
 * Interface for a DAO of an Iteration.
 * 
 * @see GenericDAO
 */
public interface IterationDAO extends GenericDAO<Iteration> {

    public Collection<Task> getTasksWithoutStoryForIteration(Iteration iteration);
    
    public List<Task> getAllTasksForIteration(Iteration iteration);
    
    public Pair<Integer, Integer> getCountOfDoneAndAllTasks(Iteration iteration);

    public Pair<Integer, Integer> getCountOfDoneAndAllStories(
            Iteration iteration);
    
    public List<Iteration> retrieveIterationsByIds(Set<Integer> iterationIds);
    
    public Map<Integer, Integer> getTotalAvailability(Set<Integer> iterationIds);
}
