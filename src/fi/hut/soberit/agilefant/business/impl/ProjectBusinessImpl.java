package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.Project;

public class ProjectBusinessImpl implements ProjectBusiness {

    private Project project;

    private ProjectDAO projectDAO;
    
    private ActivityTypeDAO activityTypeDAO;

    /** {@inheritDoc} */
    public Collection<Project> getAll() {
        return projectDAO.getAll();
    }

    /** {@inheritDoc} */
    public Collection<Project> getOngoingRankedProjects() {
        return projectDAO.getOngoingRankedProjects();
    }

    /** {@inheritDoc} */
    public Collection<Project> getOngoingUnrankedProjects() {
        return projectDAO.getOngoingUnrankedProjects();
    }

    /** {@inheritDoc} */
    public void moveDown(int projectId) {
        Project project = projectDAO.get(projectId);
        if (project != null) {
            Project upperRankedProject = projectDAO
                    .findFirstUpperRankedOngoingProject(project);
            if (upperRankedProject != null) {
                int upperRank = upperRankedProject.getRank();
                projectDAO.raiseRankBetween(upperRank + 1, null);
                project.setRank(upperRank + 1);
                projectDAO.store(project);
            }
        }
    }

    /** {@inheritDoc} */
    public void moveToBottom(int projectId) {
        Project project = projectDAO.get(projectId);
        if (project != null) {
            List result = projectDAO.findBiggestRank();
            if (result.size() != 0) {
                int lowestRank = (Integer) (result.get(0));
                if (lowestRank != project.getRank() || lowestRank == 0) {
                    project.setRank(lowestRank + 1);
                    projectDAO.store(project);
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void moveToTop(int projectId) {
        Project project = projectDAO.get(projectId);
        if (project != null && project.getRank() != 1) {
            if (project.getRank() == 0) {
                projectDAO.raiseRankBetween(1, null);
            } else {
                projectDAO.raiseRankBetween(1, project.getRank());
            }

            project.setRank(1);
            projectDAO.store(project);
        }
    }

    /** {@inheritDoc} */
    public void moveUp(int projectId) {
        Project project = projectDAO.get(projectId);
        if (project != null) {
            Project lowerRankedProject = projectDAO
                    .findFirstLowerRankedOngoingProject(project);
            if (lowerRankedProject != null) {
                int lowerRank = lowerRankedProject.getRank();
                projectDAO.raiseRankBetween(lowerRank, project.getRank());
                project.setRank(lowerRank);
                projectDAO.store(project);
            }
        }
    }

    /** {@inheritDoc} **/
    public Collection<ActivityType> getProjectTypes() {
        return activityTypeDAO.getAll();
    }
    
    /** {@inheritDoc} */
    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    /** {@inheritDoc} */
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public ActivityTypeDAO getActivityTypeDAO() {
        return activityTypeDAO;
    }

    public void setActivityTypeDAO(ActivityTypeDAO activityTypeDAO) {
        this.activityTypeDAO = activityTypeDAO;
    }

}
