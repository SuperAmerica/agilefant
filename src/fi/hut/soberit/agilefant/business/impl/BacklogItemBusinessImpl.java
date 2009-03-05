package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.BacklogItemComparator;
import fi.hut.soberit.agilefant.util.BacklogItemPriorityComparator;
import fi.hut.soberit.agilefant.util.BacklogItemResponsibleContainer;
import fi.hut.soberit.agilefant.util.BacklogItemUserComparator;
import fi.hut.soberit.agilefant.util.TodoMetrics;
import fi.hut.soberit.agilefant.util.UserComparator;

/**
 * 
 * @author Teemu Ilmonen
 * @author Pasi Pekkanen
 * 
 */
public class BacklogItemBusinessImpl implements BacklogItemBusiness {
    private BacklogItemDAO backlogItemDAO;
    private TaskBusiness taskBusiness;
    private HistoryBusiness historyBusiness;
    private UserBusiness userBusiness;
    private HourEntryBusiness hourEntryBusiness;
    private SettingBusiness settingBusiness;
    private BacklogBusiness backlogBusiness;
    private IterationGoalDAO iterationGoalDAO;

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
    
    public BacklogItem storeBacklogItem(int backlogItemId, int backlogId, BacklogItem dataItem, Set<Integer> responsibles, int iterationGoalId) throws ObjectNotFoundException {
        BacklogItem item = null; 
        if(backlogItemId > 0) {
            item = backlogItemDAO.get(backlogItemId);
            if(item == null) {
                throw new ObjectNotFoundException("backlogItem.notFound");
            }
        }
        Backlog backlog = backlogBusiness.getBacklog(backlogId);
        if(backlog == null) {
            throw new ObjectNotFoundException("backlog.notFound");
        }
        
        IterationGoal iterationGoal = null;
        if(iterationGoalId > 0 && backlog instanceof Iteration) {
            iterationGoal = iterationGoalDAO.get(iterationGoalId);
            if(iterationGoal == null) {
                throw new ObjectNotFoundException("iterationGoal.notFound");
            }
        }
        
        Set<User> responsibleUsers = new HashSet<User>();
        
        for(int userId : responsibles) {
            User responsible = userBusiness.getUser(userId);
            if(responsible != null) {
                responsibleUsers.add(responsible);
            }
        }
        
        return this.storeBacklogItem(item, backlog, dataItem, responsibleUsers, iterationGoal);
    }

    public BacklogItem storeBacklogItem(BacklogItem storable, Backlog backlog, BacklogItem dataItem, Set<User> responsibles, IterationGoal iterationGoal) {

        boolean historyUpdated = false;
        
        if(backlog == null) {
            throw new IllegalArgumentException("Backlog must not be null.");
        }
        if(dataItem == null) {
            throw new IllegalArgumentException("No data given.");
        }
        if(storable == null) {
            storable = new BacklogItem();
            storable.setCreatedDate(Calendar.getInstance());
            try {
                storable.setCreator(SecurityUtil.getLoggedUser()); //may fail if request is multithreaded
            } catch(Exception e) { } //however, saving item should not fail.
        }
        storable.setDescription(dataItem.getDescription());
        storable.setEffortLeft(dataItem.getEffortLeft());
        storable.setName(dataItem.getName());
        if(storable.getOriginalEstimate() == null) {
            storable.setOriginalEstimate(dataItem.getOriginalEstimate());
        }
        storable.setPriority(dataItem.getPriority());
        storable.setState(dataItem.getState());
        
        if(dataItem.getState() == State.DONE) {
            storable.setEffortLeft(new AFTime(0));
        } else if(dataItem.getEffortLeft() == null) {
            storable.setEffortLeft(storable.getOriginalEstimate());
        }
        
        if(storable.getBacklog() != null && storable.getBacklog() != backlog) {
            this.moveItemToBacklog(storable, backlog);
            historyUpdated = true;
        } else if(storable.getBacklog() == null) {
            storable.setBacklog(backlog);
        }
        
        storable.setResponsibles(responsibles);
        
        this.setBacklogItemIterationGoal(storable, iterationGoal);
        
        BacklogItem persisted;
        
        if(storable.getId() == 0) {
            int persistedId = (Integer)backlogItemDAO.create(storable);
            persisted = backlogItemDAO.get(persistedId);
        } else {
            backlogItemDAO.store(storable);
            persisted = storable;
        }
        if(!historyUpdated) {
            historyBusiness.updateBacklogHistory(backlog.getId());
        }
        return persisted;
    }
    
