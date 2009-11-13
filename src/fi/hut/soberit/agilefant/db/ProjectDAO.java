package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;

/**
 * Interface for a DAO of a Project.
 * 
 * @see GenericDAO
 */
public interface ProjectDAO extends GenericDAO<Project> {

    /**
     * Gets a collection of users assigned to the project.
     */
    public Collection<User> getAssignedUsers(Project project);
    
    public Collection<Project> getProjectsWithUserAssigned(User user);
    
    /**
     * Gets all active projects (endDate > today)
     * @return projects
     */
    public List<Project> getActiveProjectsSortedByRank();

}
