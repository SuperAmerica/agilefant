package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import java.util.List;

import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.EffortLeftSumData;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
public class BacklogItemBusinessImpl implements BacklogItemBusiness {
    private BacklogItemDAO backlogItemDAO;
    private TaskBusiness taskBusiness;
    private HistoryBusiness historyBusiness;

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public BacklogItem getBacklogItem(int backlogItemId) {
        return backlogItemDAO.get(backlogItemId);
    }

    public void removeBacklogItem(int backlogItemId)
            throws ObjectNotFoundException {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);

        if (backlogItem == null) {
            throw new ObjectNotFoundException(
                    "Backlog item with given id was not found.");
        }
        // Store backlog to be able to update its history
        Backlog backlog = backlogItem.getBacklog();
        backlogItemDAO.remove(backlogItem);
        // Update backlog history for item's backlog
        historyBusiness.updateBacklogHistory(backlog.getId());
    }

    public HistoryBusiness getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(HistoryBusiness historyBusiness) {
        this.historyBusiness = historyBusiness;
    }

    public void updateBacklogItemStateAndEffortLeft(int backlogItemId,
            State newState, AFTime newEffortLeft)
            throws ObjectNotFoundException {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        if (backlogItem == null) {
            throw new ObjectNotFoundException("Backlog item with id: "
                    + backlogItemId + " not found.");
        } else {

            /*
             * Set the effort left as original estimate if backlog item's
             * original estimate is null in database
             */
            if (backlogItem.getOriginalEstimate() == null) {
                backlogItem.setEffortLeft(newEffortLeft);
                backlogItem.setOriginalEstimate(newEffortLeft);
            } else if (backlogItem.getEffortLeft() != null
                    && newEffortLeft == null) {
                backlogItem.setEffortLeft(new AFTime(0));
            } else {
                backlogItem.setEffortLeft(newEffortLeft);
            }

            backlogItem.setState(newState);
            // set effortleft to 0 if state changed to done
            if (newState == State.DONE)
                backlogItem.setEffortLeft(new AFTime(0));

            backlogItemDAO.store(backlogItem);
            historyBusiness.updateBacklogHistory(backlogItem.getBacklog()
                    .getId());
        }
    }

    public void updateBacklogItemEffortLeftStateAndTaskStates(
            int backlogItemId, State newState, AFTime newEffortLeft,
            Map<Integer, State> newTaskStates) throws ObjectNotFoundException {
        updateBacklogItemStateAndEffortLeft(backlogItemId, newState,
                newEffortLeft);
        taskBusiness.updateMultipleTaskStates(newTaskStates);
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }
    
    public EffortLeftSumData getEffortLeftSum(Collection<BacklogItem> items) {
        EffortLeftSumData data = new EffortLeftSumData();
        AFTime hours = new AFTime(0);
        int nonEstimatedBLIs = 0;
        for (BacklogItem bli : items) {
            if (bli.getEffortLeft() == null)
                nonEstimatedBLIs++;
            else
                hours.add(bli.getEffortLeft());            
        }
        data.setEffortLeftHours(hours);
        data.setNonEstimatedItems(nonEstimatedBLIs);
        return data;
    }
}
