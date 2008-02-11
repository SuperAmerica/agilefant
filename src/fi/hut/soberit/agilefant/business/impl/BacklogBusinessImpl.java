package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.EffortSumData;

/**
 * 
 * @author Teemu Ilmonen
 * 
 */
public class BacklogBusinessImpl implements BacklogBusiness {
    private BacklogItemDAO backlogItemDAO;

    private HistoryBusiness historyBusiness;

    private BacklogDAO backlogDAO;

    private UserDAO userDAO;

    private AssignmentDAO assignmentDAO;

    // @Override
    public void deleteMultipleItems(int backlogId, int[] backlogItemIds)
            throws ObjectNotFoundException {
        Backlog backlog = backlogDAO.get(backlogId);
        if (backlog == null) {
            throw new ObjectNotFoundException("Backlog id " + backlogId
                    + " was invalid.");
        }

        for (int id : backlogItemIds) {
            Collection<BacklogItem> items = backlog.getBacklogItems();
            Iterator<BacklogItem> iterator = items.iterator();
            while (iterator.hasNext()) {
                BacklogItem item = iterator.next();
                if (item.getId() == id) {
                    iterator.remove();
                    backlogItemDAO.remove(id);
                }
            }
        }
        historyBusiness.updateBacklogHistory(backlog.getId());
    }

    public BacklogItem createBacklogItemToBacklog(int backlogId) {
        BacklogItem backlogItem = new BacklogItem();
        backlogItem = new BacklogItem();
        Backlog backlog = backlogDAO.get(backlogId);
        if (backlog == null)
            return null;
        backlogItem.setBacklog(backlog);
        backlog.getBacklogItems().add(backlogItem);
        return backlogItem;
    }

    /**
     * {@inheritDoc}
     */
    public void changePriorityOfMultipleItems(int[] backlogItemIds,
            Priority priority) throws ObjectNotFoundException {

        for (int id : backlogItemIds) {
            BacklogItem bli = backlogItemDAO.get(id);
            if (bli == null) {
                throw new ObjectNotFoundException(
                        "Could not change priority. Object with id " + id
                                + " was not found.");
            }
            bli.setPriority(priority);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void moveMultipleBacklogItemsToBacklog(int backlogItemIds[],
            int targetBacklogId) throws ObjectNotFoundException {
        Backlog targetBacklog = backlogDAO.get(targetBacklogId);

        // Store source backlogs of the backlog items to be able to update their
        // history data.

        Set<Integer> sourceBacklogIds = new HashSet<Integer>();

        if (targetBacklog == null) {
            throw new ObjectNotFoundException("Target backlog with id: "
                    + targetBacklogId + " was not found.");
        }

        for (int id : backlogItemIds) {
            BacklogItem bli = backlogItemDAO.get(id);
            if (bli == null) {
                throw new ObjectNotFoundException("Backlog item with id: " + id
                        + " was not found.");
            }
            Backlog sourceBacklog = bli.getBacklog();

            if (sourceBacklog.getId() != targetBacklog.getId()) {

                // Store the source backlog ids into the set
                sourceBacklogIds.add(bli.getBacklog().getId());

                // Set originalestimate to current effortleft
                bli.setOriginalEstimate(bli.getEffortLeft());

                // Remove iteration goal
                if (bli.getIterationGoal() != null) {
                    bli.getIterationGoal().getBacklogItems().remove(bli);
                    bli.setIterationGoal(null);
                }
                
                // Set backlog item's backlog to target backlog
                bli.setBacklog(targetBacklog);
                backlogItemDAO.store(bli);

                // Remove BLI from source backlog
                sourceBacklog.getBacklogItems().remove(bli);

                // Store source backlog
                backlogDAO.store(sourceBacklog);

                // Add backlog item to new Backlog's backlog item list
                targetBacklog.getBacklogItems().add(bli);
            }
        }

        backlogDAO.store(targetBacklog);

        // Update history data for source backlogs
        for (Integer sourceBacklogId : sourceBacklogIds) {
            historyBusiness.updateBacklogHistory(sourceBacklogId);

        }

        // Update history data for target backlog
        historyBusiness.updateBacklogHistory(targetBacklog.getId());
    }

    /** {@inheritDoc} * */
    public EffortSumData getEffortLeftSum(Collection<BacklogItem> bliList) {
        EffortSumData data = new EffortSumData();
        AFTime hours = new AFTime(0);
        int nonEstimatedBLIs = 0;
        for (BacklogItem bli : bliList) {
            if (bli.getEffortLeft() == null)
                nonEstimatedBLIs++;
            else
                hours.add(bli.getEffortLeft());            
        }
        data.setEffortHours(hours);
        data.setNonEstimatedItems(nonEstimatedBLIs);
        return data;
    }

    /** {@inheritDoc} * */
    public EffortSumData getOriginalEstimateSum(Collection<BacklogItem> bliList) {
        EffortSumData data = new EffortSumData();
        AFTime hours = new AFTime(0);
        int nonEstimatedBLIs = 0;
        for (BacklogItem bli : bliList) {
            if (bli.getOriginalEstimate() == null)
                nonEstimatedBLIs++;
            else
                hours.add(bli.getOriginalEstimate());            
        }
        data.setEffortHours(hours);
        data.setNonEstimatedItems(nonEstimatedBLIs);
        return data;
    }
    
    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public HistoryBusiness getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(HistoryBusiness historyBusiness) {
        this.historyBusiness = historyBusiness;
    }

    public void setAssignments(int[] selectedUserIds, Backlog backlog) {
        if (backlog != null) {
            // Edit project assignments: remove all assignments, then create
            // some.
            Collection<Assignment> oldAssignments = backlog.getAssignments();
            for (Assignment ass : oldAssignments) {
                assignmentDAO.remove(ass);
            }
            Collection<User> users = getUsers(backlog, true);
            for (User user : users) {
                user.getAssignments().removeAll(oldAssignments);
                userDAO.store(user);
            }
            backlog.getAssignments().clear();
            backlogDAO.store(backlog);

            if (selectedUserIds != null) {
                for (int id : selectedUserIds) {
                    
                    User user = userDAO.get(id);
                    if (user != null) {
                        Assignment assignment = new Assignment(user, backlog);
                        user.getAssignments().add(assignment);
                        backlog.getAssignments().add(assignment);
                        assignmentDAO.store(assignment);
                        userDAO.store(user);
                        backlogDAO.store(backlog);
                    }
                }
            }
        }
    }
    
    public void removeAssignments(User user) {
        if (user != null) {
            Collection<Assignment> assignments = assignmentDAO.getAll();
            for (Assignment ass : assignments) {
                if (ass.getUser().getId() == user.getId()) {
                    user.getAssignments().remove(ass);
                    ass.getBacklog().getAssignments().remove(ass);
                    userDAO.store(user);
                    backlogDAO.store(ass.getBacklog());
                    assignmentDAO.remove(ass);
                }
            }
        }
    }

    public Collection<User> getUsers(Backlog backlog, boolean areAssigned) {
        Collection<User> users;
        Collection<Assignment> assignments = backlog.getAssignments();
        users = new HashSet<User>();
        for (Assignment ass : assignments) {
            users.add(ass.getUser());
        }
        if (areAssigned)
            return users;
        else {
            Collection<User> allUsers = userDAO.getAll();
            allUsers.removeAll(users);
            return allUsers;
        }

    }

    public int getNumberOfAssignedUsers(Backlog backlog) {
        return getUsers(backlog, true).size();
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
    }
}
