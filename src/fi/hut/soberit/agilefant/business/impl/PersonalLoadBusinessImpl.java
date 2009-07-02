package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.PersonalLoadBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
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

    /*
     * Calculate sum of task effort left portions for given user per iteration
     * for tasks that don't have direct assignees, but have assignees for the
     * parent story.
     */
    protected void calculateStoryAssignedTaskLoad(
            Map<Backlog, Long> iterationEffortLeft, User user, Interval interval) {
        List<Task> storyTasks = this.taskDAO
                .getUnassignedTasksByStoryResponsibles(user, interval);

        Set<Integer> storyTaskStoryIds = new HashSet<Integer>();

        for (Task task : storyTasks) {
            storyTaskStoryIds.add(task.getStory().getId());
        }

        Map<Integer, Integer> responsibleCounts = storyDAO
                .getNumOfResponsiblesByStory(storyTaskStoryIds);
        for (Task task : storyTasks) {
            long taskEffort = 0;
            if (task.getEffortLeft() != null
                    && responsibleCounts.get(task.getId()) != null) {
                long taskEffortLeft = task.getEffortLeft().getMinorUnits();
                int numberOfAssignees = responsibleCounts.get(task.getId());
                taskEffort = taskEffortLeft / numberOfAssignees;
            }
            if (!iterationEffortLeft.containsKey(task.getStory().getBacklog())) {
                iterationEffortLeft
                        .put(task.getStory().getBacklog(), (Long) 0L);
            }
            iterationEffortLeft.put(task.getStory().getBacklog(), taskEffort);
        }
    }

    /*
     * Calculate sum of task effort left portions for given user per iteration
     * for tasks that have direct assignees.
     */
    protected void calculateDirectlyAssignedTaskLoad(
            Map<Backlog, Long> iterationEffortLeft, User user, Interval interval) {
        List<Task> assignedTasks = this.taskDAO
                .getIterationTasksByUserAndTimeframe(user, interval);
        assignedTasks.addAll(this.taskDAO.getStoryTasksByUserAndTimeframe(user,
                interval));

        Set<Integer> assignedTaskIds = new HashSet<Integer>();

        for (Task task : assignedTasks) {
            assignedTaskIds.add(task.getId());
        }
        Map<Integer, Integer> responsibleCounts = taskDAO
                .getNumOfResponsiblesByTask(assignedTaskIds);
        for (Task task : assignedTasks) {
            long taskEffort = 0;
            if (task.getEffortLeft() != null
                    && responsibleCounts.get(task.getId()) != null) {
                long taskEffortLeft = task.getEffortLeft().getMinorUnits();
                int numberOfAssignees = responsibleCounts.get(task.getId());
                taskEffort = taskEffortLeft / numberOfAssignees;
            }
            if (!iterationEffortLeft.containsKey(task.getIteration())) {
                iterationEffortLeft.put(task.getIteration(), (Long) 0L);
            }
            iterationEffortLeft.put(task.getIteration(), taskEffort);
        }
    }

    /*
     * Combine assigned load
     */
    public Map<Backlog, Long> calculateTotalAssignedLoad(User user,
            Interval interval) {
        Map<Backlog, Long> userLoadDataPerIteration = new HashMap<Backlog, Long>();
        this.calculateDirectlyAssignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        this.calculateStoryAssignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        return userLoadDataPerIteration;
    }

    /*
     * Duration object representing total (days) that the given user can work
     * within the given timeframe.
     */
    public Duration calculateWorktimePerPeriod(User user, Interval interval) {

        return null;
    }

    public void updateUserLoadByInterval(IntervalLoadContainer container,
            Iteration iter, User user, long assignedEffort) {
        DateTime periodStart = container.getStart();
        DateTime periodEnd = container.getEnd();
        DateTime iterationStart = new DateTime(iter.getStartDate());
        DateTime iterationEnd = new DateTime(iter.getEndDate());
        Interval iterationInterval = new Interval(iterationStart, iterationEnd);
        Interval periodInterval = new Interval(periodStart, periodEnd);

        // iteration is not ongoing at this time
        if (!iterationInterval.overlaps(periodInterval)) {
            return;
        }

        if (periodStart.isBefore(iterationStart)
                && periodEnd.isAfter(iterationEnd)) {
            // iteration is shorter that the period
            periodStart = iterationStart;
            periodEnd = iterationEnd;

        } else if (periodStart.isBefore(iterationStart)
                && periodEnd.isAfter(iterationStart)) {
            // iteration begins within the period
            periodStart = iterationStart;

        } else if (periodStart.isBefore(iterationEnd)
                && periodEnd.isAfter(iterationEnd)) {
            // iteration ends within the period
            periodEnd = iterationEnd;
        }
        periodInterval = new Interval(periodStart, periodEnd);
        // (work days in period / total work days in this iteration) * total
        // work
        Duration workTimeInIteration = this.calculateWorktimePerPeriod(user,
                iterationInterval);
        Duration workTimeInPeriod = this.calculateWorktimePerPeriod(user,
                periodInterval);

        double fraction = (double) workTimeInPeriod.getMillis()
                / (double) workTimeInIteration.getMillis();

        double effortPortion = (double) assignedEffort * fraction;
        container.setAssignedLoad(container.getAssignedLoad()
                + (long) effortPortion);
    }

    public List<IntervalLoadContainer> calculateAvailableMinutesPerWeek(
            User user, DateTime startDate, DateTime endDate) {
        List<IntervalLoadContainer> ret = new ArrayList<IntervalLoadContainer>();
        if (startDate.compareTo(endDate) > 0) {
            return Collections.emptyList();
        }
        MutableDateTime dateIterator = new MutableDateTime(startDate);
        while (startDate.compareTo(endDate) < 0) {
            IntervalLoadContainer interval = new IntervalLoadContainer();
            interval.setStart(dateIterator.toDateTime());
            interval.setWorkHours(user.getWeekEffort().getMinorUnits());
            dateIterator.addWeeks(1);
            interval.setEnd(dateIterator.toDateTime());
            ret.add(interval);
        }

        return ret;
    }

    public List<IntervalLoadContainer> generatePersonalAssignedLoad(User user,
            DateTime startDate, DateTime endDate) {
        Interval interval = new Interval(startDate, endDate);
        Map<Backlog, Long> iterationEffortLeft = this
                .calculateTotalAssignedLoad(user, interval);
        List<IntervalLoadContainer> periods = this
                .calculateAvailableMinutesPerWeek(user, startDate, endDate);
        for (Backlog bl : iterationEffortLeft.keySet()) {
            Iteration iter = (Iteration) bl;
            for (IntervalLoadContainer period : periods) {
                this.updateUserLoadByInterval(period, iter, user,
                        iterationEffortLeft.get(bl));
            }
        }
        return periods;
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
