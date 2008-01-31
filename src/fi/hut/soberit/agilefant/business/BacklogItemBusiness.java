package fi.hut.soberit.agilefant.business;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;

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
     * and <code>TaskBusiness.updateMultipleTaskStates</code> in one
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
            Map<Integer, State> newTaskStates) throws ObjectNotFoundException;

    public HistoryBusiness getHistoryBusiness();

    public void setHistoryBusiness(HistoryBusiness historyBusiness);

}
