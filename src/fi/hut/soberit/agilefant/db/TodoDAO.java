package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.Todo;
import fi.hut.soberit.agilefant.model.State;

/**
 * Interface for a DAO of a todo.
 * 
 * @see GenericDAO
 */
public interface TodoDAO extends GenericDAO<Todo> {

    /**
     * Get all todos of the given task, which have one of the given
     * states.
     * 
     * @param task
     *                task, todos of which to find
     * @param states
     *                array of accepted states
     * @return all todos matching the criteria
     */
    public Collection<Todo> getTodosByStateAndBacklogItem(Task task,
            State[] states);

    /**
     * Get all todos, which have one of the given states.
     * 
     * @param states
     *                array of accepted states
     * @return all todos matching the criteria
     */
    public Collection<Todo> getTodosByState(State[] states);

    /**
     * Finds the next upper ranked (x.rank < todo.rank) todo starting from the
     * todo given as parameter.
     * 
     * @param todo
     * @return next upper ranked todo, null if todo given as parameter is
     *         highest ranked
     */
    public Todo findUpperRankedTodo(Todo todo);

    /**
     * Finds the next lower ranked (x.rank > todo.rank) todo starting from the
     * todo given as parameter.
     * 
     * @param todo
     * @return next lower ranked todo, null if todo given as parameter is lowest
     *         ranked
     */
    public Todo findLowerRankedTodo(Todo todo);

    /**
     * Finds the lowest ranked todo in given backlog item.
     * 
     * @param backlogItem
     * @return lowest ranked todo in backlog item, null if task does not
     *         have any todos
     */
    public Todo getLowestRankedTodo(Task task);

    /**
     * Raises todos' rank for all todos that have rank in range <i>lowLimitRank <=
     * todo.rank < upperLimitRank</i> and belong to given task. Does
     * nothing if there are no todos that have their rank in the range.
     * 
     * @param lowLimitRank
     * @param upperLimitRank
     * @param task
     */
    public void raiseRankBetween(Integer lowLimitRank, Integer upperLimitRank,
            Task task);

}
