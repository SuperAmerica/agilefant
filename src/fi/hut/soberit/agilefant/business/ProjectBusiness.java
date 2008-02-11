package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.Map;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ProjectPortfolioData;

/**
 * Updates projects' ranks.
 * 
 * @author Aleksi Toivonen
 * 
 */

public interface ProjectBusiness {

    /**
     * Get all projects.
     * 
     * @return all projects.
     */
    public Collection<Project> getAll();

    /**
     * Get all ongoing projects that are ranked.
     * 
     * @return
     */
    public Collection<Project> getOngoingRankedProjects();

    /**
     * Get all ongoing projects that are not ranked.
     * 
     * @return
     */
    public Collection<Project> getOngoingUnrankedProjects();

    /**
     * Move project's rank up by one "visible" rank. May jump over many
     * projects, because projects that are ranked but not ongoing are affected.
     * 
     * @param project
     */
    public void moveUp(int projectId);

    /**
     * Move project's rank down by one "visible" rank. May jump over many
     * projects, because projects that are ranked but not ongoing are affected.
     * 
     * @param project
     */
    public void moveDown(int projectId);

    /**
     * Sets project's rank to the highest of all ranked projects.
     * 
     * @param project
     */
    public void moveToTop(int projectId);

    /**
     * Sets project's rank to the lowest of all ranked projects.
     * 
     * @param project
     */
    public void moveToBottom(int projectId);

    /**
     * Clears project's rank.
     * 
     * @param project
     */
    public void unrank(int projectId);

    /**
     * Get all project types.
     * 
     * @return collection of project types
     */
    public Collection<ProjectType> getProjectTypes();

    /**
     * Delete a project type.
     * 
     * @param projectTypeId
     *                id of the project type to be deleted.
     * @throws OperationNotPermittedException
     *                 if ProjectType has WorkTypes
     * @throws ObjectNotFoundException
     *                 if no such object exists
     */
    public void deleteProjectType(int projectTypeId)
            throws OperationNotPermittedException, ObjectNotFoundException;

    /**
     * Returns all items in project.
     * @param project Project which items are returned.
     * @return Collection of items.
     */
    public Collection<BacklogItem> getBlisInProjectAndItsIterations(
            Project project);
    
    /**
     * Get a ProjectPortfolioData object that contains information for the users column of the project
     * portfolio page.
     * 
     * @return A ProjectPortfolioData object
     */
    public ProjectPortfolioData getProjectPortfolioData();

    /**
	* Constructs and returns a map containing informaton on whether a user has
	* been assigned to some backlog item under the given project without been
	* assigned to the project itself.
	*
	* @param Project
    *                project that the data is constructed for
	*
	* @return A map that maps a user to integer 1 if the user is not assigned to
	* the project but has work under it, otherwise user is mapped to integer 0
	*/
    public Map<User, Integer> getUnassignedWorkersMap(Project project);
}
