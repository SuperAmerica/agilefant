package fi.hut.soberit.agilefant.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;

public class BacklogAction extends ActionSupport {
    private static final long serialVersionUID = 8061288993804046816L;

    private int backlogId;

    private BacklogDAO backlogDAO;

    private int backlogItemId;

    private int[] backlogItemIds;
    
    private int targetBacklogId;

    private int targetPriority;
    
    private int targetState;
    
    private int targetIterationGoalId;
    
    private int keepResponsibles = 0;
    
    //Ugly hack, refactor later
    private int iterationGoalId = 0;
    
    private Map<Integer, String> userIds = new HashMap<Integer, String>();

    private BacklogItemDAO backlogItemDAO;

    private BacklogBusiness backlogBusiness;

    /**
     * Used to determine what action is taken when multiple
     * <code>BacklogItems</code> are manipulated at once.
     */
    private String itemAction;

    public String edit() {
        Backlog backlog = backlogDAO.get(backlogId);
        return solveResult(backlog);
    }

    public int getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

    public int getIterationGoalId() {
        return iterationGoalId;
    }
    
    public void setIterationGoalId(int iterationGoalId) {
        this.iterationGoalId = iterationGoalId;
    }
    
    
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public String moveBacklogItem() {
        Backlog backlog = backlogDAO.get(backlogId);
        BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
        if (backlog == null) {
            super.addActionError(super.getText("backlog.notFound"));
            return Action.ERROR;
        }
        if (backlogItem == null) {
            super.addActionError(super.getText("backlogItem.notFound"));
            return Action.ERROR;
        }

        backlogItem.getBacklog().getBacklogItems().remove(backlogItem);
        backlog.getBacklogItems().add(backlogItem);
        backlogItem.setBacklog(backlog);
        if (backlogItem.getIterationGoal() != null) {
            backlogItem.getIterationGoal().getBacklogItems().remove(backlogItem);
            backlogItem.setIterationGoal(null);
        }
        backlogItemDAO.store(backlogItem);

        return this.solveResult(backlog);
    }

    /**
     * Moves selected <code>BacklogItems</code> to selected
     * <code>Backlog</code>.
     * 
     * @return <code>Action.ERROR</code> if there was an error, otherwise a
     *         redirect to display current backlog.
     */
    public void moveSelectedItems() {
        Log logger = LogFactory.getLog(getClass());

        logger.info("Moving " + backlogItemIds.length + " items + "
                + " to backlog: " + targetBacklogId);

        try {
            backlogBusiness.moveMultipleBacklogItemsToBacklog(backlogItemIds,
                    targetBacklogId);
        } catch (ObjectNotFoundException e) {
            super.addActionError(e.getMessage());
        }
    }

