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
import fi.hut.soberit.agilefant.business.RankingBusiness;
import fi.hut.soberit.agilefant.business.impl.RankinkBusinessImpl.RankDirection;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.WhatsNextEntryDAO;
import fi.hut.soberit.agilefant.model.Rankable;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;
import fi.hut.soberit.agilefant.util.Pair;

@Service("dailyWorkBusiness")
@Transactional(readOnly=true)
public class DailyWorkBusinessImpl implements DailyWorkBusiness {
    private TaskDAO taskDAO;
    private WhatsNextEntryDAO whatsNextEntryDAO;
    private RankingBusiness rankingBusiness;

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

    public Collection<DailyWorkTaskTO> getCurrentTasksForUser(User user) {
        DateTime now = new DateTime();
        DateTime dayStart = now.withMillisOfDay(0);
        DateTime dayEnd   = dayStart.plusDays(1);
        Interval interval = new Interval(dayStart, dayEnd);
        
        ArrayList<DailyWorkTaskTO> returned = new ArrayList<DailyWorkTaskTO>();
        for (Task task: taskDAO.getAllIterationAndStoryTasks(user, interval)) {
            returned.add(new DailyWorkTaskTO(task, DailyWorkTaskTO.TaskClass.CURRENT, -1));
        }
        
        return returned;
    }

    public Collection<DailyWorkTaskTO> getNextTasksForUser(User user) {
        Collection<WhatsNextEntry> entries = whatsNextEntryDAO.getWhatsNextEntriesFor(user);
        Collection<DailyWorkTaskTO> returned = new ArrayList<DailyWorkTaskTO>();
        
        for (WhatsNextEntry entry: entries) {
            DailyWorkTaskTO item = new DailyWorkTaskTO(entry.getTask(), DailyWorkTaskTO.TaskClass.NEXT, entry.getRank());
            item.setRank(entry.getRank());
            
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
    public DailyWorkTaskTO rankToBottomOnWhatsNext(WhatsNextEntry entry) throws IllegalArgumentException {
        if (entry == null) {
            throw new IllegalArgumentException();
        }
        
        WhatsNextEntry lastInRank;
        lastInRank = whatsNextEntryDAO.getLastTaskInRank(entry.getUser());
        
        entry.setRank(lastInRank.getRank() + 1);

        return new DailyWorkTaskTO(entry.getTask(), DailyWorkTaskTO.TaskClass.NEXT, entry.getRank());
    }
    
    @Transactional
    public DailyWorkTaskTO rankToBottomOnWhatsNext(User user, Task task)
            throws IllegalArgumentException {
        return rankToBottomOnWhatsNext(whatsNextEntryDAO.getWhatsNextEntryFor(user, task));
    }

    @Transactional
    public DailyWorkTaskTO rankUnserTaskOnWhatsNext(WhatsNextEntry entry, WhatsNextEntry upperEntry) {
        RankDirection dir = rankingBusiness.findOutRankDirection(entry, upperEntry);
        int newRank = rankingBusiness.findOutNewRank(entry, upperEntry, dir);
        Pair<Integer, Integer> borders = rankingBusiness.getRankBorders(entry, upperEntry);
        
        Collection<Rankable> shiftables = new ArrayList<Rankable>();
        shiftables.addAll(whatsNextEntryDAO.getTasksWithRankBetween(borders.first, borders.second, entry.getUser()));

        rankingBusiness.shiftRanks(dir, shiftables);
        entry.setRank(newRank);
        return new DailyWorkTaskTO(entry.getTask(), DailyWorkTaskTO.TaskClass.CURRENT, newRank);
    }
    
    @Transactional
    public DailyWorkTaskTO rankUnderTaskOnWhatsNext(User user, Task task,
            Task upperTask) throws IllegalArgumentException {
        WhatsNextEntry entry = whatsNextEntryDAO.getWhatsNextEntryFor(user, task);
        WhatsNextEntry upperEntry = whatsNextEntryDAO.getWhatsNextEntryFor(user, upperTask);
        
        return rankUnserTaskOnWhatsNext(entry, upperEntry);
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
    public void addToWhatsNext(User user, Task task) {
        WhatsNextEntry entry = new WhatsNextEntry();
        entry.setTask(task);
        entry.setUser(user);
        whatsNextEntryDAO.store(entry);
        rankToBottomOnWhatsNext(entry);
    }

}
