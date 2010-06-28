package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;

public interface TaskBusiness extends GenericBusiness<Task> {

    /**
     * Populates and stores a task.
     * @param storyId
     *            the parent story's id, or zero if none.
     * @param storyToStarted TODO
     * 
     * @return the newly stored task
     */
    public Task storeTask(Task task, Integer iterationId, Integer storyId, boolean storyToStarted); // ,

    public Task resetOriginalEstimate(int taskId);

    /**
     * Moves the tasks and updates both new and old iteration histories.
     */
    public Task move(Task task, Integer iterationId, Integer storyId);

    /**
     * Sets the task to done and removes all corresponding work queue entries.
     * @param task
     */
    public void setTaskToDone(Task task);
    
    /**
     * Sets the tasks parent.
     * <p>
     * Parent can be either a story or an iteration. Only one of the parameters:
     * <code>storyId</code> or <code>iterationId</code> should be given.
     * 
     * @throws IllegalArgumentException
     *             if both ids or none were given
     * @throws ObjectNotFoundException
     *             if iteration or story was not found
     */
    public void assignParentForTask(Task task, Integer iterationId,
            Integer storyId) throws IllegalArgumentException,
            ObjectNotFoundException;

    /* RANKING */
    /**
     * Moves the task under the given parent and ranks it one rank below the
     * upper task.
     * <p>
     * Note: upperTask should be left null if topmost rank is wanted.
     * 
     * @param parentStoryId
     *            TODO
     * @param parentIterationId
     *            TODO
     * 
     * @throws IllegalArgumentException
     *             if upperTask's parent is not same as given parent.
     */
    public Task rankAndMove(Task task, Task upperTask, Integer parentStoryId,
            Integer parentIterationId) throws IllegalArgumentException;

    /**
     * Rank the task to be under the given task
     * <p>
     * The parameter upperTask should be null if the task should be ranked
     * topmost.
     * 
     * @param task
     *            the task to rank
     * @param upperTask
     *            the task under which the other task should be ranked. null if
     *            topmost.
     * @return TODO
     * 
     * @throws IllegalArgumentException
     *             if the upper task is not under same story or iteration
     * @throws IllegalArgumentException
     *             if the given task was null
     */
    public Task rankUnderTask(Task task, Task upperTask)
            throws IllegalArgumentException;

    /**
     * Ranks the task to bottom most item under given parent.
     * <p>
     * Only one parent id should be given, other left null.
     * 
     * @throws IllegalArgumentException
     *             if both parents were given
     */
    public Task rankToBottom(Task task, Integer parentStoryId,
            Integer parentIterationId) throws IllegalArgumentException;

    /**
     * Adds the given user to the set of responsibles for task
     */
    public void addResponsible(Task task, User user);

    /**
     * Deletes a task and optionally handles hour entries.
     * 
     * @throws UnsupportedOperationException
     *             if hourEntryHandlingChoice is null and the task contains hour
     *             entries
     * @param id
     *            task id
     * @param hourEntryHandlingChoice
     *            handling choice or null
     */
    void delete(int id, HourEntryHandlingChoice hourEntryHandlingChoice);

    /**
     * Deletes a task and optionally handles hour entries.
     * 
     * @throws UnsupportedOperationException
     *             if hourEntryHandlingChoice is null and the task contains hour
     *             entries
     * @param task
     *            task object
     * @param hourEntryHandlingChoice
     *            handling choice or null
     */
    void delete(Task task, HourEntryHandlingChoice hourEntryHandlingChoice);

    /**
     * 
     * Deletes a task and updates the iteration history if the task is in an
     * iteration or in a story that is in an iteration.
     * 
     * @throws UnsupportedOperationException
     *             if hourEntryHandlingChoice is null and the task contains hour
     *             entries
     * @param id
     *            task id
     * @param hourEntryHandlingChoice
     *            handling choice or null
     */
    void deleteAndUpdateHistory(int id,
            HourEntryHandlingChoice hourEntryHandlingChoice);
}
