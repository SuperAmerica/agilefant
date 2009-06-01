package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
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
