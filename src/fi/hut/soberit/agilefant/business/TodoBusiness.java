package fi.hut.soberit.agilefant.business;

import java.util.Map;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Todo;

/**
 * Business interface for handling functionality related to todos.
 * 
 * @author Mika Salminen
 * 
 */
public interface TodoBusiness {

    /**
     * Stores a todo.
     * @param todoId the id of the todo
     * @param backlogItemId the id of the todo's parent backlog item
     * @param name the name of the todo
     * @param state the current state of the todo 
     * @return
     */
    public Todo storeTodo(int todoId, int backlogItemId, String name, State state)
        throws ObjectNotFoundException;
    
    /**
     * Stores a todo 
     * @param storable the todo to store
     * @param bli the todo's parent backlog item
     * @param name the name of the todo
     * @param state the current state of the todo
     * @return
     */
    public Todo storeTodo(Todo storable, BacklogItem bli, String name, State state);
    
    /**
     * Removes the specified todo.
     * @param todo todo to remove
     */
    public void removeTodo(Todo todo) throws ObjectNotFoundException;
    
    /**
     * Removes the specified todo.
     * @param todo the id todo to remove
     */
    public void removeTodo(int todoId) throws ObjectNotFoundException;
    
    
    /**
     * Updates multiple todos' states with one call. Takes Map with elements of
     * form: <code>[todo_id => new_status] </code> as parameter.
     * 
     * 
     * @param newStatesMap
     *                <code>Map</code> with elements
     *                <code>[todo_id => new_status]</code> defining the new
     *                states for todos.
     * 
     */

    public void updateMultipleTodos(BacklogItem bli, Map<Integer, State> newStatesMap, Map<Integer, String> newNamesMap)
            throws ObjectNotFoundException;

    /**
     * Gives the todo the lowest rank (i.e. currentLowestRank + 1) among the
     * todos owned by the same backlog item. If the todo is lowest ranked, does
     * nothing.
     * 
     * @param todoId
     * @throws ObjectNotFoundException
     */

    public abstract void rankTodoUp(int todoId) throws ObjectNotFoundException;

    /**
     * Gives the todo the highest rank (i.e. rank value 0) among the todos owned
     * by the same backlog item. If the todo is highest ranked, does nothing.
     * 
     * @param todoId
     * @throws ObjectNotFoundException
     */
    public abstract void rankTodoDown(int todoId)
            throws ObjectNotFoundException;

    /**
     * Gives the todo the highest rank (i.e. rank value 0) among the todos owned
     * by the same backlog item. If the todo is already highest ranked, does
     * nothing.
     * 
     * @param todoId
     * @throws ObjectNotFoundException
     * 
     */

    public abstract void rankTodoTop(int todoId) throws ObjectNotFoundException;

    /**
     * Gives the todo the lowest rank among the todos owned by the same backlog
     * item. If the todo is already lowest ranked, does nothing.
     * 
     * @param todoId
     * @throws ObjectNotFoundException
     */
    public abstract void rankTodoBottom(int todoId)
            throws ObjectNotFoundException;

    public abstract Todo getTodoById(int todoId) throws ObjectNotFoundException;
    
    public Map<Integer,Integer> getTodoCountByState(int backlogItemId);
    
    public void delete(int todoId);
    
    public Todo getTodo(int todoId);
}
