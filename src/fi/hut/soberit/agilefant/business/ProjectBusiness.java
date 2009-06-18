package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ProjectDataContainer;

public interface ProjectBusiness extends GenericBusiness<Project> {

    /**
     * Get a list of users, who can be assigned to a project.
     * <p>
     * The list is a composite of all enabled users and the users, that are
     * already assigned to the project.
     */
    public Collection<User> getUsersAssignableToProject(Project project);

    /**
     * Get users assigned to the project.
     */
    public Collection<User> getAssignedUsers(Project project);
    
    /**
     * Populates a persistable object and stores it.
     * 
     * @param assignments
     *            a list of project assignments
     * @throws ObjectNotFoundException
     *             if the project was not new (id > 0) and was not found
     * @throws IllegalArgumentException
     *             if a field is not properly set.
     */
    public Project storeProject(Project project,
            Collection<Assignment> assignments) throws ObjectNotFoundException,
            IllegalArgumentException;

    /**
     * Clears and populates a projects' assignments.
     * <p>
     * Will set the project of the assignments to the given project.
     */
    public void setProjectAssignments(Project project, Collection<Assignment> assignments);
    
    /**
     * Get the projects contents as Transfer Objects.
     */
    public ProjectDataContainer getProjectContents(int projectId);
}
