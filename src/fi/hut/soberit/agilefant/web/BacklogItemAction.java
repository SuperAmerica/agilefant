package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;

public class BacklogItemAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = -4289013472775815522L;

    private HistoryBusiness historyBusiness;

    private BacklogDAO backlogDAO;

    private BacklogItemDAO backlogItemDAO;

    private int backlogId = 0;

    private int backlogItemId;

    private State state;

    private AFTime effortLeft;

    private BacklogItem backlogItem;

    private Backlog backlog;

    private Collection<BacklogItem> backlogItems = new ArrayList<BacklogItem>();

    private UserDAO userDAO;

    private IterationGoalDAO iterationGoalDAO;

    private int iterationGoalId;

    private int assigneeId;

    private TaskDAO taskDAO;

    private Log logger = LogFactory.getLog(getClass());

    private BacklogBusiness backlogBusiness;

    private BacklogItemBusiness backlogItemBusiness;

    public BacklogItemBusiness getBacklogItemBusiness() {
        return backlogItemBusiness;
    }

    public void setBacklogItemBusiness(BacklogItemBusiness backlogItemBusiness) {
        this.backlogItemBusiness = backlogItemBusiness;
    }

    public BacklogBusiness getBacklogBusiness() {
        return backlogBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public String create() {
        // Id of newly created, not yet persisted backlog item is 0
        backlogItemId = 0;

        if (backlogId == 0) {
            return Action.SUCCESS;
        } else {
            backlogItem = backlogBusiness.createBacklogItemToBacklog(backlogId);
            if(backlogItem == null) {
                super.addActionError(super.getText("backlog.notFound"));
                return Action.ERROR;
            }
            backlog = backlogItem.getBacklog();
            backlogId = backlog.getId();
            return Action.SUCCESS;
        }
    }

    public String delete() {
        try {
            backlogItemBusiness.removeBacklogItem(backlogItemId);
        } catch (ObjectNotFoundException e) {
            super.addActionError(super.getText("backlogItem.notFound"));
            return Action.ERROR;
        }

        // If exception was not thrown from business method, return success.
        return Action.SUCCESS;
    }

    public String edit() {
        backlogItem = backlogItemBusiness.getBacklogItem(backlogItemId);
        if (backlogItem == null) {
            super.addActionError(super.getText("backlogItem.notFound"));
            return Action.ERROR;
        }
        backlog = backlogItem.getBacklog();
        backlogId = backlog.getId();

        historyBusiness.updateBacklogHistory(backlog.getId());

        return Action.SUCCESS;
    }

    /**
     * TODO: refactor this!
     */
    public String store() {
        Integer storableId;
        BacklogItem storable = new BacklogItem();
        Backlog newBacklog;
        Backlog oldBacklog = null;

        if (backlogItemId > 0) {
            storable = backlogItemBusiness.getBacklogItem(backlogItemId);
            if (storable == null) {
                super.addActionError(super.getText("backlogItem.notFound"));
                return Action.ERROR;
            }
            oldBacklog = storable.getBacklog();
        }
        newBacklog = backlogDAO.get(backlogId);

        this.fillStorable(storable);

        if (super.hasActionErrors()) {
            return Action.ERROR;
        }

        // Store backlog item
        this.backlogItemId = (Integer) backlogItemDAO.create(storable);

        /*
         * This should be handled inside business...
         */
        historyBusiness.updateBacklogHistory(newBacklog.getId());

        if (oldBacklog != null)
            historyBusiness.updateBacklogHistory(oldBacklog.getId());

        return Action.SUCCESS;
    }

    /**
     * Updates backlog item's state and effort left.
     * 
     * 
     */

    public String quickStoreBacklogItem() {
        backlogItem = backlogItemDAO.get(backlogItemId);
        if (backlogItem == null) {
            super.addActionError(super.getText("backlogItem.notFound"));
            return Action.ERROR;
        } else {
            
            // Set the effort left as original estimate if backlog item's
            // original estimate is null in database
            if (backlogItem.getOriginalEstimate() == null) {
                backlogItem.setEffortLeft(this.effortLeft);
                backlogItem.setOriginalEstimate(this.effortLeft);
            }
            else if (backlogItem.getEffortLeft() != null &&
                    this.effortLeft == null) {
                backlogItem.setEffortLeft(new AFTime(0));
            }
            else {
                backlogItem.setEffortLeft(this.effortLeft);
            }

            backlogItem.setState(this.state);
            // set effortleft to 0 if state changed to done
            if(this.state == State.DONE)
                backlogItem.setEffortLeft(new AFTime(0));

            backlogItemDAO.store(backlogItem);
            historyBusiness.updateBacklogHistory(backlogItem.getBacklog()
                    .getId());
            return Action.SUCCESS;
        }
    }

    protected void fillStorable(BacklogItem storable) {
        User oldAssignee = storable.getAssignee();
        User newAssignee = null;

        if ((oldAssignee == null && assigneeId > 0)
                || (oldAssignee != null && oldAssignee.getId() != assigneeId)) {
            if (assigneeId > 0) {
                newAssignee = userDAO.get(assigneeId);
            }
            storable.setAssignee(newAssignee);
        }

        if (this.backlogItem.getIterationGoal() != null) {
            IterationGoal goal = iterationGoalDAO.get(this.backlogItem
                    .getIterationGoal().getId());
            // IterationGoal goal = iterationGoalDAO.get(iterationGoalId);
            storable.setIterationGoal(goal);
        }
        if (this.backlogItem.getName().equals("")) {
            super.addActionError(super.getText("backlogitem.missingName"));
            return;
        }
        storable.setName(this.backlogItem.getName());
        storable.setDescription(this.backlogItem.getDescription());
        storable.setPriority(this.backlogItem.getPriority());

        // Set efforts and state for backlog item

        storable.setState(backlogItem.getState());

        // set effortleft to 0 if state changed to done
        if(backlogItem.getState() == State.DONE) {
            backlogItem.setEffortLeft(new AFTime(0));
            this.effortLeft = new AFTime(0);
        }
        
        /*
         * Set effort left. If this is new item set its effort to be the
         * original effort. Otherwise set its effort to be the received effort
         * left from text field.
         */
        if (storable.getOriginalEstimate() == null) {
            storable.setOriginalEstimate(backlogItem.getOriginalEstimate());
            storable.setEffortLeft(backlogItem.getOriginalEstimate());
        } else if (storable.getEffortLeft() != null
                && backlogItem.getEffortLeft() == null) {
            storable.setEffortLeft(new AFTime(0));
        } else {
            storable.setEffortLeft(backlogItem.getEffortLeft());
        }        
        
        // TODO: REFACTOR THIS when moving backlog items from backlog to another
        // change
        // backlog item's original estimate to current effort left.

        backlog = backlogDAO.get(backlogId);
        if (backlog == null) {
            super.addActionError(super.getText("backlog.notFound"));
        }
        // if we're moving backlogitem, set originalestimate to current
        // effortleft.
        if (storable.getId() > 0 && storable.getBacklog() != null
                && storable.getBacklog() != this.backlog
                && this.backlog != null) {
            storable.getBacklog().getBacklogItems().remove(storable);
            storable.setOriginalEstimate(storable.getEffortLeft());
            backlog.getBacklogItems().add(storable);
        }
        storable.setBacklog(backlog);
    }

    public Backlog getBacklog() {
        return backlog;
    }

    public void setBacklog(Backlog backlog) {
        this.backlog = backlog;
    }

    public int getBacklogId() {
        return backlogId;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

    public BacklogItem getBacklogItem() {
        return backlogItem;
    }

    public void setBacklogItem(BacklogItem backlogItem) {
        this.backlogItem = backlogItem;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public Collection<BacklogItem> getBacklogItems() {
        return backlogItems;
    }

    public void setBacklogItems(Collection<BacklogItem> backlogItems) {
        this.backlogItems = backlogItems;
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    /*
     * protected BacklogItemDAO getBacklogItemDAO() { return
     * this.backlogItemDAO; }
     */

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
        this.iterationGoalDAO = iterationGoalDAO;
    }

    public void setAssigneeId(int assigneeId) {
        this.assigneeId = assigneeId;
    }

    /**
     * Setter for Spring IoC
     * 
     * @param iterationGoalId
     *                iteration goal id to be set
     */
    public void setIterationGoalId(int iterationGoalId) {
        this.iterationGoalId = iterationGoalId;
    }

    /**
     * Getter for Spring IoC
     * 
     * @return iteration goal id
     */
    public int getIterationGoalId() {
        return iterationGoalId;
    }

    /**
     * @return the task data access object
     */
    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    /**
     * @param taskDAO
     *                the task data access object to set
     */
    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public String getBacklogItemName() {
        return backlogItem.getName();
    }

    public void setBacklogItemName(String backlogItemName) {
        backlogItem.setName(backlogItemName);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public AFTime getEffortLeft() {
        return effortLeft;
    }

    public void setEffortLeft(AFTime effortLeft) {
        this.effortLeft = effortLeft;
    }

    public HistoryBusiness getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(HistoryBusiness historyBusiness) {
        this.historyBusiness = historyBusiness;
    }

}
