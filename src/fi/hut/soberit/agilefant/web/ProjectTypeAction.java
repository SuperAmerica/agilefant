package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.Project;

public class ProjectTypeAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = 1342432127514974396L;

    private int projectTypeId;

    private ProjectType projectType;

    private ProjectTypeDAO projectTypeDAO;

    private ProjectDAO projectDAO;

    private Collection<ProjectType> projectTypes;
    
    private ProjectBusiness projectBusiness;

    public String getAll() {
        projectTypes = projectTypeDAO.getAll();
        return Action.SUCCESS;
    }

    public String create() {
        projectTypeId = 0;
        projectType = new ProjectType();
        return Action.SUCCESS;
    }

    public String edit() {
        projectType = projectTypeDAO.get(projectTypeId);
        if (projectType == null) {
            super.addActionError(super.getText("projectType.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public String store() {
        if (projectType == null) {
            super.addActionError(super.getText("projectType.missingForm"));
        }
        ProjectType fillable = new ProjectType();
        if (projectTypeId > 0) {
            fillable = projectTypeDAO.get(projectTypeId);
            if (fillable == null) {
                super.addActionError(super.getText("projectType.notFound"));
                return Action.ERROR;
            }
        }
        this.fillObject(fillable);
        projectTypeDAO.store(fillable);
        return Action.SUCCESS;
    }

    public String delete() {
        projectType = projectTypeDAO.get(projectTypeId);
        if (projectType == null) {
            super.addActionError(super.getText("projectType.notFound"));
            return Action.ERROR;
        }
        for (Project d : projectDAO.getAll()) {
            if (d.getProjectType().getId() == projectTypeId) {
                super.addActionError(super
                        .getText("projectType.projectsLinked"));
                return Action.ERROR;
            }
        }
        try {
            projectBusiness.deleteProjectType(projectTypeId);
        }
        catch (OperationNotPermittedException e) {
            super.addActionError(e.getMessage());
            return Action.ERROR;
        }
        catch (ObjectNotFoundException e) {
            super.addActionError(e.getMessage());
            return Action.ERROR;
        }
        
        return Action.SUCCESS;
    }

    protected void fillObject(ProjectType fillable) {
        fillable.setName(projectType.getName());
        fillable.setDescription(projectType.getDescription());
    }

    public int getProjectTypeId() {
        return projectTypeId;
    }

    public void setProjectTypeId(int projectTypeId) {
        this.projectTypeId = projectTypeId;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public Collection<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    public void setProjectTypeDAO(ProjectTypeDAO projectTypeDAO) {
        this.projectTypeDAO = projectTypeDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public ProjectBusiness getProjectBusiness() {
        return projectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

}