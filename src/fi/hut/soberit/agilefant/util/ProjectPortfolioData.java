package fi.hut.soberit.agilefant.util;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;

public class ProjectPortfolioData {
    private Map<Project, List<User>> assignedUsers;
    /**
     * Key: projectId-userId
     */ 
    private Map<String, String> loadLefts;
    private Map<Project, String> summaryUserData;
    private Map<Project, Integer> summaryUnassignedUserData;
    private Map<Project, String> summaryLoadLeftData;
    
    /** 
     * Key: projectId-userId, 
     * Value: 1 if unassigned to project but has assigned blis
     *        key not found in map otherwise 
     */
    private Map<String, Integer> unassignedUsers;
    
    public ProjectPortfolioData() {
    }

    public Map<Project, List<User>> getAssignedUsers() {
        return assignedUsers;
    }

    public Map<String, String> getLoadLefts() {
        return loadLefts;
    }

    public Map<Project, String> getSummaryUserData() {
        return summaryUserData;
    }

    public Map<Project, String> getSummaryLoadLeftData() {
        return summaryLoadLeftData;
    }

    public void setAssignedUsers(Map<Project, List<User>> assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    public void setLoadLefts(Map<String, String> loadLefts) {
        this.loadLefts = loadLefts;
    }

    public void setSummaryUserData(Map<Project, String> summaryUserData) {
        this.summaryUserData = summaryUserData;
    }

    public void setSummaryLoadLeftData(Map<Project, String> summaryLoadLeftData) {
        this.summaryLoadLeftData = summaryLoadLeftData;
    }

    public Map<Project, Integer> getSummaryUnassignedUserData() {
        return summaryUnassignedUserData;
    }

    public void setSummaryUnassignedUserData(
            Map<Project, Integer> summaryUnassignedUserData) {
        this.summaryUnassignedUserData = summaryUnassignedUserData;
    }

    public Map<String, Integer> getUnassignedUsers() {
        return unassignedUsers;
    }

    public void setUnassignedUsers(Map<String, Integer> unassignedUsers) {
        this.unassignedUsers = unassignedUsers;
    }
}
