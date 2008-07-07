package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationGoalBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.util.EffortSumData;

public class IterationGoalAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = -8772191083987255387L;

    private IterationDAO iterationDAO;

    private IterationGoalDAO iterationGoalDAO;
    
    private BacklogBusiness backlogBusiness;
    
    private IterationGoalBusiness iterationGoalBusiness;
    
    private HourEntryBusiness hourEntryBusiness;

    private int iterationId;

    private int iterationGoalId;

    private IterationGoal iterationGoal;

    private Iteration iteration;

    private Collection<IterationGoal> iterationGoals = new ArrayList<IterationGoal>();
    
    private EffortSumData effortLeftSum;
    
    private String moveTo = "";
    
    //private AFTime effortLeftSum = new AFTime(0);
    
    private EffortSumData origEstSum;
    
    //private AFTime origEstSum = new AFTime(0);

    public String create() {
        iterationGoalId = 0;
        iterationGoal = new IterationGoal();
        iteration = iterationDAO.get(iterationId);
        return Action.SUCCESS;
    }

    public String delete() {
        iterationGoal = iterationGoalDAO.get(iterationGoalId);
        if (iterationGoal == null) {
            super.addActionError(super.getText("iterationgGoal.notFound"));
            return Action.ERROR;
        }
        for (BacklogItem bi : iterationGoal.getBacklogItems()) {
            bi.setIterationGoal(null);
        }
        // iterationId = iteration.getId();
        iterationGoalDAO.remove(iterationGoalId);
        return Action.SUCCESS;
    }

    public String edit() {
        iterationGoal = iterationGoalDAO.get(iterationGoalId);
        if (iterationGoal == null) {
            super.addActionError(super.getText("iterationgGoal.notFound"));
            return Action.ERROR;
        }
        iteration = iterationGoal.getIteration();
        iterationId = iteration.getId();
        
        // Calculate effort lefts and original estimates
        Collection<BacklogItem> items = iterationGoal.getBacklogItems();
        effortLeftSum = backlogBusiness.getEffortLeftSum(items);
        origEstSum = backlogBusiness.getOriginalEstimateSum(items);
        
        // Load Hour Entry sums to iteration's BLIs.
        hourEntryBusiness.loadSumsToBacklogItems(iteration);
        
        return Action.SUCCESS;
    }
    
    public String prioritizeIterationGoal() {
        
        iterationGoal = iterationGoalDAO.get(iterationGoalId);
        
        if (iterationGoal == null) {
            return CRUDAction.AJAX_ERROR;
        }
        
        if (moveTo.equalsIgnoreCase("top")) {
            // Send the iteration goal to top
            iterationGoalBusiness.moveToTop(iterationGoal);
        }
        else if (moveTo.equalsIgnoreCase("up")) {
            // Move the iteration goal up
            iterationGoalBusiness.moveUp(iterationGoal);
        }
        else if (moveTo.equalsIgnoreCase("down")) {
            // Move the iteration goal up
            iterationGoalBusiness.moveDown(iterationGoal);
        }
        else if (moveTo.equalsIgnoreCase("bottom")) {
            // Send the iteration goal to bottom
            iterationGoalBusiness.moveToBottom(iterationGoal);
        }
        else {
            // Invalid selection
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }

    public String store() {
        IterationGoal storable = new IterationGoal();
        if (iterationGoalId > 0) {
            storable = iterationGoalDAO.get(iterationGoalId);
            if (storable == null) {
                super.addActionError(super.getText("iterationgGoal.notFound"));
                return Action.ERROR;
            }
        }
        this.fillStorable(storable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        if (iterationGoalId == 0)
            iterationGoalId = (Integer) iterationGoalDAO.create(storable);
        else
            iterationGoalDAO.store(storable);
        return Action.SUCCESS;
    }

    protected void fillStorable(IterationGoal storable) {
        iteration = iterationDAO.get(iterationId);
        if (this.iterationGoal.getName() == null ||
                this.iterationGoal.getName().trim().equals("")) {
            super.addActionError(super.getText("iterationGoal.missingName"));
            return;
        }
        if (iteration == null)
            super.addActionError(super.getText("iteration.notFound"));
        else {
            if (storable.getId() > 0) {
                storable.getIteration().getIterationGoals().remove(storable);
                iteration.getIterationGoals().add(storable);
            }
            storable.setIteration(iteration);
            storable.setName(this.iterationGoal.getName());
            storable.setDescription(this.iterationGoal.getDescription());
            
            // Set the priority number
            if (iterationGoalId == 0) {
                storable.setPriority(iterationGoalBusiness.getNewPriorityNumber(iteration));
            }
            
        }
    }

    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
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

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
        this.iterationGoalDAO = iterationGoalDAO;
    }

    public Collection<IterationGoal> getIterationGoals() {
        return iterationGoals;
    }

    public void setIterationGoals(Collection<IterationGoal> iterationGoals) {
        this.iterationGoals = iterationGoals;
    }

    public EffortSumData getEffortLeftSum() {
        return effortLeftSum;
    }

    public EffortSumData getOriginalEstimateSum() {
        return origEstSum;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setMoveTo(String moveTo) {
        this.moveTo = moveTo;
    }

    public void setIterationGoalBusiness(IterationGoalBusiness iterationGoalBusiness) {
        this.iterationGoalBusiness = iterationGoalBusiness;
    }

    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }
    
    
}
