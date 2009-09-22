package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Holiday;
import fi.hut.soberit.agilefant.model.User;

/**
 * 
 * @author rjokelai
 * 
 */
@Service("userBusiness")
@Transactional
public class UserBusinessImpl extends GenericBusinessImpl<User> implements
        UserBusiness {

    private StoryDAO storyDAO;
    private UserDAO userDAO;

    public UserBusinessImpl() {
        super(User.class);
    }
    
    @Transactional(readOnly = true)
    public User retrieveByLoginName(String loginName) {
        return userDAO.getByLoginName(loginName);
    }

    @Transactional(readOnly = true)
    public List<User> getDisabledUsers() {
        return userDAO.listUsersByEnabledStatus(false);
    }

    @Transactional(readOnly = true)
    public List<User> getEnabledUsers() {
        return userDAO.listUsersByEnabledStatus(true);
    }

    @Transactional(readOnly = true)
    public boolean hasUserCreatedStories(User user) {
        return storyDAO.countByCreator(user) > 0;
    }
    
    @Transactional
    public User storeUser(User data, String password1, String password2) {
        super.store(data);
        User returned = this.retrieve(data.getId());
        return returned;
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

    @Autowired
    public void setUserDAO(UserDAO userDAO) {
        this.genericDAO = userDAO;
        this.userDAO = userDAO;
    }

    @Autowired
    public void setStoryDAO(StoryDAO storyDAO) {
        this.storyDAO = storyDAO;
    }

    public void disableUser(int id) {
        User user = userDAO.get(id);
        user.setEnabled(false);        
    }

    public void enableUser(int id) {
        User user = userDAO.get(id);
        user.setEnabled(true);  
    }
    

}
