package fi.hut.soberit.agilefant.web;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Project;

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
}