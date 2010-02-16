package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;
import fi.hut.soberit.agilefant.transfer.IterationRowMetrics;
import fi.hut.soberit.agilefant.model.Iteration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.AssignmentBusiness;
import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryRankBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationHistoryEntryDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.AssignmentTO;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.transfer.IterationTO;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;
import fi.hut.soberit.agilefant.util.Pair;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import fi.hut.soberit.agilefant.util.TaskHandlingChoice;

@Service("iterationBusiness")
@Transactional
public class IterationBusinessImpl extends GenericBusinessImpl<Iteration>
        implements IterationBusiness {

    private IterationDAO iterationDAO;

    @Autowired
    private TransferObjectBusiness transferObjectBusiness;
    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    @Autowired
    private BacklogHistoryEntryBusiness backlogHistoryEntryBusiness;
    @Autowired
    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness;
    @Autowired
    private IterationHistoryEntryDAO iterationHistoryEntryDAO;
    @Autowired
    private BacklogBusiness backlogBusiness;
    @Autowired
    private AssignmentBusiness assignmentBusiness;
    @Autowired
    private SettingBusiness settingBusiness;
    @Autowired 
    private StoryRankBusiness storyRankBusiness;
    @Autowired
    private TaskBusiness taskBusiness;

    public IterationBusinessImpl() {
        super(Iteration.class);
    }
    
    @Autowired
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.genericDAO = iterationDAO;
        this.iterationDAO = iterationDAO;
    }

    @Override
    public void delete(int id) {
        delete(retrieve(id));
    }
    
    
    public void deleteAndUpdateHistory(int id) {
        Iteration iteration = retrieve(id);
        Backlog project = iteration.getParent();
        delete(iteration);
        backlogHistoryEntryBusiness.updateHistory(project.getId());      
    }

    @Override
    public void delete(Iteration iteration) {
            Set<Task> tasks = new HashSet<Task>(iteration.getTasks());
            for (Task item : tasks) {
                taskBusiness.delete(item.getId(), HourEntryHandlingChoice.DELETE);
            }
            
            Set<Story> stories = new HashSet<Story>(iteration.getStories());
            TaskHandlingChoice taskHandlingChoice = TaskHandlingChoice.DELETE;
            HourEntryHandlingChoice storyHourEntryHandlingChoice = HourEntryHandlingChoice.DELETE;
            HourEntryHandlingChoice taskHourEntryHandlingChoice = HourEntryHandlingChoice.DELETE;
            for (Story item : stories) {
                storyBusiness.delete(item, taskHandlingChoice,
                        storyHourEntryHandlingChoice, taskHourEntryHandlingChoice);
            }
            Set<Assignment> assignments = new HashSet<Assignment>(iteration.getAssignments());
            for (Assignment item : assignments) {
                assignmentBusiness.delete(item.getId());
            }
            
            hourEntryBusiness.deleteAll(iteration.getHourEntries());
            
            iteration.getHourEntries().clear();
            
            Set<IterationHistoryEntry> historyEntries = new HashSet<IterationHistoryEntry>(iteration.getHistoryEntries());
            for (IterationHistoryEntry item : historyEntries) {
                iterationHistoryEntryBusiness.delete(item.getId());
            }
            super.delete(iteration);
    }
    

    @Transactional(readOnly = true)
    public IterationTO getIterationContents(int iterationId) {
        Iteration iteration = this.iterationDAO.retrieveDeep(iterationId);
        if (iteration == null) {
            throw new ObjectNotFoundException("Iteration not found");
        }
        IterationTO iterationTO = transferObjectBusiness.constructIterationTO(iteration);

        List<Story> stories = this.storyRankBusiness
                .retrieveByRankingContext(iteration);
        Map<Integer, StoryMetrics> metricsData = this.iterationDAO
                .calculateIterationDirectStoryMetrics(iteration);
        int rank = 0;
        List<StoryTO> rankedStories = new ArrayList<StoryTO>();

        HashMap<Integer, Story> tmp = new HashMap<Integer, Story>();
        for (Story story : iteration.getStories()) {
            tmp.put(story.getId(), story);
        }
        for (Story story : stories) {
            StoryTO storyTO = new StoryTO(tmp.get(story.getId()));
            if (metricsData.containsKey(story.getId())) {
                storyTO.setMetrics(metricsData.get(story.getId()));
            }
            storyTO.setRank(rank++);
            rankedStories.add(storyTO);
        }
        iterationTO.setRankedStories(rankedStories);
        // Set the tasks without a story
        Collection<Task> tasksWithoutStory = iterationDAO
                .getTasksWithoutStoryForIteration(iteration);

        iterationTO.setTasks(new HashSet<Task>());

        Map<Integer, Long> taskEsData = this.iterationDAO
                .calculateIterationTaskEffortSpent(iteration);

        for (Task task : tasksWithoutStory) {
            TaskTO taskTO = new TaskTO(task);
            iterationTO.getTasks().add(taskTO);
            if (taskEsData.containsKey(taskTO.getId())) {
                taskTO.setEffortSpent(taskEsData.get(taskTO.getId()));
            }
        }

        for (Story story : iterationTO.getRankedStories()) {
            Set<Task> tasks = new HashSet<Task>();
            for (Task task : story.getTasks()) {
                TaskTO taskTO = new TaskTO(task);
                if (taskEsData.containsKey(taskTO.getId())) {
                    taskTO.setEffortSpent(taskEsData.get(taskTO.getId()));
                }
                tasks.add(taskTO);
            }
            story.setTasks(tasks);
        }

        return iterationTO;
    }

    public ExactEstimate calculateDailyVelocity(LocalDate start,
            IterationHistoryEntry yesterdayEntry) {
        LocalDate today = new LocalDate();

        if (yesterdayEntry == null)
            return new ExactEstimate(0);

        double length = Days.daysBetween(start, today).getDays();
        if (length < 1) {
            length = 1;
        }

        long origEst = yesterdayEntry.getOriginalEstimateSum();
        long effLeft = yesterdayEntry.getEffortLeftSum();

        double velocity = (origEst - effLeft) / length;

        return new ExactEstimate((long) velocity);
    }

    public ExactEstimate calculateDailyVelocity(Iteration iteration) {
        LocalDate today = new LocalDate();
        IterationHistoryEntry entry = iterationHistoryEntryDAO.retrieveByDate(
                iteration.getId(), today.minusDays(1));
        return calculateDailyVelocity(new LocalDate(iteration.getStartDate()),
                entry);
    }

    private Integer calculatePercent(Integer part, Integer total) {
        return Math.round(100.0f * part / total);
    }

    public IterationMetrics getIterationMetrics(Iteration iteration) {
        if (iteration == null) {
            throw new IllegalArgumentException("Iteration must be not null.");
        }

        IterationMetrics metrics = new IterationMetrics();

        IterationHistoryEntry latestHistoryEntry = iterationHistoryEntryBusiness
                .retrieveLatest(iteration);

        // 1. Set original estimate and effort left
        if (latestHistoryEntry == null) {
            metrics.setOriginalEstimate(new ExactEstimate(0));
            metrics.setEffortLeft(new ExactEstimate(0));
        } else {
            metrics.setOriginalEstimate(new ExactEstimate(latestHistoryEntry
                    .getOriginalEstimateSum()));
            metrics.setEffortLeft(new ExactEstimate(latestHistoryEntry
                    .getEffortLeftSum()));
        }

        metrics.setDailyVelocity(calculateDailyVelocity(iteration));

        // 2. Set story points
        metrics.setStoryPoints(storyBusiness
                .getStoryPointSumByBacklog(iteration));

        // 3. Set spent effort
        long spentEffort = hourEntryBusiness
                .calculateSumOfIterationsHourEntries(iteration);
        metrics.setSpentEffort(new ExactEstimate(spentEffort));

        // 3. Tasks done and Total
        Pair<Integer, Integer> pair = iterationDAO
                .getCountOfDoneAndAllTasks(iteration);
        metrics.setTotalTasks(pair.second);
        metrics.setCompletedTasks(pair.first);
        metrics.setPercentDoneTasks(calculatePercent(pair.first, pair.second));

        pair = iterationDAO.getCountOfDoneAndAllStories(iteration);
        metrics.setTotalStories(pair.second);
        metrics.setCompletedStories(pair.first);
        metrics
                .setPercentDoneStories(calculatePercent(pair.first, pair.second));

        return metrics;
    }

    public IterationTO store(int iterationId, int parentBacklogId,
            Iteration iterationData, Set<Integer> assigneeIds) {
        Backlog parent = null;
        if(parentBacklogId != 0) {
            parent = this.backlogBusiness.retrieve(parentBacklogId);
        }
        if (parent == null && iterationId == 0) {
            throw new IllegalArgumentException("Invalid parent.");
        }
        if (parent instanceof Iteration) {
            throw new IllegalArgumentException(
                    "Nested iterations are not allowed.");
        }
        if (iterationData.getEndDate().isBefore(iterationData.getStartDate())) {
            throw new IllegalArgumentException("End date before start date");
        }
        if (iterationId == 0) {
            return transferObjectBusiness.constructIterationTO(this.create(parent, iterationData, assigneeIds));
        }
        Iteration iter = this.retrieve(iterationId);
        iter.setStartDate(iterationData.getStartDate());
        iter.setEndDate(iterationData.getEndDate());
        iter.setBacklogSize(iterationData.getBacklogSize());
        iter.setBaselineLoad(iterationData.getBaselineLoad());
        iter.setDescription(iterationData.getDescription());
        iter.setName(iterationData.getName());
        setAssignees(iter, assigneeIds);
        this.iterationDAO.store(iter);
        if (parent != null && iter.getParent() != parent) {
            this.moveTo(iter, parent);
        }
        return transferObjectBusiness.constructIterationTO(iter);
    }

    private Iteration create(Backlog parentBacklog, Iteration iterationData, Set<Integer> assigneeIds) {
        iterationData.setParent(parentBacklog);
        int iterationId = (Integer) this.iterationDAO.create(iterationData);
        Iteration iter = this.retrieve(iterationId);
        
        setAssignees(iter, assigneeIds);
        return iter;
    }
    
    private void setAssignees(Iteration iteration, Set<Integer> assigneeIds) {
        if (assigneeIds != null) {
            for(Assignment assignment : iteration.getAssignments()) {
                if(!assigneeIds.contains(assignment.getUser().getId())) {
                    assignmentBusiness.delete(assignment.getId());
                }
            }
            assignmentBusiness.addMultiple(iteration, assigneeIds);
        }
    }

    public void moveTo(Iteration iter, Backlog parent) {
        Backlog oldParent = iter.getParent();
        iter.setParent(parent);
        this.iterationDAO.store(iter);
        if (oldParent instanceof Project) {
            this.backlogHistoryEntryBusiness.updateHistory(oldParent.getId());
        }
        if (parent instanceof Project) {
            this.backlogHistoryEntryBusiness.updateHistory(parent.getId());
        }
    }

    public Collection<Iteration> retrieveCurrentAndFutureIterations() {
        DateTime now = new DateTime();
        DateTime dayStart = now.withMillisOfDay(0);

        return iterationDAO.retrieveCurrentAndFutureIterationsAt(dayStart);
    }
    
    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void setIterationHistoryEntryBusiness(
            IterationHistoryEntryBusiness iterationHistoryEntryBusiness) {
        this.iterationHistoryEntryBusiness = iterationHistoryEntryBusiness;
    }

    public void setIterationHistoryEntryDAO(
            IterationHistoryEntryDAO iterationHistoryEntryDAO) {
        this.iterationHistoryEntryDAO = iterationHistoryEntryDAO;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setAssignmentBusiness(AssignmentBusiness assignmentBusiness) {
        this.assignmentBusiness = assignmentBusiness;
    }

    public void setBacklogHistoryEntryBusiness(
            BacklogHistoryEntryBusiness backlogHistoryEntryBusiness) {
        this.backlogHistoryEntryBusiness = backlogHistoryEntryBusiness;
    }
    
    public void setSettingBusiness(SettingBusiness settingBusiness) {
        this.settingBusiness = settingBusiness;
    }
    
    public void setStoryRankBusiness(StoryRankBusiness storyRankBusiness) {
        this.storyRankBusiness = storyRankBusiness;
    }
    
    @Transactional(readOnly = true)
    public IterationRowMetrics getIterationRowMetrics(int iterationId) {
        Iteration iteration = retrieve(iterationId);
        LocalDate today = new LocalDate();
        IterationRowMetrics iterationRowMetrics = new IterationRowMetrics();
        
        // Timesheets enabled?
        iterationRowMetrics.setTimesheetsEnabled(settingBusiness.isHourReportingEnabled());
        iterationRowMetrics.setStateData(iterationDAO.countIterationStoriesByState(iterationId));
        iterationRowMetrics.setTotalDays(Days.daysBetween(iteration.getStartDate(), iteration.getEndDate()).getDays());
        
        if (today.isBefore(iteration.getEndDate().toLocalDate())) {
            iterationRowMetrics.setDaysLeft(daysLeftInIteration(iteration).getDays());
        }
        
        IterationHistoryEntry latestHistoryEntry = iterationHistoryEntryBusiness
        .retrieveLatest(iteration);
        
        if (latestHistoryEntry == null) {
            iterationRowMetrics.setOriginalEstimate(new ExactEstimate(0));
            iterationRowMetrics.setEffortLeft(new ExactEstimate(0));
        } else {
            iterationRowMetrics.setOriginalEstimate(new ExactEstimate(latestHistoryEntry
                    .getOriginalEstimateSum()));
            iterationRowMetrics.setEffortLeft(new ExactEstimate(latestHistoryEntry
                    .getEffortLeftSum()));
        }

        // Set spent effort
        long spentEffort = hourEntryBusiness
                .calculateSumOfIterationsHourEntries(iteration);
        iterationRowMetrics.setSpentEffort(new ExactEstimate(spentEffort));
        // Set variance
        iterationRowMetrics.setVariance(calculateVariance(iteration));
  
        return iterationRowMetrics;
    }
    
    public Set<AssignmentTO> calculateAssignedLoadPerAssignee(Iteration iter) {
        List<Task> iterationTasks = this.iterationDAO
                .getAllTasksForIteration(iter);

        Map<Integer, AssignmentTO> assignments = new HashMap<Integer, AssignmentTO>();
        long unassignedLoad = 0L;
        int totalAvailability = 0;

        for (Assignment assignment : iter.getAssignments()) {
            AssignmentTO to = new AssignmentTO(assignment);
            assignments.put(assignment.getUser().getId(), to);
            totalAvailability += assignment.getAvailability();
        }
        if (totalAvailability == 0) {
            totalAvailability = 1;
        }

        for (Task task : iterationTasks) {
            int responsibleCount = task.getResponsibles().size();
            long taskEffort = (task.getEffortLeft() != null) ? task
                    .getEffortLeft().longValue() : 0;
            if (responsibleCount != 0) {
                Collection<User> responsibles = task.getResponsibles();
                divideTaskLoad(assignments, responsibleCount, taskEffort,
                        responsibles);
            } else if (task.getStory() != null
                    && !task.getStory().getResponsibles().isEmpty()) {
                responsibleCount = task.getStory().getResponsibles().size();
                Collection<User> responsibles = task.getStory()
                        .getResponsibles();
                divideTaskLoad(assignments, responsibleCount, taskEffort,
                        responsibles);

            } else {
                unassignedLoad += taskEffort;
            }
        }
        float timeframeLeft = this
                .calculateIterationTimeframePercentageLeft(iter);
        float weeksLeft = (float) this.daysLeftInIteration(iter).getDays() / 7.0f;
        long iterationBaselineLoad = 0l;
        if (iter.getBaselineLoad() != null) {
            iterationBaselineLoad = (long) (iter.getBaselineLoad().floatValue() * weeksLeft);
        }
        for (AssignmentTO assignment : assignments.values()) {
            float assignedPortion = (float) assignment.getAvailability()
                    / (float) totalAvailability;
            assignment.getBaselineLoad().add(iterationBaselineLoad);
            assignment.setUnassignedLoad(new ExactEstimate(
                    (long) (assignedPortion * (float) unassignedLoad)));

            ExactEstimate iterationWorkHours;
            if(iter.getBacklogSize() != null) {
                iterationWorkHours = new ExactEstimate(Math
                        .round(timeframeLeft * iter.getBacklogSize().floatValue()
                                * assignedPortion));
            } else {
                iterationWorkHours = new ExactEstimate(0);
            }
            assignment.setAvailableWorktime(iterationWorkHours);
            SignedExactEstimate totalLoad = new SignedExactEstimate(0);
            totalLoad.add(assignment.getUnassignedLoad().longValue());
            totalLoad.add(assignment.getAssignedLoad().longValue());
            if (assignment.getPersonalLoad() != null) {
                assignment.getBaselineLoad().add(
                        (long) (weeksLeft * assignment.getPersonalLoad()
                                .floatValue()));
            }
            if(assignment.getBaselineLoad() != null) {
                totalLoad.add(assignment.getBaselineLoad().longValue());
            }
            assignment.setTotalLoad(totalLoad);
            if (iterationWorkHours.longValue() > 0l) {
                assignment.setLoadPercentage(Math.round(100f
                        * assignment.getTotalLoad().floatValue()
                        / assignment.getAvailableWorktime().floatValue()));
            }
        }
        return new HashSet<AssignmentTO>(assignments.values());
    }

    private void divideTaskLoad(Map<Integer, AssignmentTO> assignments,
            int responsibleCount, long taskEffort, Collection<User> responsibles) {
        for (User user : responsibles) {
            AssignmentTO assignment = assignments.get(user.getId());
            if (assignment == null) {
                assignment = new AssignmentTO(new Assignment());
                assignment.setAvailability(0);
                assignment.setId(-1 * user.getId()); // user negative and
                                                     // context unique id for
                                                     // non-persited assignment
                assignment.setUser(user);
                assignment.setUnassigned(true);
                assignments.put(user.getId(), assignment);
            }
            assignment.getAssignedLoad().add(taskEffort / responsibleCount);
        }
    }
    
    public Days daysLeftInIteration(Iteration iter) {
        DateTime currentTime = new DateTime();
        Interval iterInterval = new Interval(iter.getStartDate()
                .toDateMidnight(), iter.getEndDate().toDateMidnight());
        if (iter.getEndDate().isBeforeNow()) {
            return Days.days(0);
        }
        Interval toIterationEnd = new Interval(currentTime.toDateMidnight(),
                iter.getEndDate().toDateMidnight());
        Interval intersection = toIterationEnd.overlap(iterInterval);
        if (iterInterval.toDurationMillis() == 0) {
            return Days.days(0);
        } 
        return Days.daysIn(intersection);
    }

    public float calculateIterationTimeframePercentageLeft(Iteration iter) {
        Interval iterInterval = new Interval(iter.getStartDate()
                .toDateMidnight(), iter.getEndDate().toDateMidnight());
        Days daysLeft = this.daysLeftInIteration(iter);
        return (float) daysLeft.toStandardDuration().getMillis()
                / (float) iterInterval.toDurationMillis();
    }
    
    public Integer calculateVariance(Iteration iter) {
        IterationHistoryEntry latestHistoryEntry = iterationHistoryEntryBusiness
        .retrieveLatest(iter);
        if(latestHistoryEntry == null) {
            return null;
        }
        long effortLeft = latestHistoryEntry.getEffortLeftSum();
        long dailyVelocity = calculateDailyVelocity(iter).longValue();
        if(dailyVelocity != 0 && iter.getStartDate().isBeforeNow() && effortLeft != 0) {
            int daysLeft = (int) (effortLeft / dailyVelocity);
            return daysLeft - daysLeftInIteration(iter).getDays(); 
        } else {
            return null;
        }      
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
        
    }

}
