package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
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
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.EffortSumData;
import fi.hut.soberit.agilefant.util.CalendarUtils;
import fi.hut.soberit.agilefant.util.DailyWorkLoadData;
import fi.hut.soberit.agilefant.util.ProjectPortfolioData;

public class ProjectBusinessImpl implements ProjectBusiness {

    Logger log = Logger.getLogger(this.getClass());
    
    private BacklogBusiness backlogBusiness;
    
    private UserBusiness userBusiness;

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
            ArrayList<User> assignments = new ArrayList<User>(
                    this.backlogBusiness.getUsers(pro, true));
            Collection<BacklogItem> blis = getBlisInProjectAndItsIterations(pro);

            // Get overheads for users in this project
            for(Assignment ass : pro.getAssignments()){   
                if(ass.getDeltaOverhead() != null){
                    userOverheads.put(pro.getId()+"-"+ass.getUser().getId(), ass.getDeltaOverhead().toString());
                    AFTime total = new AFTime(0);
                    if(pro.getDefaultOverhead() != null) {
                        total.add(pro.getDefaultOverhead());
                    }
                    total.add(ass.getDeltaOverhead());
                    totalUserOverheads.put(pro.getId()+"-"+ass.getUser().getId(), total.toString());
                }else{
                    if(pro.getDefaultOverhead() != null){
                        totalUserOverheads.put(pro.getId()+"-"+ass.getUser().getId(), pro.getDefaultOverhead().toString());
                    }else{
                        totalUserOverheads.put(pro.getId()+"-"+ass.getUser().getId(), "");
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
                                AFTime t = new AFTime(bli.getEffortLeft().getTime() / responsibles.size());
                                loadLeftData.put(pro.getId() + "-"
                                        + resp.getId(), t.toString());
                            }
                        }
                        

                        
                        // Check whether user is responsible for a bli in the
                        // project but is currently not assigned to it
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

    public List<Backlog> getProjectsAndIterationsInTimeFrame(List<Backlog> backlogs, 
                        Date startDate, Date endDate){
        List<Backlog> list = new ArrayList<Backlog>(0);
        
        for(Backlog blog : backlogs){            
            Project pro = null;
            Iteration it = null;
            
            // Backlog is Project
            if(blog.getClass().equals(Project.class)){
                pro = (Project) blog;
                if((pro.getEndDate().after(startDate) &&
                        pro.getEndDate().before(endDate) )||(
                        pro.getStartDate().after(startDate) &&
                        pro.getStartDate().before(endDate) ) ||
                        pro.getStartDate().before(startDate) &&
                        pro.getEndDate().after(endDate)){
                     list.add(blog);
                     log.debug("IN timeframe, project");
                }else{
                    log.debug("NOT in timeframe");
                }
            }
            
            // Backlog is Iteration
            if(blog.getClass().equals(Iteration.class)){
                it = (Iteration) blog;
                if((it.getEndDate().after(startDate) &&
                        it.getEndDate().before(endDate) )||(
                        it.getStartDate().after(startDate) &&
                        it.getStartDate().before(endDate) ) ||
                        it.getStartDate().before(startDate) &&
                        it.getEndDate().after(endDate)){
                    list.add(blog);
                    log.debug("IN timeframe, iteration");
                }else{
                    log.debug("NOT in timeframe");
                }
            }
           }        
        return list;
    }
    
    public HashMap<Integer, String> calculateEffortLefts(Date from, int weeksAhead, Map<Backlog, List<BacklogItem>> items){
        GregorianCalendar cal = new GregorianCalendar();
        CalendarUtils cUtils = new CalendarUtils();
        HashMap<Integer, String> effortLefts = new HashMap<Integer, String>();
        
        Date start = cUtils.nextMonday(from);        
        Date end = cUtils.nextMonday(start);
        cal.setTime(start);
        Integer week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        for(int i=1;i<=weeksAhead;i++){
            // 1. Get Backlogs that hit for the week            
            log.debug("Projects searched from :"+start);
            log.debug("Projects searched ending :"+end);
            cal.setTime(start);
            week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
            log.debug("Calculating sums for week"+week);
            
            // 2. Get projects that hit current week
            List<Backlog> list = this.getProjectsAndIterationsInTimeFrame(new ArrayList<Backlog>(items.keySet()), start, end);
            log.debug(list.size()+" projects found for given time frame");
            
            // 3. Calculate effort sum from items in those projects
            AFTime total = new AFTime(0);
            for(Backlog blog : list){
                Project pro = null;
                Iteration it = null;
                if(blog.getClass().equals(Project.class)){
                    pro = (Project)blog;     
                }
                if(blog.getClass().equals(Iteration.class)){
                    it = (Iteration)blog;     
                }
                
                List<BacklogItem> blis = items.get((Backlog)pro);
                if(blis != null){
                    AFTime sum = this.backlogBusiness.getEffortLeftSum(blis).getEffortHours();
                    if(sum != null){
                        total.add(sum);
                        log.debug("Adding: "+sum);
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
     * @param from
     * @param weeksAhead
     * @param items
     * @return
     */
    public HashMap<Integer, String> calculateOverheads(Date from, int weeksAhead, List<Backlog> items, User user){
        GregorianCalendar cal = new GregorianCalendar();
        CalendarUtils cUtils = new CalendarUtils();
        HashMap<Integer, String> overheads = new HashMap<Integer, String>();
        
        Date start = cUtils.nextMonday(from);        
        Date end = cUtils.nextMonday(start);
        cal.setTime(start);
        Integer week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
        List<Assignment> assignments = new ArrayList<Assignment>(user.getAssignments());
        
        for(int i=1;i<=weeksAhead;i++){
            // 1. Get Backlogs that hit for the week            
            log.debug("Projects searched from :"+start);
            log.debug("Projects searched ending :"+end);
            cal.setTime(start);
            week = cal.get(GregorianCalendar.WEEK_OF_YEAR);
            log.debug("Calculating overhead for week"+week);
            
            // 2. Get projects that hit current week
            List<Backlog> list = this.getProjectsAndIterationsInTimeFrame(items, start, end);
            log.debug(list.size()+" projects found for given time frame");
            
            // 3. Calculate overhead sum from items in those projects
            AFTime overhead = new AFTime(0);            
            for(Backlog blog : list){
                // Only check assignments for Projects (overhead 
                // only set for projects not iterations)
                if(blog.getClass().equals(Project.class)){
                    Project pro = (Project) blog;
                    for(Assignment ass : assignments){
                        if(ass.getBacklog().equals((Backlog)pro)){
                            if(pro.getDefaultOverhead() != null){
                                overhead.add(pro.getDefaultOverhead());
                                log.debug("Added overhead from project: "+pro.getDefaultOverhead());
                            }
                            if(ass.getDeltaOverhead() != null ){
                                overhead.add(ass.getDeltaOverhead());
                                log.debug("Added overhead from user: "+pro.getDefaultOverhead());
                            }
                        }
                    }
                }else{
                    log.debug("Class was iteration class, overhead :"+blog.getClass());
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
     * EffortLefts are calculated for every week by summings efforts
     * from blis assigned to user from projects that start/end date 
     * hit in particular week. 
     * 
     * For example:
     * Weeks             1   2  3  4 
     * Eff              20  20 10 10
     * projects          2   2  1  1
     * (10h blis each)
     * 
     * So efforts are just summed together and NOT divided for length of
     * the project or weeks.
     */
    public DailyWorkLoadData getDailyWorkLoadData(User user, int weeksAhead) {
        
        HashMap<Integer, String>effortsLeftMap = new HashMap<Integer, String>();
        HashMap<Integer, String>overheadsMap = new HashMap<Integer, String>();
        HashMap<Integer, String>totalsMap = new HashMap<Integer, String>();
        ArrayList<Integer> weekNumbers = new ArrayList<Integer>();
        String[] overallTotals = new String[3]; // Not currently used, but could use in the future
        
        // 1. Effort Lefts
        Map<Backlog, List<BacklogItem>> items = this.userBusiness.getBacklogItemsAssignedToUser(user);        
        effortsLeftMap = this.calculateEffortLefts(new GregorianCalendar().getTime(), weeksAhead, items);
        
        // 2. Overheads 
        List<Backlog> assignedBacklogs = this.userBusiness.getUsersBacklogs(user);
        overheadsMap = this.calculateOverheads(new GregorianCalendar().getTime(), weeksAhead, assignedBacklogs, user);
        for(Integer week : effortsLeftMap.keySet()){
            AFTime weekTotal = new AFTime(0);
            AFTime tmp = new AFTime(effortsLeftMap.get(week));                        
            weekTotal.add(tmp);
            tmp = new AFTime(overheadsMap.get(week));
            weekTotal.add(tmp);
            log.debug("Setting weekly total for week"+week+" to :"+weekTotal);
            totalsMap.put(week, weekTotal.toString());            
            weekNumbers.add(week);
        }
                
        DailyWorkLoadData data = new DailyWorkLoadData();
        data.setEffortsLeftMap(effortsLeftMap);
        data.setTotalsMap(totalsMap);
        data.setOverheadsMap(overheadsMap);
        data.setWeekNumbers(weekNumbers);
        data.setOverallTotals(overallTotals);
        
        return data;
    }

    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

}
