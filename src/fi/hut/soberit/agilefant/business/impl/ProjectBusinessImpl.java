package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ProjectPortfolioData;

public class ProjectBusinessImpl implements ProjectBusiness {

    private BacklogBusiness backlogBusiness;

    private ProjectDAO projectDAO;

    private IterationDAO iterationDAO;

    private ProjectTypeDAO projectTypeDAO;

    // Testing
    private UserDAO userDAO;

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

    /** {@inheritDoc} * */
    public void unrank(int projectId) {
        Project project = projectDAO.get(projectId);
        if (project != null) {
            project.setRank(0);
            projectDAO.store(project);
        }
    }

    /** {@inheritDoc} * */
    public void deleteProjectType(int projectTypeId)
            throws OperationNotPermittedException, ObjectNotFoundException {

        ProjectType projectType = projectTypeDAO.get(projectTypeId);

        if (projectType == null) {
            throw new ObjectNotFoundException();
        }

        if (!projectType.getWorkTypes().isEmpty()) {
            throw new OperationNotPermittedException(
                    "Can't delete: project type has work types.");
        }

        projectTypeDAO.remove(projectTypeId);
    }

    private Collection<BacklogItem> getBlisInProjectAndItsIterations(
            Project project) {
        Collection<BacklogItem> blis = new HashSet<BacklogItem>();
        blis.addAll(project.getBacklogItems());
        Collection<Iteration> iterations = iterationDAO.getAll();
        for (Iteration it : iterations) {
            if (it.getProject().getId() == project.getId()) {
                blis.addAll(it.getBacklogItems());
            }
        }
        return blis;
    }

