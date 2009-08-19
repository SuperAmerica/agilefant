package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.Days;
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
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationHistoryEntryDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.IterationDataContainer;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.Pair;

@Service("iterationBusiness")
@Transactional
public class IterationBusinessImpl extends GenericBusinessImpl<Iteration>
        implements IterationBusiness {

    private IterationDAO iterationDAO;

    @Autowired
    private TransferObjectBusiness transferObjectBusiness;
    @Autowired
    private ProjectBusiness projectBusiness;
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
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.genericDAO = iterationDAO;
        this.iterationDAO = iterationDAO;
    }

    @Override
    public void delete(int id) {
        delete(retrieve(id));
    }

    @Override
    public void delete(Iteration iteration) {
        Backlog project = iteration.getParent();
        super.delete(iteration);
        backlogHistoryEntryBusiness.updateHistory(project.getId());
    }

    @Transactional(readOnly = true)
    public IterationDataContainer getIterationContents(int iterationId) {

        IterationDataContainer iterationData = new IterationDataContainer();
        Iteration iteration = this.retrieve(iterationId);

        // Get the project's assignees
        Collection<User> assignedUsers = projectBusiness
                .getAssignedUsers((Project) iteration.getParent());

        // 1. Set iteration's stories as transfer objects and include story
        // metrics
        for (StoryTO storyTO : transferObjectBusiness
                .constructBacklogDataWithUserData(iteration, assignedUsers)) {
            storyTO.setMetrics(storyBusiness.calculateMetrics(storyTO));
            iterationData.getStories().add(storyTO);
        }

        // 2. Set the tasks without a story
        Collection<Task> tasksWithoutStory = iterationDAO
                .getTasksWithoutStoryForIteration(iteration);

        iterationData.setTasksWithoutStory(new ArrayList<Task>());

        for (Task task : tasksWithoutStory) {
            TaskTO taskTO = transferObjectBusiness.constructTaskTO(task,
                    assignedUsers);
            taskTO.setEffortSpent(hourEntryBusiness.calculateSum(taskTO
                    .getHourEntries()));
            iterationData.getTasksWithoutStory().add(taskTO);
        }

        return iterationData;
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

    public Iteration store(int iterationId, int parentBacklogId,
            Iteration iterationData) {
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
        if (iterationData.getEndDate().before(iterationData.getStartDate())) {
            throw new IllegalArgumentException("End date before start date");
        }
        if (iterationId == 0) {
            return this.create(parent, iterationData);
        }
        Iteration iter = this.retrieve(iterationId);
        iter.setStartDate(iterationData.getStartDate());
        iter.setEndDate(iterationData.getEndDate());
        iter.setBacklogSize(iterationData.getBacklogSize());
        iter.setBaselineLoad(iterationData.getBaselineLoad());
        iter.setDescription(iterationData.getDescription());
        iter.setName(iterationData.getName());
        this.iterationDAO.store(iter);
        if (parent != null && iter.getParent() != parent) {
            this.moveTo(iter, parent);
        }
        return iter;
    }

    private Iteration create(Backlog parentBacklog, Iteration iterationData) {
        iterationData.setParent(parentBacklog);
        int iterationId = (Integer) this.iterationDAO.create(iterationData);
        Iteration iter = this.retrieve(iterationId);
        
        //copy project assignments
        if (parentBacklog instanceof Project) {
            Set<Integer> userIds = new HashSet<Integer>();
            for (Assignment assignment : ((Project) parentBacklog)
                    .getAssignments()) {
                userIds.add(assignment.getUser().getId());
            }
            if(userIds.size() > 0) {
                iterationData.setAssignments(this.assignmentBusiness.addMultiple(
                        iter, userIds, ExactEstimate.ZERO,
                        100));
                }
        }
        return iter;
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

    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
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

}
