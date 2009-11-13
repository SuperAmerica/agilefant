package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.Period;

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
    
    /**
     * Gets a collection of unranked projects
     * @return projects
     */
    public Collection<Project> getUnrankedProjects();
    
    /**
     * Gets a list of ranked projects
     * @param startDate start date
     * @param endDate end date
     * @return projects
     */
    public List<Project> getRankedProjects(LocalDate startDate, LocalDate endDate);

    Collection<Project> getProjectsWithRankBetween(int lower, int upper);

}
