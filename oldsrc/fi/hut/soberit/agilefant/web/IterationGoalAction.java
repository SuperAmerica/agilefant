package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.IterationGoalBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.util.IterationGoalMetrics;
import flexjson.JSONSerializer;

public class IterationGoalAction extends ActionSupport {

    private static final long serialVersionUID = -8772191083987255387L;    
    
    private IterationGoalBusiness iterationGoalBusiness;
    
    private BacklogBusiness backlogBusiness;

    private int iterationId;
    
    private Iteration iteration;

    private int iterationGoalId;
    
    private int priority;
    
    private boolean moveBacklogItems = false;

    private IterationGoal iterationGoal = new IterationGoal();
    
    
    private String jsonData = "";

    public String create() {
        iterationGoalId = 0;
        iterationGoal = new IterationGoal();
        try {
            Backlog bl = backlogBusiness.getBacklog(iterationId);
            iteration = (Iteration)bl;
        } catch (Exception e) {

        }
        return Action.SUCCESS;
    }

    public String delete() {
        try {
            iterationGoalBusiness.remove(iterationGoalId);
        } catch (Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }

    public String store() {
        try {
            iterationGoal = iterationGoalBusiness.store(iterationGoalId, iterationGoal.getName(), 
                    iterationId, iterationGoal.getDescription(), priority);
        } catch (Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        jsonData = new JSONSerializer().serialize(iterationGoal);
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String ajaxGetIterationGoals() {
        jsonData = backlogBusiness.getIterationGoalsAsJSON(iterationId);        
        return Action.SUCCESS;
    }
    
    public String getMetrics() {
        IterationGoalMetrics metrics;
        try {
            metrics = this.iterationGoalBusiness.getIterationGoalMetrics(iterationGoalId, iterationId);
            jsonData = new JSONSerializer().serialize(metrics);
        } catch (Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String getContents() {
        jsonData = new JSONSerializer().serialize(iterationGoalBusiness.getIterationGoalContents(iterationGoalId, iterationId));
        return CRUDAction.AJAX_SUCCESS;
    }
    
    public String moveIterationGoal() {
        try {
            this.iterationGoalBusiness.attachGoalToIteration(iterationGoalId, iterationId, moveBacklogItems);
        } catch (Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        
        return CRUDAction.AJAX_SUCCESS;
    }

    public int getIterationId() {
        return iterationId;
    }

    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }

    public IterationGoal getIterationGoal() {
        return iterationGoal;
    }

    public void setIterationGoal(IterationGoal iterationGoal) {
        this.iterationGoal = iterationGoal;
    }

    public int getIterationGoalId() {
        return iterationGoalId;
    }

    public void setIterationGoalId(int iterationGoalId) {
        this.iterationGoalId = iterationGoalId;
    }

    public void setIterationGoalBusiness(IterationGoalBusiness iterationGoalBusiness) {
        this.iterationGoalBusiness = iterationGoalBusiness;
    }

    public String getJsonData() {
        return jsonData;
    }

    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setMoveBacklogItems(boolean moveBacklogItems) {
        this.moveBacklogItems = moveBacklogItems;
    }


    
    
}
