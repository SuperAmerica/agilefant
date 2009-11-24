package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ProjectMetrics;
import fi.hut.soberit.agilefant.transfer.ProjectTO;

public interface ProjectBusiness extends GenericBusiness<Project> {


    /**
     * Get users assigned to the project.
     */
    public Collection<User> getAssignedUsers(Project project);
    
    public Project store(int projectId, Integer productId, Project project, Set<Integer> assigneeIds) throws ObjectNotFoundException,
            IllegalArgumentException;

    public ProjectMetrics getProjectMetrics(Project project);
    
    public ProjectTO getProjectData(int projectId);
    
    public List<Story> retrievetRootStories(Project project);

    Project rankUnderProject(Project project, Project upperProject)
            throws IllegalArgumentException;

    Project rankUnderProject(int projectId, int rankUnderId);
    
    public void unrankProject(int projectId);
    
    public void moveToRanked(int projectId);

    Project rankOverProject(int projectId, int rankOverId);
}
