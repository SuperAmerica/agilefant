package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.TeamBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.Team;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

/**
 * 
 * @author rjokelai
 * 
 */
@Service("userBusiness")
@Transactional
public class UserBusinessImpl extends GenericBusinessImpl<User> implements
        UserBusiness {

    private UserDAO userDAO;
    
    private TeamBusiness teamBusiness;

    public UserBusinessImpl() {
        super(User.class);
    }
    
    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.genericDAO = userDAO;
        this.userDAO = userDAO;
    }

    @Autowired
    public void setTeamBusiness(TeamBusiness teamBusiness) {
        this.teamBusiness = teamBusiness;
    }
    
    

    @Transactional(readOnly = true)
    public User retrieveByLoginName(String loginName) {
        return userDAO.getByLoginName(loginName);
    }

    @Transactional(readOnly = true)
    public boolean isLoginNameUnique(String loginName) {
        return userDAO.getByLoginNameIgnoreCase(loginName) == null;
    }
    
    @Transactional(readOnly = true)
    public List<User> getDisabledUsers() {
        return userDAO.listUsersByEnabledStatus(false);
    }

    @Transactional(readOnly = true)
    public List<User> getEnabledUsers() {
        return userDAO.listUsersByEnabledStatus(true);
    }

    @Transactional
    public User storeUser(User data, Set<Integer> teamIds, String password, String passwordConfirm) {
        
        changePassword(data, password, passwordConfirm);
        changeTeams(data, teamIds);
        
        return storeOrCreate(data);
    }

    private void changeTeams(User data, Set<Integer> teamIds) {
        if (teamIds != null) {
            Collection<Team> teams = new HashSet<Team>();
            for (Integer tid : teamIds) {
                teams.add(teamBusiness.retrieve(tid));
            }
            data.setTeams(teams);
        }
    }

    private User storeOrCreate(User data) {
        User returned;
        if (data.getId() == 0) {
            int newId = (Integer)userDAO.create(data);
            returned = userDAO.get(newId);
        }
        else {
            userDAO.store(data);
            returned = data;
        }
        return returned;
    }

    private void changePassword(User data, String password,
            String passwordConfirm) {
        if (password != null) {
            if (!password.equals(passwordConfirm)) {
                throw new IllegalArgumentException("Passwords don't match");
            }
            else if (!password.equalsIgnoreCase("")) {
                String md5hash = SecurityUtil.MD5(password);
                data.setPassword(md5hash);    
            }
        }
    }
    
    public boolean isDayUserHoliday(DateTime date, User user) {
        for(Holiday holiday : user.getHolidays()) {
            if(holiday.getInterval().contains(date)) {
                return true;
            }
        }
        return false;
    }
    
    public Duration calculateWorktimePerPeriod(User user, Interval interval) {
        MutableDateTime iterator = new MutableDateTime(interval.getStart());
        int deductDays = 0;
        
        while(iterator.isBefore(interval.getEnd())) {
            if(iterator.getDayOfWeek() == DateTimeConstants.SATURDAY || iterator.getDayOfWeek() == DateTimeConstants.SUNDAY) {
                deductDays++;
            }
            if(this.isDayUserHoliday(iterator.toDateTime(), user)) {
                deductDays++;
            }
            iterator.addDays(1);
        }
        //if interval ends on a holiday at 00:00 the result would be negative
        Duration worktime = new Duration(interval.getStart(), interval.getEnd().minusDays(deductDays));
        if(worktime.getMillis() < 0 ) {
            return new Duration(0);
        }
        return worktime;
    }

    public ExactEstimate calculateWorkHoursPerPeriod(User user,
            Interval interval) {
        Duration workDuration = this.calculateWorktimePerPeriod(user, interval);
        return new ExactEstimate((long) workDuration.toStandardSeconds()
                .toStandardHours().getHours());
    }

    public void disableUser(int id) {
        User user = userDAO.get(id);
        user.setEnabled(false);        
    }

    public void enableUser(int id) {
        User user = userDAO.get(id);
        user.setEnabled(true);  
    }
    
    public void setAdmin(int id, boolean admin) {
        User user = userDAO.get(id);
        user.setAdmin(admin);
    }
    
    public User retrieveByCredentials(String loginName, String password) {
        User user = retrieveByLoginName(loginName);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

}
