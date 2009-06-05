package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;

/**
 * Interface for a DAO of an Iteration.
 * 
 * @see GenericDAO
 */
public interface IterationDAO extends GenericDAO<Iteration> {

    public Collection<Task> getTasksWithoutStoryForIteration(Iteration iteration);
    
    public List<Task> getAllTasksForIteration(Iteration iteration);
}
