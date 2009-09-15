package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.DailyWorkBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.RankingBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.business.impl.RankinkBusinessImpl.RankDirection;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Rankable;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.Pair;
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
    
    @Autowired
    private DailyWorkBusiness dailyWorkBusiness;
    
    @Autowired
    private RankingBusiness rankingBusiness;
    
    private TaskDAO taskDAO;
    
    public TaskBusinessImpl() {
        super(Task.class);
    }
    
    @Autowired
    public void setTaskDAO(TaskDAO taskDAO) {
        this.genericDAO = taskDAO;
        this.taskDAO = taskDAO;
    }
    
    @Transactional(readOnly = true)
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
               
        updateEffortLeftAndOriginalEstimate(task);
        
        populateUserData(task, userIds);
        
        if (task.getId() == 0) {
            int newTaskId = this.create(task);
            storedTask = this.retrieve(newTaskId);
            this.rankToBottom(storedTask, storyId, iterationId);
        }
        else {
            this.store(task);
            storedTask = task;
        }
        
        updateIterationHistoryIfApplicable(task);
        
        if (task.getState() == TaskState.DONE) {
            dailyWorkBusiness.removeTaskFromWorkQueues(task);
        }
        
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
        delete(retrieve(id));
    }
    
    @Override
    public void delete(Task task) {
        if(task.getHourEntries().size() != 0) {
            throw new OperationNotPermittedException("Task contains spent effort entries.");
        }
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
    @Transactional
    public Task rankUnderTask(Task task, Task upperTask) throws IllegalArgumentException {
        if (task == null) {
            throw new IllegalArgumentException("Task should be given");
        }
        else if (upperTask != null) {
            if (task.getStory() != upperTask.getStory()) {
                throw new IllegalArgumentException("The tasks' parent's should be the same");    
            }
            else if (task.getIteration() != upperTask.getIteration()) {
                throw new IllegalArgumentException("The tasks' parent's should be the same");
            }
        }
        
        RankDirection dir = rankingBusiness.findOutRankDirection(task, upperTask);
        int newRank = rankingBusiness.findOutNewRank(task, upperTask, dir);
        
        Pair<Integer, Integer> borders = rankingBusiness.getRankBorders(task, upperTask);
        
        Collection<Rankable> shiftables = new ArrayList<Rankable>();
        shiftables.addAll(taskDAO.getTasksWithRankBetween(
                borders.first, borders.second, task.getIteration(), task.getStory()));
        
        rankingBusiness.shiftRanks(dir, shiftables);
        
//        Collection<Task> shiftedTasks = getShiftedTasks(dir, task, upperTask);
//        shiftTaskRanks(dir, shiftedTasks);
        
        task.setRank(newRank);
        
        return task;
    }
    

    
    /** {@inheritDoc} */
    @Transactional
    public Task rankToBottom(Task task, Integer parentStoryId, Integer parentIterationId)
        throws IllegalArgumentException {
        if (task == null || (parentStoryId == null && parentIterationId == null)) {
            throw new IllegalArgumentException();
        }
        Task lastInRank;
        
        if (parentStoryId != null) {
            lastInRank = taskDAO.getLastTaskInRank(storyBusiness.retrieve(parentStoryId), null);    
        }
        else {
            lastInRank = taskDAO.getLastTaskInRank(null, iterationBusiness.retrieve(parentIterationId));
        }
        
        task.setRank(lastInRank.getRank() + 1);
        return task;
    }
    

    
    
    /** {@inheritDoc} */
    @Transactional
    public Task rankAndMove(Task task, Task upperTask, Integer parentStoryId,
            Integer parentIterationId) throws IllegalArgumentException {
        
        assignParentForTask(task, parentIterationId, parentStoryId);
        rankToBottom(task, parentStoryId, parentIterationId);
        rankUnderTask(task, upperTask);
        
        return task;
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

    public void setRankingBusiness(RankingBusiness rankingBusiness) {
        this.rankingBusiness = rankingBusiness;
    }
    
}