package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.TaskStatus;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

/**
 * 
 * @author rjokelai
 * 
 */
public class UserBusinessImpl implements UserBusiness {
    private UserDAO userDAO;

    /** {@inheritDoc} * */
    public List<BacklogItem> getBacklogItemsInProgress(User user) {
        if (user == null) {
            user = SecurityUtil.getLoggedUser();
        }
        List<BacklogItem> userItems = userDAO.getBacklogItemsInProgress(user);
        List<BacklogItem> returnItems = new ArrayList<BacklogItem>();

        /*
         * Sort out the not started and done tasks and tasks from not current
         * iterations
         */
        Iterator<BacklogItem> iter = userItems.iterator();
        while (iter.hasNext()) {
            BacklogItem bli = iter.next();

            if (bli.getPlaceHolder().getStatus() != TaskStatus.NOT_STARTED
                    && bli.getPlaceHolder().getStatus() != TaskStatus.DONE) {

                returnItems.add(bli);
            }
        }

        return returnItems;
    }

    public List<User> getAllUsers() {
        return (List<User>) userDAO.getAll();
    }

    public User getUser(int userId) {
        return userDAO.get(userId);
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
}
