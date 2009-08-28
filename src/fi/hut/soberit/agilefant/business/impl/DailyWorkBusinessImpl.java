package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.DailyWorkBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

@Service("dailyWorkBusiness")
@Transactional(readOnly=true)
public class DailyWorkBusinessImpl implements DailyWorkBusiness {
    private TaskDAO taskDAO;

    @Autowired
    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    } 
    
    public Collection<Task> getDailyTasksForUser(User user) {
        DateTime now = new DateTime();
        DateTime dayStart = now.withMillisOfDay(0);
        DateTime dayEnd   = dayStart.plusDays(1);
        Interval interval = new Interval(dayStart, dayEnd);
        
        return taskDAO.getAllIterationAndStoryTasks(user, interval);
    }
}
