package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.ProjectTypeDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.BacklogLoadData;
import fi.hut.soberit.agilefant.util.CalendarUtils;
import fi.hut.soberit.agilefant.util.DailyWorkLoadData;
import fi.hut.soberit.agilefant.util.EffortSumData;
import fi.hut.soberit.agilefant.util.ProjectMetrics;
import fi.hut.soberit.agilefant.util.ProjectPortfolioData;
import fi.hut.soberit.agilefant.util.UserComparator;

public class ProjectBusinessImpl implements ProjectBusiness {

    Logger log = Logger.getLogger(this.getClass());

    private BacklogBusiness backlogBusiness;

    private UserBusiness userBusiness;

    private ProjectDAO projectDAO;

    private IterationDAO iterationDAO;

    private ProjectTypeDAO projectTypeDAO;
    
    private HourEntryBusiness hourEntryBusiness;
    
    private SettingBusiness settingBusiness;

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
            List<Integer> result = projectDAO.findBiggestRank();
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
    
    
    /** {@inheritDoc} * */
    public void removeAllHourEntries( Backlog backlog ){
        hourEntryBusiness.removeHourEntriesByParent(backlog);
    }

    /** {@inheritDoc} * */
    public Collection<BacklogItem> getBlisInProjectAndItsIterations(
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
        HashMap<String, String> userOverheads = new HashMap<String, String>();
        HashMap<String, String> totalUserOverheads = new HashMap<String, String>();
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
            /*ArrayList<User> assignments = new ArrayList<User>(
                    this.backlogBusiness.getUsers(pro, true));*/
            Collection<BacklogItem> blis = getBlisInProjectAndItsIterations(pro);

            // Get overheads for users in this project
            for (Assignment ass : pro.getAssignments()) {
                if (ass.getDeltaOverhead() != null) {
                    userOverheads.put(
                            pro.getId() + "-" + ass.getUser().getId(), ass
                                    .getDeltaOverhead().toString());
                    AFTime total = new AFTime(0);
                    if (pro.getDefaultOverhead() != null) {
                        total.add(pro.getDefaultOverhead());
                    }
                    total.add(ass.getDeltaOverhead());
                    totalUserOverheads.put(pro.getId() + "-"
                            + ass.getUser().getId(), total.toString());
                } else {
                    if (pro.getDefaultOverhead() != null) {
                        totalUserOverheads.put(pro.getId() + "-"
                                + ass.getUser().getId(), pro
                                .getDefaultOverhead().toString());
                    } else {
                        totalUserOverheads.put(pro.getId() + "-"
                                + ass.getUser().getId(), "");
                    }
                }
            }

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
                                AFTime t = new AFTime(bli.getEffortLeft()
                                        .getTime()
                                        / responsibles.size());
                                loadLeftData.put(pro.getId() + "-"
                                        + resp.getId(), t.toString());
                            }
                        }

                        // Check whether user is responsible for a bli in the
                        // project but is currently not assigned to it
                        if (!projectAssignments.contains(resp)
                                && bli.getEffortLeft() == null) {
                            unassignedUsersMap.put(pro.getId() + "-"
                                    + resp.getId(), 1);
                        } else if (!projectAssignments.contains(resp)
                                && bli.getEffortLeft().getTime() != 0) {
                            unassignedUsersMap.put(pro.getId() + "-"
                                    + resp.getId(), 1);
                        }
                        if (bli.getEffortLeft() == null) {
                            int numberOfUnestimatedBlis = 1;
                            if (unassignedBlisMap.get(pro.getId() + "-"
                                    + resp.getId()) != null) {
                                numberOfUnestimatedBlis = unassignedBlisMap
                                        .get(pro.getId() + "-" + resp.getId()) + 1;
                            }
                            unassignedBlisMap.put(pro.getId() + "-"
                                    + resp.getId(), numberOfUnestimatedBlis);
                        }
                    }
                }

            }
            int unassignedUsers = allUsers.size() - assignedUsers;

            String userDataString = "" + assignedUsers;
            EffortSumData loadData = new EffortSumData();
            loadData.setEffortHours(ongoingBliLoadLeft);
            loadData.setNonEstimatedItems(unestimatedBlis);
            String loadLeftString = loadData.toString();

            summaryLoadLeftMap.put(pro, loadLeftString);
            userDataMap.put(pro, userDataString);
            unassignedUserDataMap.put(pro, unassignedUsers);
            assignmentMap.put(pro, new ArrayList<User>(this.backlogBusiness
                    .getUsers(pro, true)));

        }

        for (String key : keySet) {
            String value = loadLeftData.get(key);
            // Fetch aftime-value and non-estimated items to a
            // EffortSumData-object to get correct output string.
            AFTime aftimeValue = new AFTime(0);
            if (value != null)
                aftimeValue = new AFTime(value);

            int userUnestimatedBlis = 0;
            if (unassignedBlisMap.get(key) != null)
                userUnestimatedBlis += unassignedBlisMap.get(key);

            EffortSumData sumData = new EffortSumData();
            sumData.setEffortHours(aftimeValue);
            sumData.setNonEstimatedItems(userUnestimatedBlis);

            value = sumData.toString();

            loadLeftData.put(key, value);

        }

        data.setUnassignedUsers(unassignedUsersMap);
        data.setAssignedUsers(assignmentMap);
        data.setSummaryUserData(userDataMap);
        data.setSummaryUnassignedUserData(unassignedUserDataMap);
        data.setSummaryLoadLeftData(summaryLoadLeftMap);
        data.setLoadLefts(loadLeftData);
        data.setUserOverheads(userOverheads);
        data.setTotalUserOverheads(totalUserOverheads);
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

    public List<Backlog> getProjectsAndIterationsInTimeFrame(
            List<Backlog> backlogs, Date startDate, Date endDate) {
        List<Backlog> list = new ArrayList<Backlog>(0);

        for (Backlog blog : backlogs) {
            Project pro = null;
            Iteration it = null;

            // Backlog is Project
            if (blog.getClass().equals(Project.class)) {
                pro = (Project) blog;
                if ((pro.getEndDate().after(startDate) && pro.getEndDate()
                        .before(endDate))
                        || (pro.getStartDate().after(startDate) && pro
                                .getStartDate().before(endDate))
                        || (pro.getStartDate().before(startDate) && pro
                                .getEndDate().after(endDate))
                        || (pro.getStartDate().compareTo(startDate) == 0)
                        || (pro.getEndDate().compareTo(endDate) == 0)) {
                    list.add(blog);
                    log.debug("IN timeframe, project");
                } else {
                    log.debug("NOT in timeframe");
                }
            }

            // Backlog is Iteration
            if (blog.getClass().equals(Iteration.class)) {
                it = (Iteration) blog;
                if ((it.getEndDate().after(startDate) && it.getEndDate()
                        .before(endDate))
                        || (it.getStartDate().after(startDate) && it
                                .getStartDate().before(endDate))
                        || (it.getStartDate().before(startDate) && it
                                .getEndDate().after(endDate))
                        || (it.getStartDate().compareTo(startDate) == 0)
                        || (it.getEndDate().compareTo(endDate) == 0)) {
                    list.add(blog);
                    log.debug("IN timeframe, iteration");
                } else {
                    log.debug("NOT in timeframe");
                }
            }
        }
        return list;
    }

    public HashMap<Integer, String> calculateEffortLefts(Date from,
            int weeksAhead, Map<Backlog, List<BacklogItem>> items) {
        GregorianCalendar cal = new GregorianCalendar();
        CalendarUtils cUtils = new CalendarUtils();
        HashMap<Integer, String> effortLefts = new HashMap<Integer, String>();

        Date start = from;
        Date end = cUtils.nextMonday(start);
        cal.setTime(start);
        Integer week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        for (int i = 1; i <= weeksAhead; i++) {
            // 1. Get Backlogs that hit for the week
            log.debug("Projects searched from :" + start);
            log.debug("Projects searched ending :" + end);
            cal.setTime(start);
            week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
            log.debug("Calculating sums for week" + week);

            // 2. Get projects that hit current week
            List<Backlog> list = this.getProjectsAndIterationsInTimeFrame(
                    new ArrayList<Backlog>(items.keySet()), start, end);
            log.debug(list.size() + " projects found for given time frame");

            // 3. Calculate effort sum from items in those projects
            AFTime total = new AFTime(0);
            for (Backlog blog : list) {
                Project pro = null;
                Iteration it = null;
                if (blog.getClass().equals(Project.class)) {
                    pro = (Project) blog;
                    List<BacklogItem> blis = items.get((Backlog) pro);
                    if (blis != null) {
                        // Dividing for weeks that project hits
                        AFTime sum = this.backlogBusiness
                                .getEffortLeftResponsibleDividedSum(blis)
                                .getEffortHours();
                        int projectLength = CalendarUtils.getLengthInDays(pro
                                .getStartDate(), pro.getEndDate());
                        log.debug("Week Project length: " + projectLength
                                + " days");
                        int weekEndDaysInProject = cUtils.getWeekEndDays(pro
                                .getStartDate(), pro.getEndDate());
                        log.debug("Excluding " + weekEndDaysInProject
                                + " days from project as week end days");
                        projectLength = projectLength - weekEndDaysInProject;
                        if (projectLength == 0) { // TODO Find better way to
                                                    // prevent null divination
                                                    // if project on weekend
                            projectLength = 1;
                        }
                        List<Date> dates = cUtils.getProjectDaysList(pro
                                .getStartDate(), pro.getEndDate(), start,
                                new Date(end.getTime() - 86400000L), false);
                        int projectDaysOnWeek = 0;
                        if (dates != null) {
                            projectDaysOnWeek = dates.size();
                        }
                        log.debug("Week Project length (modified): "
                                + projectLength + " days");
                        log.debug("Week Project days:" + projectDaysOnWeek);
                        log.debug("Week Project effort per day: "
                                + new AFTime(sum.getTime()
                                        / (long) projectLength));
                        sum = new AFTime((sum.getTime() / (long) projectLength)
                                * projectDaysOnWeek);
                        if (sum != null) {
                            total.add(sum);
                            log.debug("Week effort sum: " + sum);
                        }
                    }
                }
                if (blog.getClass().equals(Iteration.class)) {
                    it = (Iteration) blog;
                    List<BacklogItem> blis = items.get((Backlog) it);
                    if (blis != null) {
                        // Dividing for weeks that project hits
                        AFTime sum = this.backlogBusiness
                                .getEffortLeftResponsibleDividedSum(blis)
                                .getEffortHours();
                        int projectLength = CalendarUtils.getLengthInDays(it
                                .getStartDate(), it.getEndDate());
                        log.debug("Week Project length: " + projectLength
                                + " days");
                        int weekEndDaysInProject = cUtils.getWeekEndDays(it
                                .getStartDate(), it.getEndDate());
                        log.debug("Excluding " + weekEndDaysInProject
                                + " days from project as week end days");
                        projectLength = projectLength - weekEndDaysInProject;
                        if (projectLength == 0) { // TODO Find better way to
                                                    // prevent null division if
                                                    // project on weekend
                            projectLength = 1;
                        }
                        List<Date> dates = cUtils.getProjectDaysList(it
                                .getStartDate(), it.getEndDate(), start,
                                new Date(end.getTime() - 86400000L), false);
                        int projectDaysOnWeek = 0;
                        if (dates != null) {
                            projectDaysOnWeek = dates.size();
                        }
                        log.debug("Week Project length(modified): "
                                + projectLength + " days");
                        log.debug("Week Project days:" + projectDaysOnWeek);
                        log.debug("Week Project effort per day: "
                                + new AFTime(sum.getTime()
                                        / (long) projectLength));
                        sum = new AFTime((sum.getTime() / (long) projectLength)
                                * projectDaysOnWeek);
                        if (sum != null) {
                            total.add(sum);
                            log.debug("Week effort sum: " + sum);
                        }
                    }
                }

            }

            effortLefts.put(week, total.toString());
            start = cUtils.nextMonday(start);
            end = cUtils.nextMonday(start);
            cal.setTime(start);
            week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        }

        return effortLefts;
    }

    /**
     * Calculates overheads for user from given backlogs( projects)
     * 
     * @param from
     * @param weeksAhead
     * @param items
     * @return
     */
    public HashMap<Integer, String> calculateOverheads(Date from,
            int weeksAhead, List<Backlog> items, User user) {
        GregorianCalendar cal = new GregorianCalendar();
        CalendarUtils cUtils = new CalendarUtils();
        HashMap<Integer, String> overheads = new HashMap<Integer, String>();

        Date start = from;
        Date end = cUtils.nextMonday(start);
        cal.setTime(start);
        Integer week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        List<Assignment> assignments = new ArrayList<Assignment>(user
                .getAssignments());

        for (int i = 1; i <= weeksAhead; i++) {
            // 1. Get Backlogs that hit for the week
            log.debug("Projects searched from :" + start);
            log.debug("Projects searched ending :" + end);
            cal.setTime(start);
            week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
            log.debug("Calculating overhead for week" + week);

            // 2. Get projects that hit current week
            List<Backlog> list = this.getProjectsAndIterationsInTimeFrame(
                    items, start, end);
            log.debug(list.size() + " projects found for given time frame");

            // 3. Calculate overhead sum from items in those projects
            AFTime overhead = new AFTime(0);
            for (Backlog blog : list) {
                // Only check assignments for Projects (overhead
                // only set for projects not iterations)
                if (blog.getClass().equals(Project.class)) {
                    Project pro = (Project) blog;
                    for (Assignment ass : assignments) {
                        if (ass.getBacklog().equals((Backlog) pro)) {
                            if (pro.getDefaultOverhead() != null) {
                                overhead.add(pro.getDefaultOverhead());
                                log.debug("Added overhead from project: "
                                        + pro.getDefaultOverhead());
                            }
                            if (ass.getDeltaOverhead() != null) {
                                overhead.add(ass.getDeltaOverhead());
                                log.debug("Added overhead from user: "
                                        + pro.getDefaultOverhead());
                            }
                        }
                    }
                } else {
                    log.debug("Class was iteration class, overhead :"
                            + blog.getClass());
                }
            }
            overheads.put(week, overhead.toString());
            start = cUtils.nextMonday(start);
            end = cUtils.nextMonday(start);
            cal.setTime(start);
            week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        }

        return overheads;
    }

    /**
     * Calculates Efforts and overheads data for users
     * 
     * EffortLefts are calculated for every week by summings efforts from blis
     * assigned to user from projects that start/end date hit in particular
     * week.
     * 
     * For example: Weeks 1 2 3 4 Eff 20 20 10 10 projects 2 2 1 1 (10h blis
     * each)
     * 
     * So efforts are just sumassignmentDAO.getAssignment(user,
     * backlog).getDeltaOverhead();med together and NOT divided for length of
     * the project or weeks.
     */
    public DailyWorkLoadData getDailyWorkLoadData(User user, int weeksAhead) {
        DailyWorkLoadData data = new DailyWorkLoadData();
        Map<Backlog, BacklogLoadData> loadDataList = new HashMap<Backlog, BacklogLoadData>();
        ArrayList<Integer> weekNumbers = new ArrayList<Integer>();
        // String[] overallTotals = new String[3]; // Not currently used, but could
                                                // use in the future

        GregorianCalendar cal = new GregorianCalendar();
        cal.setFirstDayOfWeek(GregorianCalendar.MONDAY);

        int currentWeek = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        
        // 1. Weeks
        for (int i = 0; i < weeksAhead; i++) {
            weekNumbers.add(currentWeek + i);
            if (!data.getWeeklyTotals().containsKey(currentWeek + i)) {
                data.getWeeklyTotals().put(new Integer(currentWeek + i), new AFTime(0));
                data.getWeeklyEfforts().put(new Integer(currentWeek + i), new AFTime(0));
                data.getWeeklyOverheads().put(new Integer(currentWeek + i), new AFTime(0));
            }
        }

        // 2. Backlog loads
        List<Backlog> assignedBacklogs = this.backlogBusiness.getUserBacklogs(
                user, new Date(), weeksAhead);
        
        data.setBacklogs(assignedBacklogs);
        
        // Loop through the backlogs
        for (Backlog blog : assignedBacklogs) {
            BacklogLoadData bdata = this.backlogBusiness
                    .calculateBacklogLoadData(blog, user, new Date(),
                            weeksAhead);
            loadDataList.put(bdata.getBacklog(), bdata);
            
            // Calculate totals
            for (Integer weekNo : weekNumbers) {
                AFTime effAdd = bdata.getEfforts().get(weekNo);
                if (effAdd != null) {
                    data.getWeeklyEfforts().get(weekNo).add(effAdd);
                    data.getTotalEffort().add(effAdd);    
                }
                
                
                AFTime ohAdd = bdata.getOverheads().get(weekNo);
                if (ohAdd != null) {
                    data.getWeeklyOverheads().get(weekNo).add(ohAdd);
                    data.getTotalOverhead().add(ohAdd);    
                }
                
                
                AFTime toAdd = bdata.getWeeklyTotals().get(weekNo);
                if (toAdd != null) {
                    data.getWeeklyTotals().get(weekNo).add(toAdd);
                    data.getOverallTotal().add(toAdd);                    
                }
            }
        }
        for (int i = 0; i < weeksAhead; i++) {
            boolean accommondate = isAccommodableWorkload(currentWeek, currentWeek + i, 
                    data.getWeeklyTotals().get(currentWeek + i), user);
            data.getWeeklyOverload().put(new Integer(currentWeek + i), new Boolean(accommondate));
        }
        data.setWeekNumbers(weekNumbers);
        data.setLoadDatas(loadDataList);

        return data;
    }

    /**
     * Calculate whether given hours can be fitted to the given week.
     * Each day is assumed 8 hours long.
     * @param currentWeek
     * @param targetWeek
     * @param totalWorkload
     * @return
     */
    private boolean isAccommodableWorkload(int currentWeek, int targetWeek, AFTime totalWorkload, User user) {
        if (user == null) {
            return false;
        }
        long totalInWeek = 5;
        int daysLeft = 5;
        if(currentWeek == targetWeek) {
            Calendar cal = GregorianCalendar.getInstance();
            daysLeft = 1;
            while(cal.get(Calendar.DAY_OF_WEEK) != Calendar.FRIDAY && daysLeft < 6) {
                cal.add(Calendar.DAY_OF_YEAR, 1);
                daysLeft++;
            }
        }
        totalInWeek = (long) (user.getWeekHours().getTime() * (1.0 * daysLeft * settingBusiness.getCriticalLow() / (5 * 100)));
        if (totalInWeek <  totalWorkload.getTime()) {
            return false;
        }
        return true;
    }
    
    /** {@inheritDoc} */
    public List<User> getAssignableUsers(Project project) {
        Set<User> userSet = new HashSet<User>(); 
        
        // Add all assigned users
        userSet.addAll(backlogBusiness.getUsers(project, true));
        
        // Add all enabled users
        userSet.addAll(userBusiness.getEnabledUsers());
        
        // Add the users to a list
        List<User> userList = new ArrayList<User>(userSet);
        
        // Sort the list
        Collections.sort(userList, new UserComparator());
        
        return userList;
    }
    
    public void calculateProjectMetrics(Product product) {
        if (product != null && product.getProjects() != null && product.getProjects().size() > 0) {
            //ProjectData projectDataMap
            for (Project p : product.getProjects()) {
                ProjectMetrics metrics = new ProjectMetrics();
                metrics.setAssignees(backlogBusiness.getNumberOfAssignedUsers(p));
                if (p.getIterations() != null) {
                    metrics.setNumberOfAllIterations(p.getIterations().size());
                    int ongoingIters = 0;
                    Date current = Calendar.getInstance().getTime();
                    for (Iteration iter: p.getIterations()) {
                        if (iter.getStartDate().getTime() < current.getTime() && iter.getEndDate().getTime() > current.getTime()) {
                            ongoingIters++;
                        }
                    }
                    metrics.setNumberOfOngoingIterations(ongoingIters);
                } else {
                    metrics.setNumberOfAllIterations(0);
                    metrics.setNumberOfOngoingIterations(0);
                }                
                p.setMetrics(metrics);
            }
        }
    }
    
    public Map<Integer, AFTime> calculateTotalOverheads(Project project) {
        Map<Integer, AFTime> totalOverheads = new HashMap<Integer, AFTime>();
        if (project != null) {                   
            for (Assignment ass : project.getAssignments()) {
                AFTime totalOverhead = new AFTime(0);
                if (project.getDefaultOverhead() != null) {
                    totalOverhead.add(project.getDefaultOverhead());
                }
                if (ass.getDeltaOverhead() != null) {
                    totalOverhead.add(ass.getDeltaOverhead());
                }
                totalOverheads.put(ass.getUser().getId(), totalOverhead);
            }            
        }
        return totalOverheads;
    }
    
    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }

    public SettingBusiness getSettingBusiness() {
        return settingBusiness;
    }

}
