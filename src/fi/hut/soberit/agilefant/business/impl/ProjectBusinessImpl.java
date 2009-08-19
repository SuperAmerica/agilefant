package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;

@Service("projectBusiness")
@Transactional
public class ProjectBusinessImpl extends GenericBusinessImpl<Project> implements
        ProjectBusiness {

    private ProjectDAO projectDAO;
    private BacklogDAO backlogDAO;
    private ProductBusiness productBusiness;

    @Autowired
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.genericDAO = projectDAO;
        this.projectDAO = projectDAO;
    }

    @Autowired
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }    
    
    @Autowired
    public void setProductBusiness(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public ProjectMetrics getProjectMetrics(Project project) {
        if (project == null) {
            throw new IllegalArgumentException("Project must be supplied");
        }
        ProjectMetrics metrics = new ProjectMetrics();
        metrics.setStoryPoints(
                backlogDAO.calculateStoryPointSumIncludeChildBacklogs(project.getId()));
        return metrics;
    }
    
    /** {@inheritDoc} */
    public Project store(int projectId,
            Integer productId, Project project) throws ObjectNotFoundException,
            IllegalArgumentException {

        Project persistable = new Project();
        if (projectId > 0) {
            persistable = this.retrieve(projectId);       
        } 
        validateProjectData(project, projectId, productId);
        if(productId != null ){
            Product product = this.productBusiness.retrieve(productId);
            persistable.setParent(product);
        }
        
        persistable.setName(project.getName());
        persistable.setStartDate(project.getStartDate());
        persistable.setEndDate(project.getEndDate());
        persistable.setProjectType(project.getProjectType());
        persistable.setDescription(project.getDescription());
        persistable.setStatus(project.getStatus());
        persistable.setBacklogSize(project.getBacklogSize());
        persistable.setParent(project.getParent());
        
        Project stored = persistProject(persistable);
        
        return stored;
    }
    
    /**
     * Persists a given project.
     * <p>
     * Decides whether to use <code>store</code> or <code>create</code>.
     * @return the persisted project
     */
    private Project persistProject(Project project) {
        if (project.getId() > 0) {
            this.store(project);
            return project;
        }
        else {
            int newId = this.create(project);
            return this.retrieve(newId);
        }
    }
    
    /**
     * Validates the given project's data.
     * <p>
     * Currently checks start and end date. 
     */
    private static void validateProjectData(Project project, int projectId, Integer productId)
        throws IllegalArgumentException {
        if (project.getStartDate().after(project.getEndDate())) {
            throw new IllegalArgumentException("Project start date after end date.");
        }
        if(projectId == 0 && productId == null) {
            throw new IllegalArgumentException("New project must have a parent product");
        }
    }


    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Collection<User> getAssignedUsers(Project project) {
        return projectDAO.getAssignedUsers(project);
    }



}
