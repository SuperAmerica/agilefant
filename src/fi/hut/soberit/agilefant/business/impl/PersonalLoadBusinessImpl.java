package fi.hut.soberit.agilefant.business.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.chart.axis.DateTick;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.PersonalLoadBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.IntervalLoadContainer;

@Service("personalLoadBusiness")
@Transactional
public class PersonalLoadBusinessImpl implements PersonalLoadBusiness {
    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private StoryDAO storyDAO;

    long calculateUserPortion(List<Task> tasks,
            Map<Integer, Integer> responsibleCounts) {
        long totalEffortLeft = 0L;
        for (Task task : tasks) {
            if (task.getEffortLeft() != null
                    && responsibleCounts.get(task.getId()) != null) {
                long taskEffortLeft = task.getEffortLeft().getMinorUnits();
                int numberOfAssignees = responsibleCounts.get(task.getId());
                totalEffortLeft += taskEffortLeft / numberOfAssignees;
            }
        }
        return totalEffortLeft;
    }

    long calculateStoryAssignedTaskLoad(User user, DateTime startDate,
            DateTime endDate) {
        List<Task> storyTasks = this.taskDAO
                .getUnassignedTasksByStoryResponsibles(user, startDate, endDate);

        Set<Integer> storyTaskStoryIds = new HashSet<Integer>();

        for (Task task : storyTasks) {
            storyTaskStoryIds.add(task.getStory().getId());
        }

        Map<Integer, Integer> responsibleCounts = storyDAO
                .getNumOfResponsiblesByStory(storyTaskStoryIds);
        return calculateUserPortion(storyTasks, responsibleCounts);
    }

    long calculateDirectlyAssignedTaskLoad(User user, DateTime startDate,
            DateTime endDate) {
        List<Task> assignedTasks = this.taskDAO
                .getIterationTasksByUserAndTimeframe(user, startDate, endDate);
        assignedTasks.addAll(this.taskDAO.getStoryTasksByUserAndTimeframe(user,
                startDate, endDate));

        Set<Integer> assignedTaskIds = new HashSet<Integer>();

        for (Task task : assignedTasks) {
            assignedTaskIds.add(task.getId());
        }
        Map<Integer, Integer> responsibleCounts = taskDAO
                .getNumOfResponsiblesByTask(assignedTaskIds);
        return calculateUserPortion(assignedTasks, responsibleCounts);
    }
    
    public long calculateTotalAssignedLoad(User user, DateTime startDate,
            DateTime endDate) {
        return calculateDirectlyAssignedTaskLoad(user, startDate, endDate)
                + calculateStoryAssignedTaskLoad(user, startDate, endDate);
    }
    
    public List<IntervalLoadContainer> calculateAvailableMinutesPerWeek(User user, DateTime startDate,
            DateTime endDate) {
        List<IntervalLoadContainer> ret = new ArrayList<IntervalLoadContainer>();
        if(startDate.compareTo(endDate) > 0) {
            return Collections.emptyList();
        }
        MutableDateTime dateIterator = new MutableDateTime(startDate);
        while(startDate.compareTo(endDate) < 0) {
            IntervalLoadContainer interval = new IntervalLoadContainer();
            interval.setStart(dateIterator.toDateTime());
            interval.setWorkHours(user.getWeekEffort().getMinorUnits());
            dateIterator.addWeeks(1);
            interval.setEnd(dateIterator.toDateTime());
            ret.add(interval);
        }
        
        return ret;
    }

    public TaskDAO getTaskDAO() {
        return taskDAO;
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public StoryDAO getStoryDAO() {
        return storyDAO;
    }

    public void setStoryDAO(StoryDAO storyDAO) {
        this.storyDAO = storyDAO;
    }
}
