package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;

import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class DailyWorkAction extends ActionSupport {
    private static final long serialVersionUID = 5732278003634700787L;

    private List<Iteration> iterations;
    
    private List<Project> projects;
    
    private Map<Backlog, AFTime> effortSums;

    private NavigableMap<Backlog, List<BacklogItem>> bliMap;

    private User user;
    
    private BacklogBusiness backlogBusiness;

    private UserBusiness userBusiness;

    private int userId;

    private List<BacklogItem> backlogItemsForUserInProgress;

    private List<User> userList;

    @Override
    public String execute() throws Exception {
        if (userId == 0) {
            userId = SecurityUtil.getLoggedUserId();
        }

        user = userBusiness.getUser(userId);
        effortSums = new HashMap<Backlog, AFTime>();


        bliMap = userBusiness.getBacklogItemsAssignedToUser(user);
        projects = new ArrayList<Project>();
        iterations = new ArrayList<Iteration>();
        
        NavigableSet<Backlog> backlogs = bliMap.navigableKeySet();
        Iterator<Backlog> iter = backlogs.iterator();

        while (iter.hasNext()) {
            Backlog backlog = iter.next();
            if (backlog instanceof Project) {
                projects.add((Project)backlog);
            }
            else if (backlog instanceof Iteration) {
                iterations.add((Iteration) backlog);
            }
            List<BacklogItem> blis = bliMap.get(backlog);
            AFTime effLeftSum = backlogBusiness.getEffortLeftSum(blis);
            effortSums.put(backlog, effLeftSum);
        }
        

        backlogItemsForUserInProgress = userBusiness
                .getBacklogItemsInProgress(user);

        userList = userBusiness.getAllUsers();
        
        return super.execute();
    }

    public List<Project> getProjects() {
        return projects;
    }

    public List<Iteration> getIterations() {
        return iterations;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public NavigableMap<Backlog, List<BacklogItem>> getBliMap() {
        return bliMap;
    }

    public void setBliMap(NavigableMap<Backlog, List<BacklogItem>> bliMap) {
        this.bliMap = bliMap;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public List<BacklogItem> getBacklogItemsForUserInProgress() {
        return backlogItemsForUserInProgress;
    }

    public void setBacklogItemsForUserInProgress(
            List<BacklogItem> backlogItemsForUserInProgress) {
        this.backlogItemsForUserInProgress = backlogItemsForUserInProgress;
    }

    public Map<Backlog, AFTime> getEffortSums() {
        return effortSums;
    }

    public void setEffortSums(Map<Backlog, AFTime> effortSums) {
        this.effortSums = effortSums;
    }

    public BacklogBusiness getBacklogBusiness() {
        return backlogBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }
}