    private void fillProjectPortfolioData(ProjectPortfolioData data) {
        HashMap<Project, String> userDataMap = new HashMap<Project, String>();
        HashMap<Project, Integer> unassignedUserDataMap = new HashMap<Project, Integer>();
        HashMap<Project, String> summaryLoadLeftMap = new HashMap<Project, String>();
        HashMap<String, String> loadLeftData = new HashMap<String, String>();
        HashMap<String, Integer> unassignedUsersMap = new HashMap<String, Integer>();
        Map<Project, List<User>> assignmentMap = new HashMap<Project, List<User>>(
                0);
        Set<String> keySet = new HashSet<String>();
        
        Map<String, Integer> unassignedBlisMap = new HashMap<String, Integer>();
        
        Collection<Project> projects = projectDAO.getOngoingProjects();

        // Go trough all projects and bli:s
        for (Project pro : projects) {
            int assignedUsers = backlogBusiness.getNumberOfAssignedUsers(pro);
            int unestimatedBlis = 0;
            AFTime ongoingBliLoadLeft = new AFTime(0);
            Set<User> allUsers = new HashSet<User>(this.backlogBusiness
                    .getUsers(pro, true));
            HashSet<User> projectAssignments = new HashSet<User>(
                    this.backlogBusiness.getUsers(pro, true));

            Collection<BacklogItem> blis = getBlisInProjectAndItsIterations(pro);

            for (BacklogItem bli : blis) {
                if (bli.getResponsibles() != null) {
                    ArrayList<User> responsibles = new ArrayList<User>(bli
                            .getResponsibles());

                    if (bli.getEffortLeft() == null) {
                        unestimatedBlis++;
                        allUsers.addAll(bli.getResponsibles());
                    } else if (bli.getEffortLeft().getTime() != 0) {
                        ongoingBliLoadLeft.add(bli.getEffortLeft());
                        allUsers.addAll(bli.getResponsibles());
                    }

                    for (User resp : responsibles) {
                        
                        keySet.add(pro.getId() + "-" + resp.getId());
                        
                        // Calculate and add effort from bli to user(s) assigned
                        // Uses projectID-UserId as map key
                        String effortForUsr = loadLeftData.get(pro.getId()
                                + "-" + resp.getId());
                        if (effortForUsr != null) {
                            AFTime usrLoadLeft = new AFTime(effortForUsr);
                            if (bli.getEffortLeft() != null) {
                                // Add effort to this user: (bli effort / number
                                // of people assigned)
                                AFTime newEffort = new AFTime(bli
                                        .getEffortLeft().getTime()
                                        / responsibles.size());
                                usrLoadLeft.add(newEffort);
                                loadLeftData.put(pro.getId() + "-"
                                        + resp.getId(), usrLoadLeft.toString());
                            }
                        } else { // no effort for user, create one
                            if (bli.getEffortLeft() != null) {
                                AFTime t = new AFTime(bli.getEffortLeft().getTime() / responsibles.size());
                                loadLeftData.put(pro.getId() + "-"
                                        + resp.getId(), t.toString());
                            }
                            
                            
                        }
                        // Check whether user is responsible for a bli in the
                        // project
                        // but is currently not assigned to it
                        if (!projectAssignments.contains(resp) && bli.getEffortLeft() == null) {
                            unassignedUsersMap.put(pro.getId() + "-"
                                    + resp.getId(), 1);
                        }
                        else if (!projectAssignments.contains(resp) && bli.getEffortLeft().getTime() != 0) {
                            unassignedUsersMap.put(pro.getId() + "-"
                                    + resp.getId(), 1);
                        }
                        if (bli.getEffortLeft() == null) {
                            int numberOfUnestimatedBlis = 1;
                            if (unassignedBlisMap.get(pro.getId() + "-" + resp.getId()) != null) {
                                numberOfUnestimatedBlis = unassignedBlisMap.get(pro.getId() + "-"
                                        + resp.getId()) + 1;
                            }
                            unassignedBlisMap.put(pro.getId() + "-" + resp.getId(), numberOfUnestimatedBlis);
                        }
                    }
                }

            }
            int unassignedUsers = allUsers.size() - assignedUsers;

            String userDataString = "" + assignedUsers;
            String loadLeftString = "" + ongoingBliLoadLeft;
            if (unestimatedBlis == 1)
                loadLeftString += " + " + unestimatedBlis
                + " non-estimated BLI";
            else if (unestimatedBlis > 0)
                loadLeftString += " + " + unestimatedBlis
                        + " non-estimated BLIs";
            summaryLoadLeftMap.put(pro, loadLeftString);
            userDataMap.put(pro, userDataString);
            unassignedUserDataMap.put(pro, unassignedUsers);
            assignmentMap.put(pro, new ArrayList<User>(this.backlogBusiness
                    .getUsers(pro, true)));

        }
        
        for (String key : keySet) {
            String value = loadLeftData.get(key);
            String appendValue = "";
            
            int userUnestimatedBlis = 0;
            if (unassignedBlisMap.get(key) != null)
                userUnestimatedBlis += unassignedBlisMap.get(key);
            
            if (userUnestimatedBlis > 0) {
                if (value != null)
                    appendValue += " + ";
                if (userUnestimatedBlis == 1)
                    appendValue += userUnestimatedBlis + " non-estimated BLI";
                else
                    appendValue += userUnestimatedBlis + " non-estimated BLIs";
            }
            
            if (value == null)
                value = "";
            value += appendValue;
            
            if (value != null)
                loadLeftData.put(key, value);
        }
        
        data.setUnassignedUsers(unassignedUsersMap);
        data.setAssignedUsers(assignmentMap);
        data.setSummaryUserData(userDataMap);
        data.setSummaryUnassignedUserData(unassignedUserDataMap);
        data.setSummaryLoadLeftData(summaryLoadLeftMap);
        data.setLoadLefts(loadLeftData);
    }

    public ProjectPortfolioData getProjectPortfolioData() {
        ProjectPortfolioData data = new ProjectPortfolioData();
        fillProjectPortfolioData(data);
        return data;
    }

    public Map<User, Integer> getUnassignedWorkersMap(Project project) {
        Map<User, Integer> unassignedHasWork = new HashMap<User, Integer>();
        Collection<BacklogItem> blis = getBlisInProjectAndItsIterations(project);
        Collection<User> assignees = backlogBusiness.getUsers(project, true);
        Set<User> workers = new HashSet<User>();
        for (BacklogItem bli : blis)
            workers.addAll(bli.getResponsibles());
        for (User worker : workers)
            unassignedHasWork.put(worker, assignees.contains(worker) ? 0 : 1);
        return unassignedHasWork;
    }

    /** {@inheritDoc} * */
    public Collection<ProjectType> getProjectTypes() {
        return projectTypeDAO.getAll();
    }

    /** {@inheritDoc} */
    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    /** {@inheritDoc} */
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public ProjectTypeDAO getProjectTypeDAO() {
        return projectTypeDAO;
    }

    public void setProjectTypeDAO(ProjectTypeDAO projectTypeDAO) {
        this.projectTypeDAO = projectTypeDAO;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

}
