package fi.hut.soberit.agilefant.business.impl;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.User;

public class HourEntryBusinessImpl implements HourEntryBusiness {
    private HourEntryDAO hourEntryDAO;
    private UserDAO userDAO;


    
    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.hourEntryDAO = hourEntryDAO;
    }
    
    public HourEntryDAO getHourEntryDAO() {
        return hourEntryDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * {@inheritDoc}
     */
    public void addHourEntryForMultipleUsers(HourEntry hourEntry, int[] userIds) {
        for (int id : userIds) {
            User current = userDAO.get(id);
            HourEntry store = new HourEntry();
            store.setDate(hourEntry.getDate());
            store.setUser(current);
            store.setDescription(hourEntry.getDescription());
            store.setTargetId(hourEntry.getTargetId());
            store.setTargetType(hourEntry.getTargetType());
            store.setTimeSpent(hourEntry.getTimeSpent());
            store.setId((Integer)hourEntryDAO.create(store));
            hourEntryDAO.store(store);
        }
    }

}
