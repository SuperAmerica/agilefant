package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.DailyWorkBusiness;
import fi.hut.soberit.agilefant.business.RankUnderDelegate;
import fi.hut.soberit.agilefant.business.RankingBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.WhatsNextEntryDAO;
import fi.hut.soberit.agilefant.model.Rankable;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO.TaskClass;

@Service("dailyWorkBusiness")
@Transactional
public class DailyWorkBusinessImpl implements DailyWorkBusiness {
    private TaskDAO taskDAO;
    private WhatsNextEntryDAO whatsNextEntryDAO;
    private RankingBusiness rankingBusiness;
    private TaskBusiness taskBusiness;
    
    @Autowired
    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    @Autowired
    public void setWhatsNextEntryDAO(WhatsNextEntryDAO dao) {
        this.whatsNextEntryDAO = dao;
    } 

    @Autowired
    public void setRankingBusiness(RankingBusiness rankingBusiness) {
        this.rankingBusiness = rankingBusiness;
    }

    @Autowired
    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }

    public Collection<DailyWorkTaskTO> getCurrentTasksForUser(User user) {
        DateTime now = new DateTime();
        DateTime dayStart = now.withMillisOfDay(0);
        DateTime dayEnd   = dayStart.plusDays(1);
        Interval interval = new Interval(dayStart, dayEnd);
        
        ArrayList<DailyWorkTaskTO> returned = new ArrayList<DailyWorkTaskTO>();
        for (Task task: taskDAO.getAllIterationAndStoryTasks(user, interval)) {
            returned.add(new DailyWorkTaskTO(task, DailyWorkTaskTO.TaskClass.ASSIGNED, -1));
        }
        
        return returned;
    }

    public Collection<DailyWorkTaskTO> getNextTasksForUser(User user) {
        Collection<WhatsNextEntry> entries = whatsNextEntryDAO.getWhatsNextEntriesFor(user);
        Collection<DailyWorkTaskTO> returned = new ArrayList<DailyWorkTaskTO>();
        
        for (WhatsNextEntry entry: entries) {
            Task task = entry.getTask();

            DailyWorkTaskTO item = new DailyWorkTaskTO(task);
            item.setWorkQueueRank(entry.getRank());
            
            Collection<User> responsibles = task.getResponsibles();
            if (responsibles != null && responsibles.contains(user)) {
                item.setTaskClass(TaskClass.NEXT_ASSIGNED);
            }
            else {
                item.setTaskClass(TaskClass.NEXT);
            }
            
            returned.add(item);
        }
        
        return returned;
    }
    
    public Collection<DailyWorkTaskTO> getAllCurrentTasksForUser(User user) {
        Collection<DailyWorkTaskTO> tasks = getNextTasksForUser(user);
        HashSet<Long> usedIds = new HashSet<Long>();
        for (DailyWorkTaskTO task: tasks) {
            usedIds.add((long)task.getId());
        }
        
        for (DailyWorkTaskTO task: getCurrentTasksForUser(user)) {
            if (usedIds.contains((long)task.getId())) {
                continue;
            }
            
            tasks.add(task);
        }
        return tasks;
    }

    @Transactional
    public DailyWorkTaskTO rankToBottomOnWhatsNext(final WhatsNextEntry entry) throws IllegalArgumentException {
        if (entry == null) {
            throw new IllegalArgumentException();
        }
        
        rankingBusiness.rankToBottom(entry, whatsNextEntryDAO.getLastTaskInRank(entry.getUser()));
        return new DailyWorkTaskTO(entry.getTask(), DailyWorkTaskTO.TaskClass.NEXT, entry.getRank());
    }
    
    @Transactional
    public DailyWorkTaskTO rankToBottomOnWhatsNext(User user, Task task)
            throws IllegalArgumentException {
        return rankToBottomOnWhatsNext(whatsNextEntryDAO.getWhatsNextEntryFor(user, task));
    }

    @Transactional
    public DailyWorkTaskTO rankUnderTaskOnWhatsNext(final WhatsNextEntry entry, WhatsNextEntry upperEntry) {
        if (entry == null) {
            throw new IllegalArgumentException();
        }

        RankUnderDelegate delegate = new RankUnderDelegate() {
            public Collection<? extends Rankable> getWithRankBetween(Integer lower,
                    Integer upper) {
                return whatsNextEntryDAO.getTasksWithRankBetween(lower, upper, entry.getUser());
            }
        };
        
        rankingBusiness.rankUnder(entry, upperEntry, delegate);
        return new DailyWorkTaskTO(entry.getTask(), DailyWorkTaskTO.TaskClass.ASSIGNED, entry.getRank());
    }
    
    @Transactional
    public DailyWorkTaskTO rankUnderTaskOnWhatsNext(User user, Task task,
            Task upperTask) throws IllegalArgumentException {
        WhatsNextEntry entry = whatsNextEntryDAO.getWhatsNextEntryFor(user, task);
        
        if (entry == null) {
            entry = addToWhatsNext(user, task);
        }
        
        WhatsNextEntry upperEntry = null;
        if (upperTask != null) {
            upperEntry = whatsNextEntryDAO.getWhatsNextEntryFor(user, upperTask);
        }
        
        return rankUnderTaskOnWhatsNext(entry, upperEntry);
    }

    @Transactional
    public void removeFromWhatsNext(User user, Task task)
            throws IllegalArgumentException {
        WhatsNextEntry entry = whatsNextEntryDAO.getWhatsNextEntryFor(user, task);
        if (entry != null) {
            whatsNextEntryDAO.remove(entry);
        }
    }
    
    @Transactional
    public WhatsNextEntry addToWhatsNext(User user, Task task) {
        WhatsNextEntry entry = new WhatsNextEntry();
        entry.setTask(task);
        entry.setUser(user);
        whatsNextEntryDAO.store(entry);
        taskBusiness.addResponsible(task, user);
        rankToBottomOnWhatsNext(entry);
        return entry;
    }

    public void removeTaskFromWorkQueues(Task task) {
        whatsNextEntryDAO.removeAllByTask(task);
    }
}
