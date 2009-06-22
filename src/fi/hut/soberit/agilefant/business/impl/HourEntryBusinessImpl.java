package fi.hut.soberit.agilefant.business.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.StoryHourEntryDAO;
import fi.hut.soberit.agilefant.db.TaskHourEntryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.HourEntryTO;
import fi.hut.soberit.agilefant.util.CalendarUtils;
import fi.hut.soberit.agilefant.util.DailySpentEffort;
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
    private TaskHourEntryDAO taskHourEntryDAO;

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
        } else if (parent instanceof Task) {
            if ((storable = taskHourEntryDAO.get(hourEntry.getId())) == null) {
                storable = new TaskHourEntry();
            }
            ((TaskHourEntry) storable).setTask((Task) parent);
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
        } else if(parent instanceof Task){
            TaskHourEntry t = (TaskHourEntry) storable;
            taskHourEntryDAO.store(t);
            storable = new HourEntryTO(t);
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
            User current = userDAO.get(id);
            if(current != null) {
                hourEntry.setUser(current);
                store(parent,hourEntry);
            }
        }
        hourEntry.setUser(null);
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
    
    @Transactional(readOnly = true)
    public long calculateSumByUserAndTimeInterval(int userId,
            DateTime startDate, DateTime endDate) {
        return hourEntryDAO.calculateSumByUserAndTimeInterval(userId, startDate, endDate);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public long calculateSum(Collection<? extends HourEntry> hourEntries) {
        long effortSpent = 0;
        for (HourEntry hourEntry : hourEntries) {
            effortSpent += hourEntry.getMinutesSpent();
        }
        return effortSpent;
    }
    
    @Transactional(readOnly = true)
    public long calculateSumOfIterationsHourEntries(Iteration iteration) {
        if (iteration == null) {
            throw new IllegalArgumentException("Iteration can't be null");
        }
        return hourEntryDAO.calculateIterationHourEntriesSum(iteration.getId());
    }
    
    public List<HourEntry> getEntriesByUserAndTimeInterval(int userId, DateTime startDate,
            DateTime endDate) {
        return this.hourEntryDAO.getHourEntriesByFilter(startDate, endDate, userId);
    }
    
    public List<HourEntry> getEntriesByUserAndDay(LocalDate day, int userId) {
        DateTime start = day.toDateMidnight().toDateTime();
        DateTime end = start.plusDays(1).minusSeconds(1);
        return this.hourEntryDAO.getHourEntriesByFilter(start, end, userId);
    }

    @Transactional(readOnly = true)
    public long calculateWeekSum(LocalDate week, int userId) {
        MutableDateTime tmp = new MutableDateTime(week.toDateMidnight());
        tmp.setDayOfWeek(DateTimeConstants.MONDAY);
        tmp.setSecondOfDay(1);
        DateTime start = tmp.toDateTime();
        tmp.addDays(6);
        tmp.setHourOfDay(23);
        tmp.setMinuteOfHour(59);
        tmp.setSecondOfMinute(59);
        DateTime end = tmp.toDateTime();
        return this.hourEntryDAO.calculateSumByUserAndTimeInterval(userId, start, end);
    }
    
    @Transactional(readOnly = true)
    public List<DailySpentEffort> getDailySpentEffortByWeek(LocalDate week, int userId) {
        MutableDateTime tmp = new MutableDateTime(week.toDateMidnight());
        tmp.setDayOfWeek(DateTimeConstants.MONDAY);
        tmp.setSecondOfDay(1);
        DateTime start = tmp.toDateTime();
        tmp.addDays(6);
        tmp.setHourOfDay(23);
        tmp.setMinuteOfHour(59);
        tmp.setSecondOfMinute(59);
        DateTime end = tmp.toDateTime();
        return this.getDailySpentEffortByInterval(start, end, userId);
    }
    
    @Transactional(readOnly = true)
    public List<DailySpentEffort> getDailySpentEffortByInterval(DateTime start,
            DateTime end, int userId) {
        Map<Date, Long> dbData = new HashMap<Date, Long>();
        List<DailySpentEffort> dailyEffort = new ArrayList<DailySpentEffort>();

        if(start.compareTo(end) >= 0) {
            return Collections.emptyList();
        }
        List<HourEntry> entries = this.hourEntryDAO.getHourEntriesByFilter(start, end, userId);
        
        //sum efforts per day
        for(HourEntry entry : entries) {
            Date date = entry.getDate().toDateMidnight().toDate();
            if(!dbData.containsKey(date)) {
                dbData.put(date, 0L);
            }
            dbData.put(date, dbData.get(date) + entry.getMinutesSpent());
        }
        MutableDateTime iteratorDate = new MutableDateTime(start.toDateMidnight());
        //construct list that has a single entry per day
        while(iteratorDate.compareTo(end) <= 0) {
            DailySpentEffort effortEntry = new DailySpentEffort();
            Date currentDate = iteratorDate.toDate();
            if(dbData.containsKey(currentDate)) {
                effortEntry.setSpentEffort(dbData.get(currentDate));
            }
            effortEntry.setDay(currentDate);
            dailyEffort.add(effortEntry);
            iteratorDate.addDays(1);
        }
        return dailyEffort;
    }
    
    public void setBacklogHourEntryDAO(BacklogHourEntryDAO backlogHourEntryDAO) {
        this.backlogHourEntryDAO = backlogHourEntryDAO;
    }
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setTaskHourEntryDAO(TaskHourEntryDAO taskHourEntryDAO) {
        this.taskHourEntryDAO = taskHourEntryDAO;
    }

    
}
