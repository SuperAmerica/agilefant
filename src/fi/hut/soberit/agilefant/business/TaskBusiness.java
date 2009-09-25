package fi.hut.soberit.agilefant.business;

import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public interface TaskBusiness extends GenericBusiness<Task> {

    /**
     * Populates and stores a task.
     * @param storyId the parent story's id, or zero if none.
     * @return the newly stored task
     */
    public Task storeTask(Task task, Integer iterationId, Integer storyId, Set<Integer> userIds);
    
    public Task resetOriginalEstimate(int taskId);

    /**
     * Moves the tasks and updates both new and old iteration histories.
     */
    public Task move(Task task, Integer iterationId, Integer storyId);
    
    /**
     * Sets the tasks parent.
     * <p>
     * Parent can be either a story or an iteration. Only one of
     * the parameters: <code>storyId</code> or <code>iterationId</code>
     * should be given.
     * @throws IllegalArgumentException if both ids or none were given
     * @throws ObjectNotFoundException if iteration or story was not found
     */
    public void assignParentForTask(Task task, Integer iterationId, Integer storyId)
        throws IllegalArgumentException, ObjectNotFoundException;
    
    
    /* RANKING */
    /**
     * Moves the task under the given parent and ranks it one rank below the upper task.
     * <p>
     * Note: upperTask should be left null if topmost rank is wanted.
     * @param parentStoryId TODO
     * @param parentIterationId TODO
     * 
     * @throws IllegalArgumentException if upperTask's parent is not same as given parent.
     */
    public Task rankAndMove(Task task, Task upperTask, Integer parentStoryId, Integer parentIterationId) throws IllegalArgumentException;
    
    /**
     * Rank the task to be under the given task
     * <p>
     * The parameter upperTask should be null if the task should be ranked topmost.
     * 
     * @param task the task to rank
     * @param upperTask the task under which the other task should be ranked. null if topmost.
     * @return TODO
     * 
     * @throws IllegalArgumentException if the upper task is not under same story or iteration
     * @throws IllegalArgumentException if the given task was null
     */
    public Task rankUnderTask(Task task, Task upperTask) throws IllegalArgumentException;
    
    /**
     * Ranks the task to bottom most item under given parent.
     * <p>
     * Only one parent id should be given, other left null.
     * 
     * @throws IllegalArgumentException if both parents were given 
     */
    public Task rankToBottom(Task task, Integer parentStoryId, Integer parentIterationId) throws IllegalArgumentException;

    /**
     * Adds the given user to the set of responsibles for task
     */
    public void addResponsible(Task task, User user);

}
