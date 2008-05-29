package fi.hut.soberit.agilefant.business.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.model.User;

public class HourEntryBusinessImpl implements HourEntryBusiness {
    private BacklogItemHourEntryDAO backlogItemHourEntryDAO;
    private UserDAO userDAO;


    
    public void setHourEntryDAO(BacklogItemHourEntryDAO hourEntryDAO) {
        this.backlogItemHourEntryDAO = hourEntryDAO;
    }
    
    public BacklogItemHourEntryDAO getHourEntryDAO() {
        return backlogItemHourEntryDAO;
    }
    public HourEntry getId(int id) {
        return backlogItemHourEntryDAO.get(id);
    }
    public void remove(int id) {
        backlogItemHourEntryDAO.remove(id);
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    public void store(TimesheetLoggable parent, HourEntry hourEntry) {
        if(parent instanceof BacklogItem) {
            BacklogItemHourEntry store = new BacklogItemHourEntry();
            store.setDate(hourEntry.getDate());
            store.setUser(hourEntry.getUser());
            store.setDescription(hourEntry.getDescription());
            store.setTimeSpent(hourEntry.getTimeSpent());
            store.setId((Integer)backlogItemHourEntryDAO.create(store));
            store.setBacklogItem((BacklogItem)parent);
            backlogItemHourEntryDAO.store(store);
            
        }
    }
    public Date formatDate(String startDate)
        throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setLenient(true);
        return df.parse(startDate);
    }
    /**
     * {@inheritDoc}
     */
    public void addHourEntryForMultipleUsers(TimesheetLoggable parent, HourEntry hourEntry, int[] userIds) {
        for (int id : userIds) {
            User current = userDAO.get(id);
            hourEntry.setUser(current);
            store(parent,hourEntry);
        }
        hourEntry.setUser(null);
    }

    public List<BacklogItemHourEntry> getEntriesByBacklogItem(BacklogItem parent) {
        return backlogItemHourEntryDAO.getEntriesByBacklogItem(parent);
    }

    public Map<Integer, Integer> getSumsByBacklog(Backlog parent) {
        return null;
    }

}
