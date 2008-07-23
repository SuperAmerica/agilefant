package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.ProjectType;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ProjectPortfolioData;

public class ProjectPortfolioAction extends ActionSupport {

    private static final long serialVersionUID = -4749839976470627112L;

    private ProjectBusiness projectBusiness;
    
    private TeamBusiness teamBusiness;
    
    private UserBusiness userBusiness;
    
    private BusinessThemeBusiness businessThemeBusiness;

    private int projectId;

    private Collection<ProjectType> projectTypes;
    
    private Collection<BusinessTheme> businessThemes;

    private Map<Project, String> summaryUserData;
    
    private Map<Project, Integer> summaryUnassignedUserData;

    private Map<Project, String> summaryLoadLeftData;

    private Map<String, String> loadLeftData;

    private Map<Project, List<User>> assignedUsers;
    
    private Map<String, Integer> unassignedUsers;
    
    private Map<String, String> userOverheads;
    
    private Map<String, String> totalUserOverheads;
                                     
    @Override
    public String execute() throws Exception {
        projectTypes = projectBusiness.getProjectTypes();
        businessThemes = businessThemeBusiness.getAll();
        ProjectPortfolioData data = projectBusiness.getProjectPortfolioData();
        summaryUserData = data.getSummaryUserData();
        summaryLoadLeftData = data.getSummaryLoadLeftData();
        loadLeftData = data.getLoadLefts();
        assignedUsers = data.getAssignedUsers();
        summaryUnassignedUserData = data.getSummaryUnassignedUserData();
        unassignedUsers = data.getUnassignedUsers();
        userOverheads = data.getUserOverheads();
        totalUserOverheads = data.getTotalUserOverheads();
        
        return super.execute();
    }

    public Collection<Project> getAll() {
        return projectBusiness.getAll();
    }

    public Collection<Project> getOngoingRankedProjects() {
        return projectBusiness.getOngoingRankedProjects();
    }

    public Collection<Project> getOngoingUnrankedProjects() {
        return projectBusiness.getOngoingUnrankedProjects();
    }

    public ProjectBusiness getProjectBusiness() {
        return projectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public String moveProjectUp() {
        projectBusiness.moveUp(projectId);
        return Action.SUCCESS;
    }

    public String moveProjectDown() {
        projectBusiness.moveDown(projectId);
        return Action.SUCCESS;
    }

    public String moveProjectTop() {
        projectBusiness.moveToTop(projectId);
        return Action.SUCCESS;
    }

    public String moveProjectBottom() {
        projectBusiness.moveToBottom(projectId);
        return Action.SUCCESS;
    }

    public String unrankProject() {
        projectBusiness.unrank(projectId);
        return Action.SUCCESS;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public Collection<ProjectType> getProjectTypes() {
        return projectTypes;
    }

    public void setProjectTypes(Collection<ProjectType> projectTypes) {
        this.projectTypes = projectTypes;
    }

    public Map<Project, String> getSummaryUserData() {
        return summaryUserData;
    }

    public Map<Project, String> getSummaryLoadLeftData() {
        return summaryLoadLeftData;
    }

    public Map<String, String> getLoadLefts() {
        return loadLeftData;
    }

    public Map<Project, List<User>> getAssignedUsers() {
        return assignedUsers;
    }

    public Map<Project, Integer> getSummaryUnassignedUserData() {
        return summaryUnassignedUserData;
    }

    public List<User> getUserList() {
        return this.userBusiness.getAllUsers();
    }
    
    public List<Team> getTeamList() {
        return this.teamBusiness.getAllTeams();
    }

    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public TeamBusiness getTeamBusiness() {
        return teamBusiness;
    }

    public void setTeamBusiness(TeamBusiness teamBusiness) {
        this.teamBusiness = teamBusiness;
    }

    public Map<String, Integer> getUnassignedUsers() {
        return unassignedUsers;
    }

    public void setUnassignedUsers(Map<String, Integer> unassignedUsers) {
        this.unassignedUsers = unassignedUsers;
    }

    public Map<String, String> getUserOverheads() {
        return userOverheads;
    }

    public void setUserOverheads(Map<String, String> userOverheads) {
        this.userOverheads = userOverheads;
    }

    public Map<String, String> getTotalUserOverheads() {
        return totalUserOverheads;
    }

    public void setTotalUserOverheads(Map<String, String> totalUserOverheads) {
        this.totalUserOverheads = totalUserOverheads;
    }

    public Collection<BusinessTheme> getBusinessThemes() {
        return businessThemes;
    }

    public void setBusinessThemes(Collection<BusinessTheme> businessThemes) {
        this.businessThemes = businessThemes;
    }

    public BusinessThemeBusiness getBusinessThemeBusiness() {
        return businessThemeBusiness;
    }

    public void setBusinessThemeBusiness(BusinessThemeBusiness businessThemeBusiness) {
        this.businessThemeBusiness = businessThemeBusiness;
    }

}
