package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ListUtils;

@Service("projectBusiness")
@Transactional
public class ProjectBusinessImpl extends GenericBusinessImpl<Project> implements
        ProjectBusiness {

    private ProjectDAO projectDAO;
    private UserBusiness userBusiness;

    @Autowired
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.genericDAO = projectDAO;
        this.projectDAO = projectDAO;
    }

    @Autowired
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
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
