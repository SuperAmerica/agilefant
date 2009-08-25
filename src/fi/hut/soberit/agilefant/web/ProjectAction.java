package fi.hut.soberit.agilefant.web;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;

@Component("projectAction")
@Scope("prototype")
public class ProjectAction implements CRUDAction, Prefetching {
    
    private static final long serialVersionUID = -4636900464606739866L;

    @PrefetchId
    private int projectId;
    
    private Integer productId = null;

    private Project project;
    
    private ProjectMetrics projectMetrics;

    @Autowired
    private ProjectBusiness projectBusiness;
    
    
    public String projectMetrics() {
        project = projectBusiness.retrieve(projectId);
        projectMetrics = projectBusiness.getProjectMetrics(project);
        return Action.SUCCESS;
    }
    
    public String projectData() {
        project = projectBusiness.getProjectData(projectId);
        return Action.SUCCESS;
    }
    
    public String create() {
        project = new Project();
        project.setStartDate(new DateTime());
        project.setEndDate(new DateTime());
        return Action.SUCCESS;
    }

    public String retrieve() {
        project = this.projectBusiness.retrieve(projectId);
        return Action.SUCCESS;
    }
    
    public String store() {
       project = this.projectBusiness.store(projectId, productId, project);
       return Action.SUCCESS;
    }
    
    public String delete() {
        this.projectBusiness.delete(projectId);
        return Action.SUCCESS;
    }

    public void initializePrefetchedData(int objectId) {
        this.project = this.projectBusiness.retrieve(objectId);        
    }
    
    //GETTERS AND SETTERS
    
    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
	
    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public ProjectMetrics getProjectMetrics() {
        return projectMetrics;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }
}