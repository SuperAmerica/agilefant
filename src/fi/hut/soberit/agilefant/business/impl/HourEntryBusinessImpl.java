package fi.hut.soberit.agilefant.business.impl;

import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
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
