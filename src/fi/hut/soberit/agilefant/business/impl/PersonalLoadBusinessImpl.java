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
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.PersonalLoadBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.IntervalLoadContainer;
import fi.hut.soberit.agilefant.util.IterationLoadContainer;

@Service("personalLoadBusiness")
@Transactional
public class PersonalLoadBusinessImpl implements PersonalLoadBusiness {
    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private StoryDAO storyDAO;
    
    @Autowired
    private UserBusiness userBusiness;

    /**
     * Calculate sum of task effort left portions for given user per iteration
     * for tasks that don't have direct assignees, but have assignees for the
     * parent story.
     */
    public void calculateStoryAssignedTaskLoad(
            Map<Integer, IterationLoadContainer> iterationEffortData,
            User user, Interval interval) {
        List<Task> storyTasks = this.taskDAO
                .getUnassignedTasksByStoryResponsibles(user, interval);

        Set<Integer> storyTaskStoryIds = new HashSet<Integer>();

        // get total responsibles per task
        for (Task task : storyTasks) {
            storyTaskStoryIds.add(task.getStory().getId());
        }
        Map<Integer, Integer> responsibleCounts = storyDAO
                .getNumOfResponsiblesByStory(storyTaskStoryIds);

        for (Task task : storyTasks) {
            Iteration iteration = (Iteration) task.getStory().getBacklog();
            long taskEffort = 0;
            int numberOfAssignees = responsibleCounts.get(task.getStory().getId());
            // divide task effort evenly per responsible
            if (task.getEffortLeft() != null && numberOfAssignees != 0) {
                long taskEffortLeft = task.getEffortLeft().getMinorUnits();
                taskEffort = taskEffortLeft / numberOfAssignees;
            }
            // add to the iteration total sum
            addTaskAssignedEffortToMap(iterationEffortData, iteration,
                    taskEffort);
        }
    }

    private void addTaskAssignedEffortToMap(
            Map<Integer, IterationLoadContainer> iterationEffortData,
            Iteration iteration, long taskEffort) {
        if (!iterationEffortData.containsKey(iteration.getId())) {
            IterationLoadContainer newContainer = new IterationLoadContainer();
            newContainer.setIteration(iteration);
            iterationEffortData.put(iteration.getId(), newContainer);
        }
        long iterationTotal = taskEffort
                + iterationEffortData.get(iteration.getId())
                        .getTotalAssignedLoad();
        iterationEffortData.get(iteration.getId()).setTotalAssignedLoad(
                iterationTotal);
    }

    /**
     * Calculate sum of task effort left portions for given user per iteration
     * for tasks that have direct assignees.
     */
    public void calculateDirectlyAssignedTaskLoad(
            Map<Integer, IterationLoadContainer> iterationEffortData,
            User user, Interval interval) {
        List<Task> assignedTasks = new ArrayList<Task>();
        assignedTasks.addAll(this.taskDAO.getIterationTasksByUserAndTimeframe(
                user, interval));
        assignedTasks.addAll(this.taskDAO.getStoryTasksByUserAndTimeframe(user,
                interval));

        Set<Integer> assignedTaskIds = new HashSet<Integer>();

        for (Task task : assignedTasks) {
            assignedTaskIds.add(task.getId());
        }
        Map<Integer, Integer> responsibleCounts = taskDAO
                .getNumOfResponsiblesByTask(assignedTaskIds);
        for (Task task : assignedTasks) {
            Iteration iteration = task.getIteration();
            if(iteration == null) {
                iteration = (Iteration)task.getStory().getBacklog();
            }
            long taskEffort = 0;
            int numberOfAssignees = responsibleCounts.get(task.getId());
            // divide task effort evenly per responsible
            if (task.getEffortLeft() != null && numberOfAssignees != 0) {
                long taskEffortLeft = task.getEffortLeft().getMinorUnits();
                taskEffort = taskEffortLeft / numberOfAssignees;
            }
            addTaskAssignedEffortToMap(iterationEffortData, iteration,
                    taskEffort);
        }
    }

    /**
     * Combine assigned load
     */
    public Map<Integer, IterationLoadContainer> calculateTotalUserLoad(User user,
            Interval interval) {
        Map<Integer, IterationLoadContainer> userLoadDataPerIteration = new HashMap<Integer, IterationLoadContainer>();
        this.calculateDirectlyAssignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        this.calculateStoryAssignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        return userLoadDataPerIteration;
    }

    /**
     * Update load data in the container to account for the load 
     * from the given iteration.
     */
    public void updateUserLoadByInterval(IntervalLoadContainer container, 
            IterationLoadContainer load, User user) {
        DateTime periodStart = container.getInterval().getStart();
        DateTime periodEnd = container.getInterval().getEnd();
        DateTime iterationStart = new DateTime(load.getIteration().getStartDate());
        DateTime iterationEnd = new DateTime(load.getIteration().getEndDate());
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
        Duration workTimeInIteration = this.userBusiness.calculateWorktimePerPeriod(user,
                iterationInterval);
        Duration workTimeInPeriod = this.userBusiness.calculateWorktimePerPeriod(user,
                periodInterval);

        double fraction = (double) workTimeInPeriod.getMillis()
                / (double) workTimeInIteration.getMillis();

        double effortPortion = (double) load.getTotalAssignedLoad() * fraction;
        container.setAssignedLoad(container.getAssignedLoad()
                + (long) effortPortion);
    }

    public List<IntervalLoadContainer> initializeLoadContainers(
            User user, DateTime startDate, DateTime endDate, Period periodLength) {
        List<IntervalLoadContainer> ret = new ArrayList<IntervalLoadContainer>();
        if (startDate.compareTo(endDate) > 0) {
            return Collections.emptyList();
        }
        MutableDateTime dateIterator = new MutableDateTime(startDate.toDateMidnight());
        while (dateIterator.isBefore(endDate)) {
            IntervalLoadContainer period = new IntervalLoadContainer();
            DateTime start = dateIterator.toDateTime();
            dateIterator.add(periodLength);
            period.setInterval(new Interval(start, dateIterator));
            ret.add(period);
        }

        return ret;
    }

    public List<IntervalLoadContainer> generatePersonalAssignedLoad(User user,
            DateTime startDate, DateTime endDate) {
        Interval interval = new Interval(startDate, endDate);
        Map<Integer, IterationLoadContainer> iterationEffortLeft = this
                .calculateTotalUserLoad(user, interval);
        Period len = new Period(0);
        List<IntervalLoadContainer> periods = this
                .initializeLoadContainers(user, startDate, endDate, len.plusWeeks(1));
        for (Integer iterationId : iterationEffortLeft.keySet()) {
            for (IntervalLoadContainer period : periods) {
                this.updateUserLoadByInterval(period, iterationEffortLeft.get(iterationId), user);
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

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }
}