    /**
     * Changes selected <code>BacklogItems'</code> priority
     * 
     * @return <code>Action.ERROR</code> if there was an error, otherwise a
     *         redirect to display current backlog.
     */
    public void changePriorityOfSelectedItems() {
        try {
            backlogBusiness.changePriorityOfMultipleItems(backlogItemIds,
                    Priority.values()[targetPriority]);
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText(e.getMessage()));
        }
    }
    
    /**
     * Changes selected <code>BacklogItems'</code> priority
     * 
     * @return <code>Action.ERROR</code> if there was an error, otherwise a
     *         redirect to display current backlog.
     */
    public void changeStateOfSelectedItems() {
        try {
            backlogBusiness.changeStateOfMultipleItems(backlogItemIds,
                    State.values()[targetState]);
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText(e.getMessage()));
        }
    }
    
    /**
     * Changes selected <code>BacklogItems'</code> priority
     * 
     * @return <code>Action.ERROR</code> if there was an error, otherwise a
     *         redirect to display current backlog.
     */
    public void changeIterationGoalOfSelectedItems() {
        try {
            backlogBusiness.changeIterationGoalOfMultipleItems(backlogItemIds,
                    targetIterationGoalId);
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText(e.getMessage()));
        }
    }

    public void changeResponsiblesOfSelectedItems() {
        try {
            backlogBusiness.setResponsiblesForMultipleBacklogItems(backlogItemIds,
                    userIds.keySet());
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText(e.getMessage()));
        }
    }
    
    /**
     * Deletes multiple selected <code>BacklogItems</code>.
     * 
     * @return <code>Action.ERROR</code> if there was an error, otherwise a
     *         redirect to display current backlog.
     */
    public void deleteSelectedItems() {
        try {
            backlogBusiness.deleteMultipleItems(backlogId, backlogItemIds);
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText(e.getMessage()));
        }
    }

    /**
     * Perform an action on multiple <code>BacklogItems</code>
     * 
     * @return <code>"move"</code> if the action is to move,
     *         <code>"delete"</code> if the action is to delete,
     *         <code>"changePriority"</code> if the action is to change
     *         priority and <code>Action.ERROR</code> if the action can't be
     *         determined.
     */
    public String doActionOnMultipleBacklogItems() {
        Backlog currentBacklog = this.backlogDAO.get(backlogId);
        Log logger = LogFactory.getLog(getClass());
        
        if (itemAction.equals("ChangeSelected")) {
            // Check, that some backlog items were selected
            if (backlogItemIds == null) {
                super.addActionError(super.getText("backlogItems.notSelected"));
                return Action.ERROR;
            }
            
            // If a target priority was selected
            if (targetPriority != -1) {
                changePriorityOfSelectedItems();
            }
            
            // If a target state was selected
            if (targetState != -1) {
                changeStateOfSelectedItems();
            }
            
            if (targetIterationGoalId != -1) {
                changeIterationGoalOfSelectedItems();
            }
            
            if (keepResponsibles != 1) { 
                changeResponsiblesOfSelectedItems();
            }
                                    
            // Move selected items
            moveSelectedItems();
            
        }
        else if (itemAction.equals("DeleteSelected")) {
            deleteSelectedItems();
        }
        else {
            logger.error("Invalid action on multiple backlog items: " + itemAction);
            return Action.ERROR;    
        }

        
        /* Check for errors */
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        
        
        return this.solveResult(currentBacklog);
    }

    protected String solveResult(Backlog backlog) {
        if (backlog == null) {
            super.addActionError(super.getText("backlog.notFound"));
            return Action.ERROR;
        } else  if (backlog instanceof Product) {
            return "editProduct";
        } else if (backlog instanceof Project) {
            return "editProject";
        } else if (backlog instanceof Iteration) {
            if (iterationGoalId == 0) {
                return "editIteration";
            } else {
                return "editIterationGoal";
            }
        }
        super.addActionError(super.getText("backlog.unknownType"));
        return Action.ERROR;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }

    public int[] getSelected() {
        return backlogItemIds;
    }

    public int getTargetBacklog() {
        return targetBacklogId;
    }

    public void setTargetBacklog(int targetBacklogId) {
        this.targetBacklogId = targetBacklogId;
    }

    public void setSelected(int[] backlogItemIds) {
        this.backlogItemIds = backlogItemIds;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public String getItemAction() {
        return itemAction;
    }

    public void setItemAction(String itemAction) {
        this.itemAction = itemAction;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public BacklogBusiness getBacklogBusiness() {
        return backlogBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public int getTargetPriority() {
        return targetPriority;
    }

    public void setTargetPriority(int targetPriority) {
        this.targetPriority = targetPriority;
    }

    public int getTargetState() {
        return targetState;
    }

    public void setTargetState(int targetState) {
        this.targetState = targetState;
    }

    public int getTargetIterationGoalId() {
        return targetIterationGoalId;
    }

    public void setTargetIterationGoalId(int targetIterationGoalId) {
        this.targetIterationGoalId = targetIterationGoalId;
    }

    public int getKeepResponsibles() {
        return keepResponsibles;
    }

    public void setKeepResponsibles(int keepResponsibles) {
        this.keepResponsibles = keepResponsibles;
    }

    public Map<Integer, String> getUserIds() {
        return userIds;
    }

    public void setUserIds(Map<Integer, String> userIds) {
        this.userIds = userIds;
    }

    public void setBacklogItemIds(int[] backlogItemIds) {
        this.backlogItemIds = backlogItemIds;
    }
}
