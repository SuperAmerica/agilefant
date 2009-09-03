package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.DailyWorkBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;

@Service("dailyWorkBusiness")
@Transactional(readOnly=true)
public class DailyWorkBusinessImpl implements DailyWorkBusiness {
    private TaskDAO taskDAO;

    @Autowired
    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    } 
    
    public Collection<DailyWorkTaskTO> getCurrentTasksForUser(User user) {
        DateTime now = new DateTime();
        DateTime dayStart = now.withMillisOfDay(0);
        DateTime dayEnd   = dayStart.plusDays(1);
        Interval interval = new Interval(dayStart, dayEnd);
        
        ArrayList<DailyWorkTaskTO> returned = new ArrayList<DailyWorkTaskTO>();
        for (Task task: taskDAO.getAllIterationAndStoryTasks(user, interval)) {
            returned.add(new DailyWorkTaskTO(task, DailyWorkTaskTO.TaskClass.CURRENT));
        }
        
        return returned;
    }

    public Collection<DailyWorkTaskTO> getNextTasksForUser(User user) {
        // TODO: actually implement this!
//        Collection<DailyWorkTaskTO> tasks = getCurrentTasksForUser(user);
//        
//        int half = (tasks.size() + 1) / 2;
//        List<DailyWorkTaskTO> rv = new ArrayList<DailyWorkTaskTO>();
//        Iterator<DailyWorkTaskTO> it = tasks.iterator();
//        for (int i = 0; i < half; i++) {
//            rv.add(new DailyWorkTaskTO(it.next(), DailyWorkTaskTO.TaskClass.NEXT));
//        }
//        
//        // stable shuffle? ;)
//        Random rnd = new Random(0xDEADBEEF);
//        Collections.shuffle(rv, rnd);

        return new ArrayList<DailyWorkTaskTO>();
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
}
