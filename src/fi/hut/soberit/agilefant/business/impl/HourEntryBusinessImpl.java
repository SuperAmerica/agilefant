package fi.hut.soberit.agilefant.business.impl;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.StoryHourEntryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.CalendarUtils;
import fi.hut.soberit.agilefant.util.HourEntryUtils;

@Service("hourEntryBusiness")
@Transactional
public class HourEntryBusinessImpl extends GenericBusinessImpl<HourEntry>
        implements HourEntryBusiness {

    private HourEntryDAO hourEntryDAO;

    @Autowired
    private StoryHourEntryDAO storyHourEntryDAO;
    @Autowired
    private BacklogHourEntryDAO backlogHourEntryDAO;
    @Autowired
    private UserDAO userDAO;

    @Autowired
    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.genericDAO = hourEntryDAO;
        this.hourEntryDAO = hourEntryDAO;
    }

    public HourEntry store(TimesheetLoggable parent, HourEntry hourEntry)
            throws IllegalArgumentException {
        HourEntry storable = null;

        if (parent == null) {
            throw new IllegalArgumentException("Unknown parent type.");
        }
        if (hourEntry == null) {
            throw new IllegalArgumentException("No data given.");
        }
        if (parent instanceof Story) {
            if ((storable = storyHourEntryDAO.get(hourEntry.getId())) == null) {
                storable = new StoryHourEntry();
            }
            ((StoryHourEntry) storable).setStory((Story) parent);
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
        storable.setMinutesSpent(hourEntry.getMinutesSpent());
        storable.setDate(hourEntry.getDate());
        if (parent instanceof Story) {
            storyHourEntryDAO.store((StoryHourEntry) storable);
        } else {
            backlogHourEntryDAO.store((BacklogHourEntry) storable);
        }
        return storable;
    }

    public List<BacklogHourEntry> retrieveByParent(Backlog parent) {
        return backlogHourEntryDAO.retrieveByBacklog(parent);
    }

    /**
     * {@inheritDoc}
     */
    public void addHourEntryForMultipleUsers(TimesheetLoggable parent, HourEntry hourEntry, Set<Integer> userIds) {
        for (int id : userIds) {
            User current = getUserDAO().get(id);
            if(current != null) {
                hourEntry.setUser(current);
                store(parent,hourEntry);
            }
        }
        hourEntry.setUser(null);
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void updateMultiple(Map<Integer, String[]> userIds,
            Map<Integer, String[]> dates, Map<Integer, String[]> efforts,
            Map<Integer, String[]> descriptions) {
        Set<Integer> ids = userIds.keySet();
        for(Integer entryId : ids) {      
            HourEntry entry = hourEntryDAO.get(entryId);
            if(entry == null)
                continue;
            Integer userId = Integer.parseInt(userIds.get(entryId)[0]);
            String dateStr = dates.get(entryId)[0];
            entry.setMinutesSpent(HourEntryUtils.convertFromString(efforts.get(entryId)[0]));
            DateTime date;
            try {
                date = new DateTime(CalendarUtils.parseDateFromString(dateStr));
                entry.setDate(date);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            User user = userDAO.get(userId);
            if(user == null)
                continue;
            entry.setUser(user);
            entry.setDescription(descriptions.get(entryId)[0]);
            hourEntryDAO.store(entry);
        }
        
    }

    @Transactional(readOnly = true)
    public long calculateSumByUserAndTimeInterval(User user,
            DateTime startDate, DateTime endDate) {
        return hourEntryDAO.calculateSumByUserAndTimeInterval(user, startDate, endDate);
    }
    
}
