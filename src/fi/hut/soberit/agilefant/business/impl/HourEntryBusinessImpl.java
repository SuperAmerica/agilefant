package fi.hut.soberit.agilefant.business.impl;

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
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryHourEntry;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailySpentEffort;

@Service("hourEntryBusiness")
@Transactional
public class HourEntryBusinessImpl extends GenericBusinessImpl<HourEntry>
        implements HourEntryBusiness {

    private HourEntryDAO hourEntryDAO;

    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private UserBusiness userBusiness;
  
    @Autowired
    private BacklogHourEntryDAO backlogHourEntryDAO;

    public HourEntryBusinessImpl() {
        super(HourEntry.class);
    }

    @Autowired
    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.genericDAO = hourEntryDAO;
        this.hourEntryDAO = hourEntryDAO;
    }

    @Transactional
    public void logBacklogEffort(int backlogId, HourEntry effortEntry,
            Set<Integer> userIds) {
        // TODO Auto-generated method stub
        
    }

    @Transactional
    public void logStoryEffort(int storyId, HourEntry effortEntry,
            Set<Integer> userIds) {
        
        Story story = this.storyBusiness.retrieve(storyId);
        Collection<User> targetUsers = this.userBusiness.retrieveMultiple(userIds);
        
        for(User targetUser : targetUsers) {
            StoryHourEntry storyEntry = new StoryHourEntry();
            storyEntry.setStory(story);
            storyEntry.setUser(targetUser);
            validateAndCopyFields(storyEntry, effortEntry);
            this.hourEntryDAO.create(storyEntry);
        }
    }

    @Transactional
    public void logTaskEffort(int taskId, HourEntry effortEntry,
            Set<Integer> userIds) {
        // TODO Auto-generated method stub
        
    }
    
    private void validateAndCopyFields(HourEntry target, HourEntry source) {
        if(source.getDate() == null) {
            throw new IllegalArgumentException("Invalid date");
        }
        target.setDescription(source.getDescription());
        target.setDate(source.getDate());
        target.setMinutesSpent(source.getMinutesSpent());
    }

    public List<BacklogHourEntry> retrieveByParent(Backlog parent) {
        return backlogHourEntryDAO.retrieveByBacklog(parent);
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

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
  
}
