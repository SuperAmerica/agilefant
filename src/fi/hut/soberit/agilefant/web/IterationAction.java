package fi.hut.soberit.agilefant.web;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.EffortSumData;

public class IterationAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = -448825368336871703L;

    private int iterationId;

    private Iteration iteration;

    private Backlog backlog;

    private IterationDAO iterationDAO;

    private ProjectDAO projectDAO;

    private BacklogItemDAO backlogItemDAO;

    private BacklogDAO backlogDAO;

    private Project project;

    private int projectId;

    private IterationGoalDAO iterationGoalDAO;

    private int iterationGoalId;

    private String startDate;

    private String endDate;

    private String dateFormat;

    private HistoryBusiness historyBusiness;
    
    private BacklogBusiness backlogBusiness;
    
    private Map<Integer, AFTime> iterationGoalEffLeftSums = new HashMap<Integer, AFTime>();
    
    private Map<Integer, AFTime> iterationGoalOrigEstSums = new HashMap<Integer, AFTime>();
    
    private EffortSumData effortLeftSum;

    private EffortSumData origEstSum;
    
    public String create() {
        iterationId = 0;
        iteration = new Iteration();
        backlog = iteration;
        
        return Action.SUCCESS;
    }

    public String edit() {
        iteration = iterationDAO.get(iterationId);
        Date startDate = iteration.getStartDate();

        if (iteration == null) {
            // super.addActionError(super.getText("iteration.notFound"));
            // return Action.INPUT;
            return Action.SUCCESS;
        }
        if (startDate == null) {
            startDate = new Date(0);
        }

        project = iteration.getProject();

        /*
         * We need Backlog-class object to generate backlog list in
         * _backlogList.jsp
         */
        backlog = iteration;

        if (project == null) {
            super
                    .addActionError(super
                            .getText("iteration.projectNotFound"));
            return Action.INPUT;
        }
        projectId = project.getId();
        
        /* Get the effort left sums of iteration goals */
        for (IterationGoal ig : iteration.getIterationGoals()) {
            AFTime effort = new AFTime(0);
            AFTime estimate = new AFTime(0);
            
            for (BacklogItem bli : ig.getBacklogItems()) {
                if (bli.getEffortLeft() != null) {
                    effort.add(bli.getEffortLeft());
                }
                if (bli.getOriginalEstimate() != null) {
                    estimate.add(bli.getOriginalEstimate()); 
                }
            }
            
            iterationGoalEffLeftSums.put(new Integer(ig.getId()), effort);
            iterationGoalOrigEstSums.put(new Integer(ig.getId()), estimate);
        }
        
        /* Get the original estimate sums of iteration goals */

        // Calculate effort lefts and original estimates
        Collection<BacklogItem> items = backlog.getBacklogItems();
        effortLeftSum = backlogBusiness.getEffortLeftSum(items);
        origEstSum = backlogBusiness.getOriginalEstimateSum(items);
      
        
        return Action.SUCCESS;
    }

    public String store() {
        if (iteration == null) {
            super.addActionError(super.getText("iteration.missingForm"));
            return Action.INPUT;
        }
        project = projectDAO.get(projectId);
        if (project == null) {
            super
                    .addActionError(super
                            .getText("iteration.projectNotFound"));
            return Action.INPUT;
        }
        Iteration fillable = new Iteration();
        if (iterationId > 0) {
            fillable = iterationDAO.get(iterationId);
            if (iteration == null) {
                super.addActionError(super.getText("iteration.notFound"));
                return Action.INPUT;
            }
        }

        try {
            this.fillObject(fillable);
        } catch (ParseException e) {
            super.addActionError(super.getText("backlog.unparseableDate")
                    + super.getText("webwork.shortDateTime.format"));
            return Action.ERROR;
        }

        if (super.hasActionErrors()) {
            return Action.ERROR;
        }

        if (iterationId == 0)
            iterationId = (Integer) iterationDAO.create(fillable);
        else
            iterationDAO.store(fillable);
        
        historyBusiness.updateBacklogHistory(fillable.getId());
        return Action.SUCCESS;
    }

    public String delete() {
        iteration = iterationDAO.get(iterationId);
        if (iteration == null) {
            super.addActionError(super.getText("projectType.notFound"));
            return Action.ERROR;
        }
        if (iteration.getBacklogItems().size() > 0
                || iteration.getIterationGoals().size() > 0) {
            super.addActionError(super
                    .getText("iteration.notEmptyWhenDeleting"));
            return Action.ERROR;
        }
        iterationDAO.remove(iteration);
        return Action.SUCCESS;
    }

    protected void fillObject(Iteration fillable) throws ParseException {
        fillable.setEndDate(endDate, dateFormat);
        fillable.setStartDate(startDate, dateFormat);
        if (this.iteration.getName().equals("")) {
            super.addActionError(super.getText("iteration.missingName"));
            return;
        }
        if (fillable.getStartDate().after(fillable.getEndDate())) {
            super
                    .addActionError(super
                            .getText("backlog.startDateAfterEndDate"));
            return;
        }
        fillable.setProject(this.project);
        fillable.setName(this.iteration.getName());
        fillable.setDescription(this.iteration.getDescription());
        if (fillable.getStartDate().after(fillable.getEndDate())) {
            super
                    .addActionError(super
                            .getText("backlog.startDateAfterEndDate"));
            return;
        }
    }

    public String moveIterationGoal() {
        Iteration iteration = iterationDAO.get(iterationId);
        IterationGoal iterationGoal = iterationGoalDAO.get(iterationGoalId);
        if (iteration == null) {
            super.addActionError(super.getText("iteration.notFound"));
            return Action.ERROR;
        }
        if (iterationGoal == null) {
            super.addActionError(super.getText("iterationGoal.notFound"));
        }

        iterationGoal.getIteration().getIterationGoals().remove(iterationGoal);
        iteration.getIterationGoals().add(iterationGoal);
        iterationGoal.setIteration(iteration);
        iterationGoalDAO.store(iterationGoal);

        return Action.SUCCESS;
    }

    public int getIterationId() {
        return iterationId;
    }

    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }

    public Iteration getIteration() {
        return iteration;
    }

    public void setIteration(Iteration iteration) {
        this.iteration = iteration;
        this.backlog = iteration;
    }

    public Backlog getBacklog() {
        return backlog;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public Collection<Iteration> getAllIterations() {
        return this.iterationDAO.getAll();
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
        this.iterationGoalDAO = iterationGoalDAO;
    }

    public void setIterationGoalId(int iterationGoalId) {
        this.iterationGoalId = iterationGoalId;
    }

    public int getIterationGoalId() {
        return iterationGoalId;
    }

    /**
     * @return the backlogItemDAO
     */
    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    /**
     * @param backlogItemDAO
     *                the backlogItemDAO to set
     */
    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    /**
     * @return the backlogDAO
     */
    public BacklogDAO getBacklogDAO() {
        return backlogDAO;
    }

    /**
     * @param backlogDAO
     *                the backlogDAO to set
     */
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate
     *                the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate
     *                the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the dateFormat
     */
    public String getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat
     *                the dateFormat to set
     */
    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public HistoryBusiness getHistoryBusiness() {
        return historyBusiness;
    }

    public void setHistoryBusiness(HistoryBusiness historyBusiness) {
        this.historyBusiness = historyBusiness;
    }

    public Map<Integer, AFTime> getIterationGoalEffLeftSums() {
        return iterationGoalEffLeftSums;
    }

    public void setIterationGoalEffLeftSums(
            Map<Integer, AFTime> iterationGoalEffLeftSums) {
        this.iterationGoalEffLeftSums = iterationGoalEffLeftSums;
    }

    public Map<Integer, AFTime> getIterationGoalOrigEstSums() {
        return iterationGoalOrigEstSums;
    }

    public void setIterationGoalOrigEstSums(
            Map<Integer, AFTime> iterationGoalOrigEstSums) {
        this.iterationGoalOrigEstSums = iterationGoalOrigEstSums;
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
}