package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.UserComparator;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
public class BacklogItemBusinessImpl implements BacklogItemBusiness {
    private BacklogItemDAO backlogItemDAO;
    private TaskBusiness taskBusiness;
    private HistoryBusiness historyBusiness;
    private UserBusiness userBusiness;
    private HourEntryBusiness hourEntryBusiness;

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public BacklogItem getBacklogItem(int backlogItemId) {
        return backlogItemDAO.get(backlogItemId);
    }
    
    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void removeBacklogItem(int backlogItemId)
            throws ObjectNotFoundException {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);

        if (backlogItem == null) {
            throw new ObjectNotFoundException(
                    "Backlog item with given id was not found.");
        }
        
        // Remove all hourEntries related to this backlogItem  
        hourEntryBusiness.removeHourEntriesByParent( backlogItem );
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
            Map<Integer, State> newTaskStates, Map<Integer, String> newTaskNames) throws ObjectNotFoundException {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        if (backlogItem == null) {
            throw new ObjectNotFoundException("Backlog item with id: "
                    + backlogItemId + " not found.");
        } else {
            updateBacklogItemStateAndEffortLeft(backlogItemId, newState,
                newEffortLeft);
            taskBusiness.updateMultipleTasks(backlogItem, newTaskStates, newTaskNames);
        }
    }
    
    public void setTasksToDone(int backlogItemId) throws ObjectNotFoundException {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        if (backlogItem == null) {
            throw new ObjectNotFoundException("Backlog item with id: "
                    + backlogItemId + " not found.");
        } else {
            Map<Integer, State> doneStates = new HashMap<Integer, State>();
            for (Task t: backlogItem.getTasks()) {
                doneStates.put(t.getId(), State.DONE);
            }
            taskBusiness.updateMultipleTasks(backlogItem, doneStates, new HashMap<Integer, String>());
        }
    }

    public void resetBliOrigEstAndEffortLeft(int backlogItemId) throws ObjectNotFoundException {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        if (backlogItem == null) {
            throw new ObjectNotFoundException("Backlog item with id: "
                    + backlogItemId + " not found.");
        } else {
            //Set effort left and original estimate to null
            backlogItem.setEffortLeft(null);
            backlogItem.setOriginalEstimate(null);
            backlogItemDAO.store(backlogItem);
            historyBusiness.updateBacklogHistory(backlogItem.getBacklog()
                    .getId());
        }
    }
    
    /** {@inheritDoc} */
    public List<User> getPossibleResponsibles(BacklogItem bli) {
        Set<User> userSet = new HashSet<User>();
                
        // Get all enabled users
        userSet.addAll(userBusiness.getEnabledUsers());
        
        // Get all previous responsibles
        if (bli != null) {
            userSet.addAll(bli.getResponsibles());
        }
        
        // Create the list and sort it
        List<User> userList = new ArrayList<User>(userSet);
        Collections.sort(userList, new UserComparator());
        
        return userList;
    }       

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }

    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
    
}
