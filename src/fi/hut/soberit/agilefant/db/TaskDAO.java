package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Interval;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.UnassignedLoadTO;

public interface TaskDAO extends GenericDAO<Task> {
    
    /**
     * Lookup tasks that are assigned to the given user and are attached to an
     * iteration via a story. Will lookup only tasks from iterations that
     * overlap with the given interval.
     * 
     * @param user
     *            Assignee or one of the assignees for the searched tasks.
     * @param interval
     *            Search interval for the container iteration.
     */
    public List<Task> getStoryTasksWithEffortLeft(User user, Interval interval);
    
    /**
     * Lookup tasks that are assigned to the given user and are directly
     * attached to an iteration. Will lookup only tasks from iterations that
     * overlap with the given interval.
     * 
     * @param user
     *            Assignee or one of the assignees for the searched tasks.
     * @param interval
     *            Search interval for the container iteration.
     */
    public List<Task> getIterationTasksWithEffortLeft(User user,
            Interval interval);

    
    public List<Task> getStoryAssignedTasksWithEffortLeft(User user, Interval interval);
    /**
     * Count total number of assignees per task.
     * 
     * @param taskIds 
     * @return
     */
    public Map<Integer, Integer> getNumOfResponsiblesByTask(Set<Integer> taskIds);

    /**
     * 
     * @param user
     * @param interval
     * @return
     */
    public List<UnassignedLoadTO> getUnassignedStoryTasksWithEffortLeft(User user,
            Interval interval);
    
    /**
     * 
     * @param user
     * @param interval
     * @return
     */
    public List<UnassignedLoadTO> getUnassignedIterationTasksWithEffortLeft(User user,
            Interval interval);

    /**
     * Get the story's child tasks with rank between and including
     * lower and upper borders.
     * @param story
     * @param lower lower border of the rank (0 if topmost task included)
     * @param upper upper border of the rank
     * @return
     */
    public Collection<Task> getTasksWithRankBetween(Story story, int lower, int upper);
    
    /**
     * Get the iteration's straight child tasks with rank between and including
     * lower and upper borders.
     * <p>
     * Will not get the iteration's stories' tasks.
     * 
     * @param iter 
     * @param lower lower border of the rank (0 if topmost included)
     * @param upper upper border of the rank
     * @return
     */
    public Collection<Task> getTasksWithRankBetween(Iteration iter, int lower, int upper);
}
