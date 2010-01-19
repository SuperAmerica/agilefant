package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.Pair;

/**
 * Interface for a DAO of an Iteration.
 * 
 * @see GenericDAO
 */
public interface IterationDAO extends GenericDAO<Iteration> {

    public Collection<Task> getTasksWithoutStoryForIteration(Iteration iteration);

    public List<Task> getAllTasksForIteration(Iteration iteration);

    public Map<StoryState, Integer> countIterationStoriesByState(int iterationId);

    public Pair<Integer, Integer> getCountOfDoneAndAllTasks(Iteration iteration);

    public Pair<Integer, Integer> getCountOfDoneAndAllStories(
            Iteration iteration);

    public List<Iteration> retrieveIterationsByIds(Set<Integer> iterationIds);

    public Map<Integer, Integer> getTotalAvailability(Set<Integer> iterationIds);

    public List<Iteration> retrieveEmptyIterationsWithPlannedSize(
            DateTime startDate, DateTime endDate, User assignee);

    public List<Iteration> retrieveCurrentAndFutureIterationsAt(
            DateTime dayStart);

    /**
     * Retrieves an iteration by id and fetches eagerly the following
     * associations:
     * 
     * <ul>
     * <li>tasks</li>
     * <li>tasks.responsibles</li>
     * <li>tasks.whatsNextEntries</li>
     * <li>stories</li>
     * <li>stories.labels</li>
     * <li>stories.tasks</li>
     * <li>stories.tasks.responsibles</li>
     * <li>stories.tasks.whatsNextEntries</li>
     * </ul>
     * 
     * @param iterationId
     * @return retrieved iteration object
     */
    public Iteration retrieveDeep(int iterationId);

    List<Iteration> retrieveActiveWithUserAssigned(int userId);
}
