package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ListUtils;

@Service("projectBusiness")
@Transactional
public class ProjectBusinessImpl extends GenericBusinessImpl<Project> implements
        ProjectBusiness {

    private ProjectDAO projectDAO;
    private UserBusiness userBusiness;
    private AssignmentDAO assignmentDAO;

    @Autowired
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.genericDAO = projectDAO;
        this.projectDAO = projectDAO;
    }

    @Autowired
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
    
    @Autowired
    public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
    }

    /** {@inheritDoc} */
    public Project storeProject(Project project,
            Collection<Assignment> assignments) throws ObjectNotFoundException,
            IllegalArgumentException {
        // Store the project
        Project persistable = getPersistable(project);
        Project stored = persistProject(persistable);
        
        // Set the assignments
        setProjectAssignments(stored, assignments);
        
        return stored;
    }
    
    /** {@inheritDoc} */
    public void setProjectAssignments(Project project,
            Collection<Assignment> assignments) {
        // Clear all previous assignments
        for (Assignment previousAssignment : project.getAssignments()) {
            assignmentDAO.remove(previousAssignment);
        }
        if (assignments == null) {
            return;
        }
        // Set the new assignments
        for (Assignment newAssignment : assignments) {
            newAssignment.setProject(project);
            project.getAssignments().add(newAssignment);
            assignmentDAO.store(newAssignment);
        }
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
    
    /** Populates the persistent items fields with new data. 
     * @throws ObjectNotFoundException if a project with the given id was not found (id > 0)
     * @throws IllegalArgumentException TODO
     */
    private Project getPersistable(Project project)
            throws ObjectNotFoundException, IllegalArgumentException {
        Project storable;

        if (project.getId() > 0) {
            storable = this.retrieve(project.getId());

            // Validates the data
            validateProjectData(project);
            
            // Populate data
            storable.setName(project.getName());
            storable.setStartDate(project.getStartDate());
            storable.setEndDate(project.getEndDate());
            storable.setProjectType(project.getProjectType());
            storable.setDescription(project.getDescription());
            storable.setStatus(project.getStatus());
            
        } else {
            validateProjectData(project);
            storable = project;
        }
        return storable;
    }
    
    /**
     * Validates the given project's data.
     * <p>
     * Currently checks start and end date. 
     */
    private static void validateProjectData(Project project)
        throws IllegalArgumentException {
        if (project.getStartDate().after(project.getEndDate())) {
            throw new IllegalArgumentException("Project start date after end date.");
        }
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Collection<User> getUsersAssignableToProject(Project project) {
        Collection<User> assignableUsers = new ArrayList<User>();
        assignableUsers.addAll(userBusiness.getEnabledUsers());
        assignableUsers.addAll(this.getAssignedUsers(project));
        assignableUsers = ListUtils.removeDuplicates(assignableUsers);
        return assignableUsers;
    }

    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Collection<User> getAssignedUsers(Project project) {
        return projectDAO.getAssignedUsers(project);
    }

}
