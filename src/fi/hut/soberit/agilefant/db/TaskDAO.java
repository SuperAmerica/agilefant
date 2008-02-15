package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.State;

/**
 * Interface for a DAO of a Task.
 * 
 * @see GenericDAO
 */
public interface TaskDAO extends GenericDAO<Task> {

    /**
     * Get all tasks of the given backlog item, which have one of the given
     * states.
     * 
     * @param bli
     *                backlog item, tasks of which to find
     * @param states
     *                array of accepted states
     * @return all tasks matching the criteria
     */
    public Collection<Task> getTasksByStateAndBacklogItem(BacklogItem bli,
            State[] states);

    /**
     * Get all tasks, which have one of the given states.
     * 
     * @param states
     *                array of accepted states
     * @return all tasks matching the criteria
     */
    public Collection<Task> getTasksByState(State[] states);

    /**
     * Finds the next upper ranked (x.rank < task.rank) task starting from the
     * task given as parameter.
     * 
     * @param task
     * @return next upper ranked task, null if task given as parameter is
     *         highest ranked
     */
    public Task findUpperRankedTask(Task task);

    /**
     * Finds the next lower ranked (x.rank > task.rank) task starting from the
     * task given as parameter.
     * 
     * @param task
     * @return next lower ranked task, null if task given as parameter is lowest
     *         ranked
     */
    public Task findLowerRankedTask(Task task);

    /**
     * Finds the lowest ranked task in given backlog item.
     * 
     * @param backlogItem
     * @return lowest ranked task in backlog item, null if backlog item does not
     *         have any tasks
     */
    public Task getLowestRankedTask(BacklogItem backlogItem);

    /**
     * Raises tasks' rank for all tasks that have rank in range <i>lowLimitRank <=
     * task.rank < upperLimitRank</i> and belong to given backlog item. Does
     * nothing if there are no tasks that have their rank in the range.
     * 
     * @param lowLimitRank
     * @param upperLimitRank
     * @param backlogItem
     */
    public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank,
            BacklogItem backlogItem);

}
