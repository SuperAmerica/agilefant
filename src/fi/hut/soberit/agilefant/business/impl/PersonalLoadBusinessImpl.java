package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.joda.time.MutableDateTime;
import org.joda.time.Period;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.PersonalLoadBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Schedulable;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ComputedLoadData;
import fi.hut.soberit.agilefant.transfer.IntervalLoadContainer;
import fi.hut.soberit.agilefant.transfer.IterationLoadContainer;
import fi.hut.soberit.agilefant.transfer.UnassignedLoadTO;

@Service("personalLoadBusiness")
@Transactional(readOnly=true)
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
            int numberOfAssignees = responsibleCounts.get(task.getStory().getId());
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
        assignedTasks.addAll(this.taskDAO.getIterationTasksWithEffortLeft(
                user, interval));
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
            if(iteration == null) {
                iteration = (Iteration)task.getStory().getBacklog();
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
        loadIterationDetails(rawUnassignedLoad);

        for (UnassignedLoadTO row : rawUnassignedLoad) {
            if (!iterationEffortData.containsKey(row.iterationId)) {
                IterationLoadContainer newContainer = new IterationLoadContainer();
                newContainer.setIteration(row.iteration);
                iterationEffortData.put(row.iterationId, newContainer);
            }
            double availabilityFactor = (double) row.availability
                    / (double) row.availabilitySum;

            double effortFraction = availabilityFactor
                    * row.effortLeft.doubleValue();
            IterationLoadContainer currentIter = iterationEffortData
                    .get(row.iterationId);
            currentIter.setTotalUnassignedLoad(currentIter
                    .getTotalUnassignedLoad()
                    + (long) effortFraction);
        }
    }

    /**
     * Sets iteration object and sum of each iterations assignment
     * availabilities to the transfer object.
     * 
     * @param rawUnassignedLoad
     *            Collection of UnassignedLoadTO transfer objects that each
     *            contain an iteration id.
     */
    public void loadIterationDetails(
            List<UnassignedLoadTO> rawUnassignedLoad) {
        Set<Integer> iterationIds = new HashSet<Integer>();
        for (UnassignedLoadTO row : rawUnassignedLoad) {
            iterationIds.add(row.iterationId);
        }
        List<Iteration> iterations = this.iterationDAO
                .retrieveIterationsByIds(iterationIds);
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
     * Combine assigned load
     */
    public Map<Integer, IterationLoadContainer> calculateTotalUserLoad(User user,
            Interval interval) {
        Map<Integer, IterationLoadContainer> userLoadDataPerIteration = new HashMap<Integer, IterationLoadContainer>();
        this.calculateDirectlyAssignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        this.calculateStoryAssignedTaskLoad(userLoadDataPerIteration, user,
                interval);
        this.calculateUnassignedTaskLoad(userLoadDataPerIteration, user, interval);
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

        double assignedEffortPortion = (double) load.getTotalAssignedLoad() * fraction;
        double unassignedEffortPortion = (double) load.getTotalUnassignedLoad() * fraction;
        container.setAssignedLoad(container.getAssignedLoad()
                + (long) assignedEffortPortion);
        container.setUnassignedLoad(container.getUnassignedLoad()
                + (long) unassignedEffortPortion);
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
            for (Assignment assigment : assigments) {
                Interval assigmentInterval = assigmentIntervals.get(assigment
                        .getId());
                if (intervalLoad.getInterval().overlaps(assigmentInterval)) {
                    // NOTE: assumes 5-day-week
                    long baselineLoadPerDay = assigment.getPersonalLoad()
                            .longValue() / 5;
                    //get intersection of current interval and assignment's backlog's timeframe 
                    Interval overlap = intervalLoad.getInterval().overlap(assigmentInterval);
                    //get number of workdays within this intersection
                    Duration workdays = this.userBusiness.calculateWorktimePerPeriod(user, overlap);
                    int days = (int)(workdays.getStandardSeconds()/(3600*24));
                    long baselineLoadForInterval = days * baselineLoadPerDay;
                    intervalLoad.setBasellineLoad(intervalLoad
                            .getBasellineLoad()
                            + baselineLoadForInterval);
                    if (assigment.getBacklog() instanceof Iteration) {
                        preComputedLoad.getIterations().add(
                                (Iteration) assigment.getBacklog());
                    } else if (assigment.getBacklog() instanceof Project) {
                        preComputedLoad.getProjects().add(
                                (Project) assigment.getBacklog());

                    }
                }

            }
        }
    }

    private Map<Integer, Interval> calculateAssigmentIntervals(List<Assignment> assigments) {
        Map<Integer, Interval> assigmentIntervals = new HashMap<Integer, Interval>();
        for(Assignment assigment : assigments) {
            Interval blInterval;
            Backlog backlog = assigment.getBacklog();
            if(backlog instanceof Schedulable) {
                Schedulable bl = (Schedulable)backlog;
                blInterval = new Interval(new DateTime(bl.getStartDate()), new DateTime(bl.getEndDate()));
            } else {
                blInterval = new Interval(0);
            }
            assigmentIntervals.put(assigment.getId(), blInterval);
        }
        return assigmentIntervals;
    }
     
    public Set<Iteration> extractIterations(Map<Integer, IterationLoadContainer> iterationEffortLeft) {
        Set<Iteration> iterations = new HashSet<Iteration>();
        for(IterationLoadContainer container : iterationEffortLeft.values()) {
            iterations.add(container.getIteration());
        }
        return iterations;
    }
    

    public ComputedLoadData generatePersonalAssignedLoad(User user,
            DateTime startDate, DateTime endDate, Period len) {
        Interval interval = new Interval(startDate, endDate);
        Map<Integer, IterationLoadContainer> iterationEffortLeft = this
                .calculateTotalUserLoad(user, interval);
        List<IntervalLoadContainer> periods = this
                .initializeLoadContainers(user, startDate, endDate, len);
        ComputedLoadData loadData = new ComputedLoadData();
        loadData.setLoadContainers(periods);
        
        Set<Iteration> iterations = this.extractIterations(iterationEffortLeft);
        for (Integer iterationId : iterationEffortLeft.keySet()) {
            for (IntervalLoadContainer period : periods) {
                this.updateUserLoadByInterval(period, iterationEffortLeft.get(iterationId), user);
            }
        }
        this.addBaselineLoad(loadData, user, interval);
        for(Iteration iteration : iterations) {
            loadData.getProjects().add((Project)iteration.getParent());
        }
        loadData.setIterations(iterations);
        loadData.setStartDate(startDate.toDate());
        loadData.setEndDate(endDate.toDate());
        return loadData;
    }
    
    public ComputedLoadData retrieveUserLoad(User user, int weeksAhead) {
        Period len = new Period();
        len = len.plusDays(1);
        MutableDateTime currentWeekStart = new MutableDateTime();
        currentWeekStart.setDayOfWeek(DateTimeConstants.MONDAY);
        currentWeekStart.setMillisOfDay(0);
        
        DateTime start = currentWeekStart.toDateTime();
        DateTime end = start.plusWeeks(weeksAhead);
        return this.generatePersonalAssignedLoad(user, start, end, len);
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
}
