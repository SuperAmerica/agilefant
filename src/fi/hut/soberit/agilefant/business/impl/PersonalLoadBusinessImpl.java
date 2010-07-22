package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.PersonalLoadBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Schedulable;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.BacklogLoadContainer;
import fi.hut.soberit.agilefant.transfer.ComputedLoadData;
import fi.hut.soberit.agilefant.transfer.IntervalLoadContainer;
import fi.hut.soberit.agilefant.transfer.IterationLoadContainer;
import fi.hut.soberit.agilefant.transfer.ProjectLoadContainer;
import fi.hut.soberit.agilefant.transfer.UnassignedLoadTO;
import fi.hut.soberit.agilefant.transfer.UserLoadLimits;

@Service("personalLoadBusiness")
@Transactional(readOnly = true)
public class PersonalLoadBusinessImpl implements PersonalLoadBusiness {
    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private StoryDAO storyDAO;

    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    private IterationDAO iterationDAO;

    @Autowired
    private AssignmentDAO assignmentDAO;

    @Autowired
    private SettingBusiness settingBusiness;

    /**
     * Calculate sum of task effort left portions for given user per iteration
     * for tasks that don't have direct assignees, but have assignees for the
     * parent story.
     */
    public void calculateStoryAssignedTaskLoad(
            Map<Integer, IterationLoadContainer> iterationEffortData,
            User user, Interval interval) {
        List<Task> storyTasks = this.taskDAO
                .getStoryAssignedTasksWithEffortLeft(user, interval);

        Set<Integer> storyTaskStoryIds = new HashSet<Integer>();

        // get total responsibles per task
        for (Task task : storyTasks) {
            storyTaskStoryIds.add(task.getStory().getId());
        }
        Map<Integer, Integer> responsibleCounts = storyDAO
                .getNumOfResponsiblesByStory(storyTaskStoryIds);

        for (Task task : storyTasks) {
            Iteration iteration = (Iteration) task.getStory().getBacklog();
            int numberOfAssignees = responsibleCounts.get(task.getStory()
                    .getId());
            long taskEffort = divideAssignedTaskEffort(task, numberOfAssignees);
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
        assignedTasks.addAll(this.taskDAO.getIterationTasksWithEffortLeft(user,
                interval));
        assignedTasks.addAll(this.taskDAO.getStoryTasksWithEffortLeft(user,
                interval));

        Set<Integer> assignedTaskIds = new HashSet<Integer>();

        for (Task task : assignedTasks) {
            assignedTaskIds.add(task.getId());
        }
        Map<Integer, Integer> responsibleCounts = taskDAO
                .getNumOfResponsiblesByTask(assignedTaskIds);
        for (Task task : assignedTasks) {
            Iteration iteration = task.getIteration();
            if (iteration == null) {
                iteration = (Iteration) task.getStory().getBacklog();
            }
            int numberOfAssignees = responsibleCounts.get(task.getId());
            // divide task effort evenly per responsible
            long taskEffort = divideAssignedTaskEffort(task, numberOfAssignees);
            addTaskAssignedEffortToMap(iterationEffortData, iteration,
                    taskEffort);
        }
    }

    private long divideAssignedTaskEffort(Task task, int numberOfAssignees) {
        long taskEffort = 0;
        if (task.getEffortLeft() != null && numberOfAssignees != 0) {
            long taskEffortLeft = task.getEffortLeft().getMinorUnits();
            taskEffort = taskEffortLeft / numberOfAssignees;
        }
        return taskEffort;
    }

    public void calculateUnassignedTaskLoad(
            Map<Integer, IterationLoadContainer> iterationEffortData,
            User user, Interval interval) {
        // get raw load data
        List<UnassignedLoadTO> rawUnassignedLoad = new ArrayList<UnassignedLoadTO>();
        rawUnassignedLoad.addAll(this.taskDAO
                .getUnassignedIterationTasksWithEffortLeft(user, interval));
        rawUnassignedLoad.addAll(this.taskDAO
                .getUnassignedStoryTasksWithEffortLeft(user, interval));

        // get iterations
        loadIterationAvailabilitySums(rawUnassignedLoad);

        for (UnassignedLoadTO row : rawUnassignedLoad) {
            if (!iterationEffortData.containsKey(row.iterationId)) {
                IterationLoadContainer newContainer = new IterationLoadContainer();
                newContainer.setIteration(row.iteration);
                iterationEffortData.put(row.iterationId, newContainer);
            }
            double availabilityFactor = ((double) row.availability)
                    / ((double) row.availabilitySum);

            double effortFraction = 0.0;
            if (row.effortLeft != null) {
                effortFraction = availabilityFactor
                        * row.effortLeft.doubleValue();
            }
            IterationLoadContainer currentIter = iterationEffortData
                    .get(row.iterationId);
            currentIter.setTotalUnassignedLoad(currentIter
                    .getTotalUnassignedLoad()
                    + (long) effortFraction);
        }
    }

    public void calculateIterationFutureLoad(
            Map<Integer, IterationLoadContainer> iterationEffortData,
            User user, Interval interval) {
        List<Iteration> emptyIterations = this.iterationDAO
                .retrieveEmptyIterationsWithPlannedSize(interval.getStart(),
                        interval.getEnd(), user);
        Set<Integer> iterationIds = new HashSet<Integer>();
        for(Iteration iter : emptyIterations) {
            iterationIds.add(iter.getId());
        }
        
        Map<Integer, Integer> totalAvailabilities = this.iterationDAO
        .getTotalAvailability(iterationIds);
        
        for (Iteration iter : emptyIterations) {
            int availability = lookupAvailability(user, iter);
            int totalAvailability = totalAvailabilities.get(iter.getId());
            double fraction = (double)availability/(double)totalAvailability;
            if (!iterationEffortData.containsKey(iter.getId())) {
                IterationLoadContainer newContainer = new IterationLoadContainer();
                newContainer.setIteration(iter);
                iterationEffortData.put(iter.getId(), newContainer);
            }
            iterationEffortData.get(iter.getId()).setTotalFutureLoad(
                    (long)(fraction*(double)iter.getBacklogSize().longValue()));
        }
    }

    private int lookupAvailability(User user, Iteration iter) {
        for(Assignment assign : iter.getAssignments()) {
            if(assign.getUser() == user) {
                return assign.getAvailability();
            }
        }
        return 1;
    }

    /**
     * Sets iteration object and sum of each iterations assignment
     * availabilities to the transfer object.
     * 
     * @param rawUnassignedLoad
     *            Collection of UnassignedLoadTO transfer objects that each
     *            contain an iteration id.
     */
    public void loadIterationAvailabilitySums(
            List<UnassignedLoadTO> rawUnassignedLoad) {
        Set<Integer> iterationIds = new HashSet<Integer>();
        for (UnassignedLoadTO row : rawUnassignedLoad) {
            iterationIds.add(row.iterationId);
        }
        Collection<Iteration> iterations = this.iterationDAO.getMultiple(iterationIds);
        
        // get availability sums per iteration
        Map<Integer, Integer> totalAvailabilities = this.iterationDAO
                .getTotalAvailability(iterationIds);

        for (UnassignedLoadTO row : rawUnassignedLoad) {
            for (Iteration iter : iterations) {
                if (iter.getId() == row.iterationId) {
                    row.iteration = iter;
                }
            }
            if (totalAvailabilities.containsKey(row.iterationId)) {
                row.availabilitySum = totalAvailabilities.get(row.iterationId);
            } else {
                row.availabilitySum = 1;
            }
        }
    }

    /**
     * Combine directly and indirectly assigned task load.
     */
    public Map<Integer, IterationLoadContainer> calculateTotalAssignedUserLoad(
            User user, Interval interval) {
        Map<Integer, IterationLoadContainer> userLoadDataPerIteration = new HashMap<Integer, IterationLoadContainer>();
        this.calculateDirectlyAssignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        this.calculateStoryAssignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        this.calculateUnassignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        return userLoadDataPerIteration;
    }

    /**
     * Update load data in the container to account for the load from the given
     * iteration.
     */
    public void updateUserLoadByInterval(IntervalLoadContainer container,
            IterationLoadContainer load, User user) {
        Interval iterationInterval = new Interval(load.getIteration()
                .getStartDate(), load.getIteration().getEndDate());
        double fraction = calculateIntervalFraction(container.getInterval(),
                iterationInterval, user);

        double assignedEffortPortion = (double) load.getTotalAssignedLoad()
                * fraction;
        double unassignedEffortPortion = (double) load.getTotalUnassignedLoad()
                * fraction;
        double futureLoad = (double) load.getTotalFutureLoad() * fraction;

        container.setAssignedLoad(container.getAssignedLoad()
                + (long) assignedEffortPortion);
        container.setUnassignedLoad(container.getUnassignedLoad()
                + (long) unassignedEffortPortion);

        container.setFutureLoad(container.getFutureLoad() + (long) futureLoad);

        IterationLoadContainer perUserIterationLoad = new IterationLoadContainer();
        perUserIterationLoad.setIteration(load.getIteration());
        perUserIterationLoad.setTotalAssignedLoad((long) assignedEffortPortion);
        perUserIterationLoad
                .setTotalUnassignedLoad((long) unassignedEffortPortion);
        perUserIterationLoad.setTotalFutureLoad((long) futureLoad);
        container.getDetailedLoad().add(perUserIterationLoad);
    }

    private double calculateIntervalFraction(Interval containerInterval,
            Interval backlogInterval, User user) {
        
        if(backlogInterval.containsNow()) {
            backlogInterval = new Interval(new DateTime().withMillisOfSecond(0), backlogInterval.getEnd());
        }
        
        // iteration is not ongoing at this time
        if (!backlogInterval.overlaps(containerInterval)) {
            return 0.0;
        }
        
        Interval periodInterval = containerInterval.overlap(backlogInterval);

        // (work days in period / total work days in this iteration) * total
        // work
        Duration workTimeInBacklog = this.userBusiness
                .calculateWorktimePerPeriod(user, backlogInterval);
        Duration workTimeInPeriod = this.userBusiness
                .calculateWorktimePerPeriod(user, periodInterval);

        double fraction = (double) workTimeInPeriod.getMillis()
                / (double) workTimeInBacklog.getMillis();
        return fraction;
    }

    public List<IntervalLoadContainer> initializeLoadContainers(User user,
            DateTime startDate, DateTime endDate, Period periodLength) {
        List<IntervalLoadContainer> ret = new ArrayList<IntervalLoadContainer>();
        if (startDate.compareTo(endDate) > 0) {
            return Collections.emptyList();
        }
        MutableDateTime dateIterator = new MutableDateTime(startDate
                .toDateMidnight());
        while (dateIterator.isBefore(endDate)) {
            IntervalLoadContainer period = new IntervalLoadContainer();
            DateTime start = dateIterator.toDateTime();
            dateIterator.add(periodLength);
            period.setInterval(new Interval(start, dateIterator));
            ret.add(period);
        }

        return ret;
    }

    /**
     * Computes baseline load portions per interval.
     * 
     * Baseline load will not be added for vacations or weekends.
     */
    public void addBaselineLoad(ComputedLoadData preComputedLoad, User user,
            Interval interval) {
        List<IntervalLoadContainer> loadContainers = preComputedLoad
                .getLoadContainers();
        List<Assignment> assigments = this.assignmentDAO
                .assigmentsInBacklogTimeframe(interval, user);
        Map<Integer, Interval> assigmentIntervals = calculateAssigmentIntervals(assigments);
        for (IntervalLoadContainer intervalLoad : loadContainers) {
            for (Assignment assignment : assigments) {
                Interval assigmentBacklogInterval = assigmentIntervals
                        .get(assignment.getId());
                Interval assignmentWithinCurrentInterval = intervalLoad
                        .getInterval().overlap(assigmentBacklogInterval);
                if (assignmentWithinCurrentInterval != null) {
                    long dailyBaselineLoad = determinateWeeklyBaselineLoad(assignment) / 5;
                    Duration effectiveWorktime = this.userBusiness
                            .calculateWorktimePerPeriod(user,
                                    assignmentWithinCurrentInterval);
                    // from milliseconds to days
                    long exactDays = effectiveWorktime.getMillis() / 86400000;
                    long baselineLoadForInterval = dailyBaselineLoad
                            * exactDays;

                    intervalLoad.setBaselineLoad(intervalLoad.getBaselineLoad()
                            + baselineLoadForInterval);

                    BacklogLoadContainer backlogLoad = this
                            .getBacklogLoadContainerFromInterval(intervalLoad,
                                    assignment.getBacklog());

                    backlogLoad.setTotalBaselineLoad(backlogLoad
                            .getTotalBaselineLoad()
                            + baselineLoadForInterval);
                }
            }
        }
    }

    private BacklogLoadContainer getBacklogLoadContainerFromInterval(
            IntervalLoadContainer intervalLoad, Backlog backlog) {
        BacklogLoadContainer targetContainer = null;
        for (BacklogLoadContainer iterator : intervalLoad.getDetailedLoad()) {
            if (iterator.getBacklog().getId() == backlog.getId()) {
                targetContainer = iterator;
                break;
            }
        }
        if (targetContainer == null) {
            if (backlog instanceof Iteration) {
                IterationLoadContainer tmp = new IterationLoadContainer();
                tmp.setIteration((Iteration) backlog);
                targetContainer = tmp;
            } else if (backlog instanceof Project) {
                ProjectLoadContainer tmp = new ProjectLoadContainer();
                tmp.setProject((Project) backlog);
                targetContainer = tmp;
            }
            intervalLoad.getDetailedLoad().add(targetContainer);
        }
        return targetContainer;
    }

    private long determinateWeeklyBaselineLoad(Assignment assignment) {
        long baselineLoad = 0L;
        ExactEstimate backlogBaseline = null;
        if (assignment.getBacklog() instanceof Iteration) {
            backlogBaseline = ((Iteration) assignment.getBacklog())
                    .getBaselineLoad();
        } else if (assignment.getBacklog() instanceof Project) {
            backlogBaseline = ((Project) assignment.getBacklog())
                    .getBaselineLoad();
        }
        if (backlogBaseline != null) {
            baselineLoad = backlogBaseline.longValue();
        }

        if (assignment.getPersonalLoad() != null) {
            baselineLoad += assignment.getPersonalLoad().longValue();
        }
        return baselineLoad;
    }

    private Map<Integer, Interval> calculateAssigmentIntervals(
            List<Assignment> assigments) {
        Map<Integer, Interval> assigmentIntervals = new HashMap<Integer, Interval>();
        for (Assignment assigment : assigments) {
            Interval blInterval;
            Backlog backlog = assigment.getBacklog();
            if (backlog instanceof Schedulable) {
                Schedulable bl = (Schedulable) backlog;
                blInterval = new Interval(bl.getStartDate(), bl.getEndDate());
            } else {
                blInterval = new Interval(0);
            }
            assigmentIntervals.put(assigment.getId(), blInterval);
        }
        return assigmentIntervals;
    }

    public ComputedLoadData generatePersonalAssignedLoad(User user,
            DateTime startDate, DateTime endDate, Period len) {
        Interval interval = new Interval(startDate, endDate);
        Map<Integer, IterationLoadContainer> iterationEffortLeft = this
                .calculateTotalAssignedUserLoad(user, interval);
        this.calculateIterationFutureLoad(iterationEffortLeft, user, interval);
        List<IntervalLoadContainer> periods = this.initializeLoadContainers(
                user, startDate, endDate, len);
        ComputedLoadData loadData = new ComputedLoadData();
        loadData.setLoadContainers(periods);

        for (Integer iterationId : iterationEffortLeft.keySet()) {
            for (IntervalLoadContainer period : periods) {
                this.updateUserLoadByInterval(period, iterationEffortLeft
                        .get(iterationId), user);
            }
        }
        this.addBaselineLoad(loadData, user, interval);
        loadData.setStartDate(startDate);
        loadData.setEndDate(endDate);
        return loadData;
    }

    public ComputedLoadData retrieveUserLoad(User user, int weeksAhead) {
        Period len = new Period();
        len = len.plusDays(1);
        MutableDateTime startDate = new MutableDateTime();
        startDate.setMillisOfDay(0);

        DateTime start = startDate.toDateTime();
        DateTime end = start.plusWeeks(weeksAhead);
        return this.generatePersonalAssignedLoad(user, start, end, len);
    }

    public UserLoadLimits getDailyLoadLimitsByUser(User user) {
        UserLoadLimits limits = new UserLoadLimits();
        double userDailyMinutes;
        if (user.getWeekEffort() == null) {
            userDailyMinutes = 0.0;
        } else {
            userDailyMinutes = user.getWeekEffort().doubleValue() / 5.0;
        }
        double lowPercentage = (double) settingBusiness.getRangeLow() / 100.0;
        limits.setDailyLoadLow(userDailyMinutes * lowPercentage);
        double mediumPercentage = (double) settingBusiness.getOptimalLow() / 100.0;
        limits.setDailyLoadMedium(userDailyMinutes * mediumPercentage);
        double highPercentage = (double) settingBusiness.getOptimalHigh() / 100.0;
        limits.setDailyLoadHigh(userDailyMinutes * highPercentage);
        double criticalPercentage = (double) settingBusiness.getCriticalLow() / 100.0;
        limits.setDailyLoadCritical(userDailyMinutes * criticalPercentage);
        double maximumPercentage = (double) settingBusiness.getRangeHigh() / 100.0;
        limits.setDailyLoadMaximum(userDailyMinutes * maximumPercentage);
        return limits;
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

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public void setAssignmentDAO(AssignmentDAO assignmentDAO) {
        this.assignmentDAO = assignmentDAO;
    }

    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }
}
