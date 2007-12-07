package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.Project;

public class ProjectPortfolioAction extends ActionSupport {

    private static final long serialVersionUID = -4749839976470627112L;

    private ProjectBusiness projectBusiness;

    private int projectId;
    
    private Collection<ProjectType> projectTypes;

    @Override
    public String execute() throws Exception {
        projectTypes = projectBusiness.getProjectTypes();
        return super.execute();
    }
    
    public Collection<Project> getAll() {
        return projectBusiness.getAll();
    }

    public Collection<Project> getOngoingRankedProjects() {
        return projectBusiness.getOngoingRankedProjects();
    }

    public Collection<Project> getOngoingUnrankedProjects() {
        return projectBusiness.getOngoingUnrankedProjects();
    }

    public ProjectBusiness getProjectBusiness() {
        return projectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public String moveProjectUp() {
        projectBusiness.moveUp(projectId);
        return Action.SUCCESS;
    }

    public String moveProjectDown() {
        projectBusiness.moveDown(projectId);
        return Action.SUCCESS;
    }

    public String moveProjectTop() {
        projectBusiness.moveToTop(projectId);
        return Action.SUCCESS;
    }

    public String moveProjectBottom() {
        projectBusiness.moveToBottom(projectId);
        return Action.SUCCESS;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Collection<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    public void setProjectTypes(Collection<ProjectType> projectTypes) {
        this.projectTypes = projectTypes;
    }
    
}
