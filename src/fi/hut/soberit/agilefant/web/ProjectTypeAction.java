package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import org.springframework.dao.DataIntegrityViolationException;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.ProjectTypeBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.ProjectType;

public class ProjectTypeAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = 1342432127514974396L;

    private int projectTypeId;

    private ProjectType projectType;

    private ProjectTypeBusiness projectTypeBusiness;

    private Collection<ProjectType> projectTypes;

    private ProjectBusiness projectBusiness;

    private String jsonData = "";

    public String getAll() {
        projectTypes = projectTypeBusiness.getAll();
        return Action.SUCCESS;
    }

    public String create() {
        projectTypeId = 0;
        projectType = new ProjectType();
        return Action.SUCCESS;
    }

    public String edit() {
        projectType = projectTypeBusiness.get(projectTypeId);
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
            fillable = projectTypeBusiness.get(projectTypeId);
            if (fillable == null) {
                super.addActionError(super.getText("projectType.notFound"));
                return Action.ERROR;
            }
        }
        this.fillObject(fillable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }
        try {
            projectTypeBusiness.store(fillable);
        } catch (DataIntegrityViolationException dve) {
            super.addActionError(super.getText("projectType.duplicateName"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public String ajaxStoreProjectType() {
        if (projectType == null) {
            super.addActionError(super.getText("projectType.missingForm"));
        }
        ProjectType fillable = new ProjectType();
        if (projectTypeId > 0) {
            fillable = projectTypeBusiness.get(projectTypeId);
            if (fillable == null) {
                super.addActionError(super.getText("projectType.notFound"));
                return CRUDAction.AJAX_ERROR;
            }
        }
        this.fillObject(fillable);
        if (super.hasActionErrors()) {
            return CRUDAction.AJAX_ERROR;
        }
        try {
            projectTypeBusiness.store(fillable);
        } catch (DataIntegrityViolationException dve) {
            super.addActionError(super.getText("projectType.duplicateName"));
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }

    public String delete() {
        projectType = projectTypeBusiness.get(projectTypeId);
        if (projectType == null) {
            super.addActionError(super.getText("projectType.notFound"));
            return Action.ERROR;
        }
        if (projectBusiness.countByProjectType(projectTypeId) > 0) {
            super.addActionError(super.getText("projectType.projectsLinked"));
            return Action.ERROR;
        }
        try {
            projectBusiness.deleteProjectType(projectTypeId);
        } catch (OperationNotPermittedException e) {
            super.addActionError(e.getMessage());
            return Action.ERROR;
        } catch (ObjectNotFoundException e) {
            super.addActionError(e.getMessage());
            return Action.ERROR;
        }

        return Action.SUCCESS;
    }

    protected void fillObject(ProjectType fillable) {
        if (projectType.getName() == null
                || projectType.getName().trim().equals("")) {
            super.addActionError("Project type name cannot be empty.");
        }
        fillable.setName(projectType.getName());
        fillable.setDescription(projectType.getDescription());
    }

    public String getProjectTypeJSON() {
        if (projectTypeId > 0) {
            jsonData = projectBusiness.getProjectTypeJSON(projectTypeId);
        } else {
            jsonData = projectBusiness.getAllProjectTypesAsJSON();
        }
        return Action.SUCCESS;
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

    public void setProjectTypeBusiness(ProjectTypeBusiness projectTypeBusiness) {
        this.projectTypeBusiness = projectTypeBusiness;
    }

    public ProjectBusiness getProjectBusiness() {
        return projectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

}