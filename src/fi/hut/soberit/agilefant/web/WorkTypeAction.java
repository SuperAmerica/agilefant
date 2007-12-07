package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.WorkType;

public class WorkTypeAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = -7854997077386161741L;

    private int workTypeId;

    private int projectTypeId;

    private ProjectType projectType;

    private WorkType workType;

    private WorkTypeDAO workTypeDAO;

    private ProjectTypeDAO projectTypeDAO;

    private int storedWorkTypeId;

    public String create() {
        projectType = projectTypeDAO.get(projectTypeId);
        if (projectType == null) {
            super
                    .addActionError(super
                            .getText("workType.projectTypeNotFound"));
            return Action.ERROR;
        }
        this.workTypeId = 0;
        this.workType = new WorkType();
        return Action.SUCCESS;
    }

    public String edit() {
        projectType = projectTypeDAO.get(projectTypeId);
        if (projectType == null) {
            super
                    .addActionError(super
                            .getText("workType.projectTypeNotFound"));
            return Action.ERROR;
        }
        workType = workTypeDAO.get(workTypeId);
        if (workType == null) {
            super.addActionError(super.getText("wotkType.notFound"));
            return Action.INPUT;
        }
        return Action.SUCCESS;
    }

    public String delete() {
        workType = workTypeDAO.get(workTypeId);
        if (workType == null) {
            super.addActionError(super.getText("wotkType.notFound"));
            return Action.INPUT;
        }
        workTypeDAO.remove(workType);
        return Action.SUCCESS;
    }

    public String store() {
        if (workType == null) {
            super.addActionError(super.getText("workType.missingForm"));
            return Action.INPUT;
        }
        projectType = projectTypeDAO.get(projectTypeId);
        if (projectType == null) {
            super
                    .addActionError(super
                            .getText("workType.projectTypeNotFound"));
            return Action.INPUT;
        }
        WorkType fillable = new WorkType();
        if (workTypeId > 0) {
            fillable = workTypeDAO.get(workTypeId);
            if (workType == null) {
                super.addActionError(super.getText("workType.notFound"));
                return Action.INPUT;
            }
        }
        fillObject(fillable);
        fillable.setProjectType(projectType);
        workTypeDAO.store(fillable);

        storedWorkTypeId = fillable.getId();

        return Action.SUCCESS;
    }

    protected void fillObject(WorkType fillable) {
        fillable.setProjectType(this.projectType);
        fillable.setDescription(this.workType.getDescription());
        fillable.setName(this.workType.getName());
    }

    public int getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(int projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public WorkType getWorkType() {
        return workType;
    }

    public void setWorkType(WorkType workType) {
        this.workType = workType;
    }

    public int getWorkTypeId() {
        return workTypeId;
    }

    public void setWorkTypeId(int workTypeId) {
        this.workTypeId = workTypeId;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectTypeDAO(ProjectTypeDAO projectTypeDAO) {
        this.projectTypeDAO = projectTypeDAO;
    }

    public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
        this.workTypeDAO = workTypeDAO;
    }

    public int getStoredWorkTypeId() {
        return storedWorkTypeId;
    }
}
