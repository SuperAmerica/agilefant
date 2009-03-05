package fi.hut.soberit.agilefant.business;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BacklogItemResponsibleContainer;
import fi.hut.soberit.agilefant.util.TodoMetrics;

/**
 * Interface for business functionality related to backlog items.
 * 
 * @author Mika Salminen
 * 
 */
public interface BacklogItemBusiness {

    /**
     * Returns backlog item by its id.
     * 
     * @param backlogItemId
     *                the id of wanted backlog item
     * 
     * @return backlog item for the id
     */
    public BacklogItem getBacklogItem(int backlogItemId);

    /**
     * Removes backlog item specified by id. ObjectNotFoundException is thrown
     * if backlog item with given id does not exist.
     * 
     * @param backlogItemId
     *                id of backlog item to be removed
     * 
     * @return true if item was removed, else false
     */

    public void removeBacklogItem(int backlogItemId)
            throws ObjectNotFoundException;

    /**
     * Updates backlog item's effort left and status.
     * 
     * @param backlogItemId
     *                id of backlog item to update
     * @param newState
     *                new state for item
     * @param newEffortLeft
     *                new effort left for item
     */
    public void updateBacklogItemStateAndEffortLeft(int backlogItemId,
            State newState, AFTime newEffortLeft)
            throws ObjectNotFoundException;

    /**
     * Transaction wrapper method to call
     * <code>BacklogItemBusiness.updateBacklogItemStateAndEffortLeft</code>
     * and <code>TaskBusiness.updateMultipleTasks</code> in one
     * transaction.
     * 
     * @param backlogItemId
     *                backlog item to update
     * @param newState
     *                new state for backlog item
     * @param newEffortLeft
     *                new effort left for backlog item
     * @param newTaskStates
     *                <code>Map</code> of elements of form [task_id =>
     *                new_status]
     */

    public void updateBacklogItemEffortLeftStateAndTaskStates(
            int backlogItemId, State newState, AFTime newEffortLeft,
            Map<Integer, State> newTaskStates, Map<Integer, String> newTaskNames) throws ObjectNotFoundException;

    /**
     * Resets backlog item's original estimate and effort left to null.
     * ObjectNotFoundException is thrown if backlog item with given id does not
     * exist.
     * 
     * @param backlogItemId
     *                id of the backlog item to be reset
     */
    public void resetBliOrigEstAndEffortLeft(int backlogItemId)
            throws ObjectNotFoundException;
    
    /**
     * Sets backlog item's tasks to done.
     * @param backlogItemId
     * @throws ObjectNotFoundException
     */
    public void setTasksToDone(int backlogItemId) throws ObjectNotFoundException;
    
    public HistoryBusiness getHistoryBusiness();

    public void setHistoryBusiness(HistoryBusiness historyBusiness);
    
    /**
     * Get the list of possible responsibles for a bli.
     * <p>
     * Returns the union of all enabled users and the bli's responsibles.
     * @param bli
     */
    public List<User> getPossibleResponsibles(BacklogItem bli);
    
    public Map<BacklogItem, TodoMetrics> getTasksByBacklog(Backlog backlog);
    
    public Map<BacklogItem, List<BacklogItemResponsibleContainer>> getResponsiblesByBacklog(Backlog backlog);
    
    public List<BacklogItem> getBacklogItemsByBacklog(Backlog backlog);
    
    /**
     * Update or create new backlog item and persist the object to database.
     * 
     * @param backlogItemId
     *            Object ID when updating existing entry or 0 when creating a
     *            new one.
     * @param backlogId
     *            Parent backlog ID.
     * @param dataItem
     *            Transfer object containing simple property values.
     * @param responsibles
     * @param iterationGoalId
     *            Zero if not iteration goal is to be set.
     * @return Persisted database object.
     * @throws ObjectNotFoundException
     *             Thrown if given iteration goal, backlog or existing backlog
     *             item is not found.
     */
    public BacklogItem storeBacklogItem(int backlogItemId, int backlogId,
            BacklogItem dataItem, Set<Integer> responsibles, int iterationGoalId)
            throws ObjectNotFoundException;

    /**
     * Update or create new backlog item and persist the object to database.
     * 
     * @param backlogItemId
     *            Persisted backlog item when updating existing entry or null
     *            when creating a new one.
     * @param backlogId
     *            Parent backlog.
     * @param dataItem
     *            Transfer object containing simple property values.
     * @param responsibles
     * @param iterationGoalId
     *            null if not iteration goal is to be set.
     * @return Persisted database object.
     * @throws ObjectNotFoundException
     *             Thrown if given iteration goal, backlog or existing backlog
     *             item is not found.
     */
    public BacklogItem storeBacklogItem(BacklogItem storable, Backlog backlog,
            BacklogItem dataItem, Set<User> responsibles,
            IterationGoal iterationGoal);

    /**
     * Move backlog item from one backlog to another. Updates backlog histories.
     * Removes product themes if the backlog item is moved to an other product.
     * 
     * @param item
     * @param backlog
     */
    public void moveItemToBacklog(BacklogItem item, Backlog backlog);

    /**
     * Sets backlog item's iteration goal if backlog item is in an iteration.
     * Checks that the iteration goal belongs to the same iteration as the
     * backlog item.
     * 
     * @param item
     * @param iterationGoal
     */
    public void setBacklogItemIterationGoal(BacklogItem item,
            IterationGoal iterationGoal);

}
