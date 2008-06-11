package fi.hut.soberit.agilefant.business.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.lang.IllegalArgumentException;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.db.GenericDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class HourEntryBusinessImpl implements HourEntryBusiness {
    private BacklogItemHourEntryDAO backlogItemHourEntryDAO;
    private BacklogHourEntryDAO backlogHourEntryDAO; 
    private UserDAO userDAO;
    private HourEntryDAO hourEntryDAO;
    private SettingBusiness settingBusiness;
    
    public HourEntryDAO getHourEntryDAO() {
        return hourEntryDAO;
    }

    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.hourEntryDAO = hourEntryDAO;
    } 
    
    public void setBacklogItemHourEntryDAO(BacklogItemHourEntryDAO hourEntryDAO) {
        this.backlogItemHourEntryDAO = hourEntryDAO;
    }
    
    public BacklogItemHourEntryDAO getBacklogItemHourEntryDAO() {
        return backlogItemHourEntryDAO;
    }
    public HourEntry getHourEntryById(int id) {
        return (HourEntry)hourEntryDAO.get(id);
    }
    public void remove(int id) {
        hourEntryDAO.remove(id);
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
    public HourEntry store(TimesheetLoggable parent, HourEntry hourEntry) 
            throws IllegalArgumentException {
        HourEntry storable = null;

        if (parent instanceof BacklogItem) {
            if ((storable = backlogItemHourEntryDAO.get(hourEntry.getId())) == null) {
                storable = new BacklogItemHourEntry();
            }
            ((BacklogItemHourEntry) storable)
                    .setBacklogItem((BacklogItem) parent);
        } else if (parent instanceof Backlog) {
            if ((storable = backlogHourEntryDAO.get(hourEntry.getId())) == null) {
                storable = new BacklogHourEntry();
            }
            ((BacklogHourEntry) storable).setBacklog((Backlog) parent);
        } else {
            throw new IllegalArgumentException("Unknown parent type.");
        }
        storable.setDate(hourEntry.getDate());
        storable.setUser(hourEntry.getUser());
        storable.setDescription(hourEntry.getDescription());
        storable.setTimeSpent(hourEntry.getTimeSpent());
        storable.setDate(hourEntry.getDate());
        if (parent instanceof BacklogItem) {
            backlogItemHourEntryDAO.store((BacklogItemHourEntry) storable);
        } else {
            backlogHourEntryDAO.store((BacklogHourEntry) storable);
        }
        return storable;
    }
    
    /**
     * {@inheritDoc}
     */
    public void addEntryForCurrentUser(TimesheetLoggable parent, AFTime effort) {
        User currentUser = SecurityUtil.getLoggedUser();
        HourEntry store = new HourEntry();
        store.setDate(new Date());
        store.setTimeSpent(effort);
        store.setUser(currentUser);
        store(parent,store);
    }

    /**
     * {@inheritDoc}
     */
    public Date formatDate(String startDate)
        throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        df.setLenient(true);
        return df.parse(startDate);
    }
    /**
     * {@inheritDoc}
     */
    public void addHourEntryForMultipleUsers(TimesheetLoggable parent, HourEntry hourEntry, Set<Integer> userIds) {
        for (int id : userIds) {
            User current = userDAO.get(id);
            if(current != null) {
                hourEntry.setUser(current);
                store(parent,hourEntry);
            }
        }
        hourEntry.setUser(null);
    }
    /**
     * {@inheritDoc}
     */
    public List<BacklogItemHourEntry> getEntriesByParent(BacklogItem parent) {
        return backlogItemHourEntryDAO.getEntriesByBacklogItem(parent);
    }
    /**
     * {@inheritDoc}
     */
    public List<BacklogHourEntry> getEntriesByParent(Backlog parent) {
        return backlogHourEntryDAO.getEntriesByBacklog(parent);
    }

    @Deprecated
    public Map<Integer, AFTime> getSumsByBacklog(Backlog parent) {
        Map<Integer, AFTime> sums = new HashMap<Integer, AFTime>();
        List<BacklogItemHourEntry> entries = 
            backlogItemHourEntryDAO.getSumsByBacklog(parent);
        
        for (BacklogItemHourEntry entry : entries) {
            AFTime currentSum = sums.get(entry.getBacklogItem().getId());
            AFTime timeSpent = entry.getTimeSpent();
            
            if (currentSum == null) {
                currentSum = new AFTime(0);
            }
            
            if (timeSpent != null) {
                currentSum.add(timeSpent);
            }
            
            sums.put(entry.getBacklogItem().getId(), currentSum);
        }
        
        return sums;
    }
    
    /**
     * {@inheritDoc}
     */
    public void loadSumsToBacklogItems(Backlog parent) {
        if (settingBusiness.isHourReportingEnabled()) {
            List<BacklogItemHourEntry> entries = 
                backlogItemHourEntryDAO.getSumsByBacklog(parent);
            
            for (BacklogItemHourEntry entry : entries) {
                AFTime currentSum = entry.getBacklogItem().getEffortSpent();
                AFTime timeSpent = entry.getTimeSpent();
                
                if (currentSum == null) {
                    currentSum = new AFTime(0);
                }
                
                if (timeSpent != null) {
                    currentSum.add(timeSpent);
                }
                
                entry.getBacklogItem().setEffortSpent(currentSum);
            }
       }
    }
    
    public Map<Integer, AFTime> getSumsByIterationGoal(Backlog parent) {
        Map<Integer, AFTime> sums = new HashMap<Integer, AFTime>();
        List<BacklogItemHourEntry> entries = 
            backlogItemHourEntryDAO.getSumsByBacklog(parent);
        
        for (BacklogItemHourEntry entry : entries) {
            if (entry.getBacklogItem().getIterationGoal() != null) {
            AFTime currentSum = sums.get(entry.getBacklogItem().getIterationGoal().getId());
            AFTime timeSpent = entry.getTimeSpent();
            
            if (currentSum == null) {
                currentSum = new AFTime(0);
            }
            
            if (timeSpent != null) {
                currentSum.add(timeSpent);
            }
            
            sums.put(entry.getBacklogItem().getIterationGoal().getId(), currentSum);
            }
        }
        
        return sums;
    }
    /**
     * {@inheritDoc}
     */
    public void removeHourEntriesByParent(TimesheetLoggable parent) {
        List<? extends HourEntry> removeThese = null;
        if(parent instanceof BacklogItem) {
          removeThese = backlogItemHourEntryDAO.getEntriesByBacklogItem( (BacklogItem)parent );
        } else if(parent instanceof Backlog) {
          removeThese = backlogHourEntryDAO.getEntriesByBacklog( (Backlog)parent );
        } else {
          return;
        }
        if(removeThese == null) {
          return;
        }
        for(HourEntry removable : removeThese) {
          try {
            remove(removable.getId());
          } catch(Exception e) { }
        }
      }   
    
    public AFTime getEffortSumByUserAndTimeInterval(User user, String startDate, String endDate)
            throws IllegalArgumentException {
        AFTime sum;
        try {
            Date start = this.formatDate(startDate);
            Date end = this.formatDate(endDate);   
            sum = getEffortSumByUserAndTimeInterval(user,start,end);
        } catch (ParseException pe) {
            throw new IllegalArgumentException("Invalid format.");
        }
        return sum;
    }
    
    public AFTime getEffortSumByUserAndTimeInterval(User user, Date start, Date end)
            throws IllegalArgumentException {        
        AFTime sum;
                
        if (start.after(end)) {
            throw new IllegalArgumentException("StartDate after endDate.");
        } else {
            sum = this.hourEntryDAO
                      .getEffortSumByUserAndTimeInterval(user, start, end);
        }  
        
        if (sum == null) {
            sum = new AFTime(0);
        }
        
        return sum;
    }

    public BacklogHourEntryDAO getBacklogHourEntryDAO() {
        return backlogHourEntryDAO;
    }

    public void setBacklogHourEntryDAO(BacklogHourEntryDAO backlogHourEntryDAO) {
        this.backlogHourEntryDAO = backlogHourEntryDAO;
    }

    public SettingBusiness getSettingBusiness() {
        return settingBusiness;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }
}
