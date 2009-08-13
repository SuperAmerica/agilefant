package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

@Service("taskBusiness")
@Transactional
public class TaskBusinessImpl extends GenericBusinessImpl<Task> implements
        TaskBusiness {

    @Autowired
    private IterationBusiness iterationBusiness;
    
    @Autowired
    private StoryBusiness storyBusiness;
    
    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness;
    
    private TaskDAO taskDAO;
    
    @Autowired
    public void setTaskDAO(TaskDAO taskDAO) {
        this.genericDAO = taskDAO;
        this.taskDAO = taskDAO;
    }
    

    public Collection<ResponsibleContainer> getTaskResponsibles(Task task) {
        Collection<ResponsibleContainer> responsibleContainers = new ArrayList<ResponsibleContainer>();
        Collection<User> storyResponsibles = task.getResponsibles();
        for (User user : storyResponsibles) {
            responsibleContainers.add(new ResponsibleContainer(user, true));
        }
        return responsibleContainers;
    }
    
    /** {@inheritDoc} */
    public Task storeTask(Task task, Integer iterationId, Integer storyId, Set<Integer> userIds) {
        Task storedTask = null;
        
        if (task == null) {
            throw new IllegalArgumentException("Task should be given");
        }
        
        //allow storing existing task without relations
        if(task.getId() == 0 || iterationId != null || storyId != null) {
            assignParentForTask(task, iterationId, storyId);
        }
        
        setTaskCreator(task);
        
        updateEffortLeftAndOriginalEstimate(task);
        
        populateUserData(task, userIds);
        
        if (task.getId() == 0) {
            int newTaskId = this.create(task);
            storedTask = this.retrieve(newTaskId);
        }
        else {
            this.store(task);
            storedTask = task;
        }
        
        updateIterationHistoryIfApplicable(task);
        
        return storedTask;
    }

    private void updateIterationHistoryIfApplicable(Task task) {
        Integer iterationId = getTaskIterationId(task);
        updateIterationHistoryIfNotNull(iterationId);
    }

    private void updateEffortLeftAndOriginalEstimate(Task task) {
        if (task.getEffortLeft() == null && task.getOriginalEstimate() != null) {
            task.setEffortLeft(task.getOriginalEstimate());
        }
        
        if (task.getOriginalEstimate() == null && task.getEffortLeft() != null) {
            task.setOriginalEstimate(task.getEffortLeft());
        }
        
        if (task.getState() == TaskState.DONE) {
            task.setEffortLeft(new ExactEstimate(0));
        }
    }

    /** If the task is new, set the creator */
    private void setTaskCreator(Task task) {
        if (task.getId() == 0) {
            task.setCreator(getLoggedInUser());
            task.setCreatedDate(new DateTime().toDate());
        }
    }

    /** {@inheritDoc} */
    @Transactional
    public void assignParentForTask(Task task, Integer iterationId, Integer storyId)
        throws IllegalArgumentException, ObjectNotFoundException {
        // 1. Error handling
        checkArgumentsForMoving(task, iterationId, storyId);
        
        // 2. The logic
        if (iterationId != null) {
            task.setIteration(iterationBusiness.retrieve(iterationId));
            task.setStory(null);
        }
        else {
            task.setStory(storyBusiness.retrieve(storyId));
            task.setIteration(null);
        }
    }

    private void checkArgumentsForMoving(Task task, Integer iterationId,
            Integer storyId) {
        if (task == null) {
            throw new IllegalArgumentException("Task should be given.");
        }
        else if (iterationId == null && storyId == null) {
            throw new IllegalArgumentException("The parent id should be given");
        }
        else if (iterationId != null && storyId != null) {
            throw new IllegalArgumentException("Only one parent can be given");
        }
    }
    
    /** {@inheritDoc} */
    @Transactional
    public Task move(Task task, Integer iterationId, Integer storyId) {
        checkArgumentsForMoving(task, iterationId, storyId);
        
        Integer sourceIterationId = getTaskIterationId(task);
        
        assignParentForTask(task, iterationId, storyId);
        this.store(task);
        
        Integer destinationIterationId = getTaskIterationId(task);
        
        if (sourceIterationId != destinationIterationId) {
            updateIterationHistoryIfNotNull(sourceIterationId);
            updateIterationHistoryIfNotNull(destinationIterationId);
        }
        
        return task;
    }

    private void updateIterationHistoryIfNotNull(Integer iterationId) {
        if (iterationId == null) {
            return;
        }
        iterationHistoryEntryBusiness.updateIterationHistory(iterationId);
    }
    
    
    /**
     * Gets the tasks parent iteration's id.
     * <p>
     * If task resides under a story, get the story's parent
     * iteration id.
     * 
     * If story's parent backlog is not and iteration, return null.
     */
    private Integer getTaskIterationId(Task task) {
        Integer iterationId = null;
        if (task.getIteration() != null) {
            iterationId = task.getIteration().getId();
        }
        else if (task.getStory().getBacklog() instanceof Iteration) {
            iterationId = task.getStory().getBacklog().getId();
        }
        return iterationId;
    }
       
    public User getLoggedInUser() {
        User loggedUser = null;
        // May fail if request is multithreaded
        loggedUser = SecurityUtil.getLoggedUser();
        return loggedUser;
    }
    
    /**
     * Populates user ids into tasks responsibles.
     * <p>
     * Will skip not found users.
     */
    private void populateUserData(Task task, Set<Integer> userIds) {
        if (userIds == null) return;
        Set<User> userSet = new HashSet<User>();
        
        for (Integer userId : userIds) {
            User user = userBusiness.retrieveIfExists(userId);
            if (user != null) {
                userSet.add(user);
            }
        }
        
        task.getResponsibles().clear();
        task.getResponsibles().addAll(userSet);
    }

    public Task resetOriginalEstimate(int taskId) {
        Task task = retrieve(taskId);
        task.setEffortLeft(null);
        task.setOriginalEstimate(null);
        taskDAO.store(task);
        
        updateIterationHistoryIfApplicable(task);
        
        return task;
    }
    
    @Override
    public void delete(int id) {        
        delete(taskDAO.get(id));
    }
    
    @Override
    public void delete(Task task) {
        taskDAO.remove(task.getId());
        if (task.getIteration() != null) {
            iterationHistoryEntryBusiness.updateIterationHistory(task.getIteration().getId());  
        }
        else if (task.getStory().getBacklog() instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(task.getStory().getBacklog().getId());
        }
    }
    
    /* RANKING */
    
    
    /** {@inheritDoc} */
    public Task rankUnderTask(Task task, Task upperTask) throws IllegalArgumentException {
        validateRankingArguments(task, upperTask);
        
        RankDirection dir = findOutRankDirection(task, upperTask);
        int newRank = findOutNewRank(dir, upperTask);
        
        Collection<Task> shiftedTasks = getShiftedTasks(dir, task, upperTask);
        shiftTaskRanks(dir, shiftedTasks);
        
        task.setRank(newRank);
        
        return task;
    }


    private int findOutNewRank(RankDirection dir, Task upperTask) {
        if (dir == RankDirection.UP) {
            return upperTask.getRank() + 1;
        }
        else if (dir == RankDirection.DOWN) {
            return upperTask.getRank();
        }
        return 0;
    }


    private enum RankDirection { TOP, UP, DOWN, BOTTOM }
    
    private RankDirection findOutRankDirection(Task task, Task upperTask) {
        if (upperTask == null) {
            return RankDirection.TOP;
        }
        else if (task.getRank() == -1) {
            return RankDirection.BOTTOM;
        }
        else if (task.getRank() >= upperTask.getRank()) {
            return RankDirection.UP;
        }
        else {
            return RankDirection.DOWN;
        }
    }
    
    private void shiftTaskRanks(RankDirection dir, Collection<Task> tasksToShift) {
        for (Task t : tasksToShift) {
            if (dir == RankDirection.DOWN) {
                t.setRank(t.getRank() - 1);
            }
            else {
                t.setRank(t.getRank() + 1);
            }
        }
    }
    
    private Collection<Task> getShiftedTasks(RankDirection dir, Task task, Task upperTask) {
        if (dir == RankDirection.UP) {
            // While moving upwards, remove both task and upperTask
            int lower = upperTask.getRank() + 1;
            int upper = task.getRank() - 1;
            return getSiblingTasksBetweenRanks(task, lower, upper);
        }
        else if (dir == RankDirection.DOWN) {
            // While moving downwards, remove only self
            int lower = task.getRank() + 1;
            int upper = upperTask.getRank();
            return getSiblingTasksBetweenRanks(task, lower, upper);
        }
        else if (dir == RankDirection.TOP) {
            int upper = task.getRank() - 1;
            return getSiblingTasksBetweenRanks(task, 0, upper);
        }
        return null;
    }

    private Collection<Task> getSiblingTasksBetweenRanks(Task task, int lower, int upper) {
        if (task.getStory() != null) {
            return taskDAO.getTasksWithRankBetween(task.getStory(), lower, upper);
        }
        else {
            return taskDAO.getTasksWithRankBetween(task.getIteration(), lower, upper);
        }
    }

    private void validateRankingArguments(Task task, Task upperTask) {
        if (task == null) {
            throw new IllegalArgumentException("Task should not be null");
        }
        else if (upperTask != null && (
                task.getStory() != upperTask.getStory() ||
                task.getIteration() != upperTask.getIteration()
                )) {
            throw new IllegalArgumentException("The tasks' parents should be same");
        }
        else if (task.equals(upperTask)) {
            throw new IllegalArgumentException("Can't rank task under self");
        }
    }
    
    
    // AUTOGENERATED
    
    public void setIterationBusiness(IterationBusiness iterationBusiness) {
        this.iterationBusiness = iterationBusiness;
    }
    
    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }
   
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public void setIterationHistoryEntryBusiness(
            IterationHistoryEntryBusiness iterationHistoryEntryBusiness) {
        this.iterationHistoryEntryBusiness = iterationHistoryEntryBusiness;
    }
    
}