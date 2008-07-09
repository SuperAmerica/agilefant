package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.business.impl.SettingBusinessImpl;
import fi.hut.soberit.agilefant.db.TeamDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.hibernate.EmailValidator;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

/**
 * UserAction
 * 
 * @author khel
 */
public class UserAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = 284890678155663442L;

    private int userId;

    private User user;

    private UserDAO userDAO;

    private TeamDAO teamDAO;

    private BacklogBusiness backlogBusiness;
    
    private UserBusiness userBusiness;
    
    private HourEntryBusiness hourEntryBusiness;

    private String password1;

    private String password2;

    private String email;
    
    private AFTime weekHours;
    
    private List<User> enabledUsers = new ArrayList<User>();
    
    private List<User> disabledUsers = new ArrayList<User>();
    
    private List<Team> teamList = new ArrayList<Team>();

    private Map<Integer, String> teamIds = new HashMap<Integer, String>();

    public String create() {
        createTeamList();
        userId = 0;
        user = new User();
        user.setEnabled(true);
        user.setWeekHours(new AFTime("40h"));
        return Action.SUCCESS;
    }

    public String delete() {
        if (userId == SecurityUtil.getLoggedUserId()) {
            super.addActionError("Cannot delete user currently logged in");
            return Action.ERROR;
        }

        User u = userDAO.get(userId);
        if (u == null) {
            super.addActionError(super.getText("user.notFound"));
            return Action.ERROR;
        }
        if (u.getAssignables().size() > 0) {
            super.addActionError(super.getText("user.hasLinkedItems"));
            return Action.ERROR;
        }
        /* 
         * User may have linked job reports. This creates a tricky situation 
         * if user has hour reports but hour reporting is currently turned off and
         * thus user can not view associated objects.
         */
        if(hourEntryBusiness.isAssociatedWithHourReport(u)) {
            super.addActionError(super.getText("user.linkedJobEntries"));
            return Action.ERROR;
        }
        /* Prevent the deletion of administrator */
        if (userId == 1) {
            super.addActionError("User cannot be deleted");
            return Action.ERROR;
        }
        /* Remove assignments before deleting user. */
        backlogBusiness.removeAssignments(u);
        /* Clear the teams */
        u.getTeams().clear();
        userDAO.remove(userId);
        return Action.SUCCESS;
    }

    public String edit() {
        createTeamList();
        user = userDAO.get(userId);
        if (user == null) {
            super.addActionError(super.getText("user.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
    
    public String disable() {
        user = userDAO.get(userId);
        userBusiness.disableUser(user);
        
        return Action.SUCCESS;
    }
    
    public String enable() {
        user = userDAO.get(userId);
        userBusiness.enableUser(user);
        
        return Action.SUCCESS;
    }

    public String store() {
        createTeamList();
        User storable = new User();
        if (userId > 0) {
            storable = userDAO.get(userId);
            if (storable == null) {
                super.addActionError(super.getText("user.notFound"));
                return Action.ERROR;
            }
        }
        this.fillStorable(storable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }

        userDAO.store(storable);

        return Action.SUCCESS;
    }

    protected void fillStorable(User storable) {
        String md5Pw = null;

        /* Check that password is correctly formed */
        if (password1.length() == 0 && password2.length() == 0) {
            if (storable.getId() == 0) {
                super.addActionError(super.getText("user.missingPassword"));
                return;
            }
            md5Pw = storable.getPassword();
        } else {
            if (!password1.equals(password2)) {
                password1 = "";
                password2 = "";
                super.addActionError(super.getText("user.passwordsNotEqual"));
                return;
            } else {
                md5Pw = SecurityUtil.MD5(password1);
            }
        }
        User existingUser = userDAO.getUser(this.user.getLoginName());
        if (existingUser != null && existingUser.getId() != storable.getId()) {
            super.addActionError(super.getText("user.loginNameInUse"));
            return;
        }

        if (this.user.getFullName() == null
                || this.user.getFullName().trim().equalsIgnoreCase("")) {
            super.addActionError("Full name is required");
            return;
        }
        
        if (this.user.getLoginName() == null ||
                this.user.getLoginName().trim().equalsIgnoreCase("")) {
            super.addActionError("Login name cannot be empty");
            return;
        }
        
        if (this.user.getWeekHours() == null) {
            super.addActionError("Weekly working hours are required");
            return;
        }
        else if (this.user.getWeekHours().getTime() < 0) {
            super.addActionError("Weekly working hours must be positive");
            return;
        }
                            
        storable.setFullName(this.user.getFullName());
        storable.setLoginName(this.user.getLoginName());
        storable.setEnabled(this.user.isEnabled());
        storable.setWeekHours(this.user.getWeekHours());
        storable.setPassword(md5Pw);

        // Set the initials
        if (this.user.getInitials() == null
                || this.user.getInitials().trim().compareTo("") == 0) {
            super.addActionError("Initials are required.");
            return;
        } else {
            storable.setInitials(this.user.getInitials());
        }

        if (this.user.getEmail() == null
                || this.user.getEmail().equalsIgnoreCase("")) {
            super.addActionError("Email is required");
            return;
        } else {
            EmailValidator e = new EmailValidator();
            if (!e.isValid(this.user.getEmail())) {
                super.addActionError(super.getText("user.invalidEmail"));
                return;
            }
        }
        storable.setEmail(this.user.getEmail());

        Collection<Team> listOfTeams = new ArrayList<Team>();
        for (int teamId : teamIds.keySet()) {
            listOfTeams.add(teamDAO.get(teamId));
        }

        storable.getTeams().clear();
        storable.getTeams().addAll(listOfTeams);
    }

    public String list() {
        createTeamList();
        setEnabledUsers(userBusiness.getEnabledUsers());
        setDisabledUsers(userBusiness.getDisabledUsers());
        return Action.SUCCESS;
    }

    private void createTeamList() {
        teamList.addAll(teamDAO.getAll());
        Collections.sort(teamList);
    }
    
    

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Method added for testing.
     * 
     * @return UserDAO-object
     */
    /*
     * protected UserDAO getUserDAO() { return userDAO; }
     */

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AFTime getWeekHours() {
        return weekHours;
    }
    
    public void setWeekHours(AFTime hours) {
        this.weekHours = hours;
    }
    
    public List<Team> getTeamList() {
        return teamList;
    }

    public TeamDAO getTeamDAO() {
        return teamDAO;
    }

    public void setTeamDAO(TeamDAO teamDAO) {
        this.teamDAO = teamDAO;
    }

    public Map<Integer, String> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(Map<Integer, String> teamIds) {
        this.teamIds = teamIds;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setTeamList(List<Team> teamList) {
        this.teamList = teamList;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public List<User> getEnabledUsers() {
        return enabledUsers;
    }

    public void setEnabledUsers(List<User> enabledUsers) {
        this.enabledUsers = enabledUsers;
    }

    public List<User> getDisabledUsers() {
        return disabledUsers;
    }

    public void setDisabledUsers(List<User> disabledUsers) {
        this.disabledUsers = disabledUsers;
    }

    public HourEntryBusiness getHourEntryBusiness() {
        return hourEntryBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }
}
