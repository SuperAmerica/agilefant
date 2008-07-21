package fi.hut.soberit.agilefant.business;

import java.util.Map;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;

/**
 * Business interface for handling functionality related to tasks.
 * 
 * @author Mika Salminen
 * 
 */
public interface TaskBusiness {

    /**
     * Updates multiple tasks' states with one call. Takes Map with elements of
     * form: <code>[task_id => new_status] </code> as parameter.
     * 
     * 
     * @param newStatesMap
     *                <code>Map</code> with elements
     *                <code>[task_id => new_status]</code> defining the new
     *                states for tasks.
     * 
     */

    public void updateMultipleTaskStates(Map<Integer, State> newStatesMap)
            throws ObjectNotFoundException;

    /**
     * Gives the task the lowest rank (i.e. currentLowestRank + 1) among the
     * tasks owned by the same backlog item. If the task is lowest ranked, does
     * nothing.
     * 
     * @param taskId
     * @throws ObjectNotFoundException
     */

    public abstract void rankTaskUp(int taskId) throws ObjectNotFoundException;

    /**
     * Gives the task the highest rank (i.e. rank value 0) among the tasks owned
     * by the same backlog item. If the task is highest ranked, does nothing.
     * 
     * @param taskId
     * @throws ObjectNotFoundException
     */
    public abstract void rankTaskDown(int taskId)
            throws ObjectNotFoundException;

    /**
     * Gives the task the highest rank (i.e. rank value 0) among the tasks owned
     * by the same backlog item. If the task is already highest ranked, does
     * nothing.
     * 
     * @param taskId
     * @throws ObjectNotFoundException
     */

    public abstract void rankTaskTop(int taskId) throws ObjectNotFoundException;

    /**
     * Gives the task the lowest rank among the tasks owned by the same backlog
     * item. If the task is already lowest ranked, does nothing.
     * 
     * @param taskId
     * @throws ObjectNotFoundException
     */
    public abstract void rankTaskBottom(int taskId)
            throws ObjectNotFoundException;

    public abstract Task getTaskById(int taskId) throws ObjectNotFoundException;
    
    public Map<Integer,Integer> getTaskCountByState(int backlogItemId);
}
