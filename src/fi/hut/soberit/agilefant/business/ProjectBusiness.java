package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;

public interface ProjectBusiness extends GenericBusiness<Project> {

    /**
     * Get a list of users, who can be assigned to a project.
     * <p>
     * The list is a composite of all enabled users and the users,
     * that are already assigned to the project.
     */
    public Collection<User> getUsersAssignableToProject(Project project);

    /**
     * Get users assigned to the project
     * @param project
     * @return
     */
    public Collection<User> getAssignedUsers(Project project);
}