    public void moveItemToBacklog(BacklogItem item, Backlog backlog) {

        Backlog oldBacklog = item.getBacklog();
        oldBacklog.getBacklogItems().remove(item);
        item.setBacklog(backlog);
        backlog.getBacklogItems().add(item);
        historyBusiness.updateBacklogHistory(oldBacklog.getId());
        historyBusiness.updateBacklogHistory(backlog.getId());
        
        if(item.getIterationGoal() != null) {
            item.getIterationGoal().getBacklogItems().remove(item);
            item.setIterationGoal(null);
        }        
        if(!backlogBusiness.isUnderSameProduct(oldBacklog, backlog)) {
            //remove only product themes
            Collection<BusinessTheme> removeThese = new ArrayList<BusinessTheme>();;
            for(BusinessTheme theme : item.getBusinessThemes()) {
                if(!theme.isGlobal()) {
                    removeThese.add(theme);
                }
            }
            for(BusinessTheme theme : removeThese) {
                item.getBusinessThemes().remove(theme);
            }
        }
    }
    public void setBacklogItemIterationGoal(BacklogItem item, IterationGoal iterationGoal) {
        if(iterationGoal != null && item.getBacklog() == iterationGoal.getIteration()) {
            if(item.getIterationGoal() != null) {
                item.getIterationGoal().getBacklogItems().remove(item);
            }
            item.setIterationGoal(iterationGoal);
            iterationGoal.getBacklogItems().add(item);
        } else {
            if(item.getIterationGoal() != null) {
                item.getIterationGoal().getBacklogItems().remove(item);
            }
            item.setIterationGoal(null);
        }
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
        backlog.getBacklogItems().remove(backlogItem);
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
            throw new ObjectNotFoundException("backlogItem.notFound");
        }

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

    public void updateBacklogItemEffortLeftStateAndTaskStates(
            int backlogItemId, State newState, AFTime newEffortLeft,
            Map<Integer, State> newTaskStates, Map<Integer, String> newTaskNames) throws ObjectNotFoundException {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        if (backlogItem == null) {
            throw new ObjectNotFoundException("backlogItem.notFound");
        } else {
            updateBacklogItemStateAndEffortLeft(backlogItemId, newState,
                newEffortLeft);
            taskBusiness.updateMultipleTasks(backlogItem, newTaskStates, newTaskNames);
        }
    }
    
    public void setTasksToDone(int backlogItemId) throws ObjectNotFoundException {
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        if (backlogItem == null) {
            throw new ObjectNotFoundException("backlogItem.notFound");
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
            throw new ObjectNotFoundException("backlogItem.notFound");
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

    
    //TODO: write test
    public List<BacklogItem> getBacklogItemsByBacklog(Backlog backlog) {
        if(backlog != null) {
            List<BacklogItem> items = backlogItemDAO.getBacklogItemsByBacklog(backlog);
            Collections.sort(items, new BacklogItemComparator(new BacklogItemPriorityComparator()));
            //do we need to load spent effort sums
            if(settingBusiness.isHourReportingEnabled()) {
                Map<BacklogItem, AFTime> spentEffort = hourEntryBusiness.getSumsByBacklog(backlog);
                for(BacklogItem item : items) {
                    if(spentEffort.containsKey(item)) {
                        item.setEffortSpent(spentEffort.get(item));
                    }
                }
            }
            return items;
        }
        return null;
    }

    //TODO: write test
    public Map<BacklogItem, List<BacklogItemResponsibleContainer>> getResponsiblesByBacklog(Backlog backlog) {
        if(backlog != null) {
           Collection<User> assignees = null;
           Map<BacklogItem, List<BacklogItemResponsibleContainer>> result = new HashMap<BacklogItem, List<BacklogItemResponsibleContainer>>();
           
           if(backlog instanceof Iteration) {
               assignees = ((Iteration)backlog).getProject().getResponsibles();
           } else if(backlog instanceof Project) {
               assignees = ((Project)backlog).getResponsibles();
           } 
           List<Object[]> data = backlogItemDAO.getResponsiblesByBacklog(backlog);
           for(Object[] row : data) {
               BacklogItem item = (BacklogItem)row[0];
               User user = (User)row[1];
               boolean inProject = false;
               if(result.get(item) == null) {
                   result.put(item, new ArrayList<BacklogItemResponsibleContainer>());
               }
               if(assignees == null || assignees.contains(user)) {
                   inProject = true;
               }
               result.get(item).add(new BacklogItemResponsibleContainer(user,inProject));
           }
           //order users
           for(BacklogItem item : result.keySet()) {
              Collections.sort(result.get(item), new BacklogItemUserComparator());
           }
           return result;
        }
        return null;
    }

    public Map<BacklogItem, TodoMetrics> getTasksByBacklog(Backlog backlog) {
        if(backlog != null) {
            return backlogItemDAO.getTasksByBacklog(backlog);
        }
        return null;
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

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
        this.iterationGoalDAO = iterationGoalDAO;
    }


    
}
