package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.business.StoryRankBusiness;
import fi.hut.soberit.agilefant.business.StoryTreeIntegrityBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.history.StoryHistoryDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.HistoryRowTO;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.HourEntryHandlingChoice;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import fi.hut.soberit.agilefant.util.TaskHandlingChoice;

@Service("storyBusiness")
@Transactional
public class StoryBusinessImpl extends GenericBusinessImpl<Story> implements
        StoryBusiness {

    private StoryDAO storyDAO;
    @Autowired
    private BacklogBusiness backlogBusiness;
    @Autowired
    private IterationDAO iterationDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private HourEntryDAO hourEntryDAO;
    @Autowired
    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness;
    @Autowired
    private BacklogHistoryEntryBusiness backlogHistoryEntryBusiness;
    @Autowired
    private ProjectBusiness projectBusiness;
    @Autowired
    private StoryHistoryDAO storyHistoryDAO;
    @Autowired
    private StoryRankBusiness storyRankBusiness;
    @Autowired
    private TransferObjectBusiness transferObjectBusiness;
    @Autowired
    private HourEntryBusiness hourEntryBusiness;
    @Autowired
    private TaskBusiness taskBusiness;
    @Autowired
    private StoryHierarchyBusiness storyHierarchyBusiness;
    @Autowired
    private StoryTreeIntegrityBusiness storyTreeIntegrityBusiness;

    public StoryBusinessImpl() {
        super(Story.class);
    }

    @Autowired
    public void setStoryDAO(StoryDAO storyDAO) {
        this.genericDAO = storyDAO;
        this.storyDAO = storyDAO;
    }

    @Transactional(readOnly = true)
    public List<Story> getStoriesByBacklog(Backlog backlog) {
        return storyDAO.getStoriesByBacklog(backlog);
    }

    @Transactional(readOnly = true)
    public Collection<Task> getStoryContents(Story story, Iteration iteration) {
        List<Task> tasks = iterationDAO.getAllTasksForIteration(iteration);
        Collection<Task> storyTasks = new ArrayList<Task>();
        for (Task bli : tasks) {
            if (bli.getStory() == story) {
                storyTasks.add(bli);
            }
        }
        return storyTasks;
    }

    @Transactional(readOnly = true)
    public Collection<Task> getStoryContents(int storyId, int iterationId) {
        Story story = storyDAO.get(storyId);
        Iteration iter = iterationDAO.get(iterationId);
        if (iter == null) {
            return null;
        }
        return getStoryContents(story, iter);
    }

    @Transactional(readOnly = true)
    public Collection<User> getStorysProjectResponsibles(Story story) {
        if (story.getBacklog() instanceof Project) {
            return projectBusiness.getAssignedUsers((Project) story
                    .getBacklog());
        } else if (story.getBacklog() instanceof Iteration) {
            return projectBusiness.getAssignedUsers((Project) story
                    .getBacklog().getParent());
        }
        return new ArrayList<User>();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(int storyId) throws ObjectNotFoundException {
        delete(this.retrieve(storyId));
    }

    @Override
    public void delete(Story story) {
        delete(story, null, null, null);
    }
    
    public void delete(int id,TaskHandlingChoice taskHandlingChoice,
            HourEntryHandlingChoice storyHourEntryHandlingChoice,
            HourEntryHandlingChoice taskHourEntryHandlingChoice) {
        delete(this.retrieve(id), taskHandlingChoice, storyHourEntryHandlingChoice, taskHourEntryHandlingChoice);
    }
    
    public void deleteAndUpdateHistory(int id,TaskHandlingChoice taskHandlingChoice,
            HourEntryHandlingChoice storyHourEntryHandlingChoice,
            HourEntryHandlingChoice taskHourEntryHandlingChoice) {
        Story story = retrieve(id);
        delete(story, taskHandlingChoice, storyHourEntryHandlingChoice, taskHourEntryHandlingChoice);
        backlogHistoryEntryBusiness.updateHistory(story.getBacklog().getId());
        if (story.getBacklog() instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(story.getBacklog().getId());
        }
    }

    /** {@inheritDoc} */
    @Transactional
    public Story store(Integer storyId, Story dataItem, Integer backlogId,
            Set<Integer> responsibleIds, boolean tasksToDone) throws ObjectNotFoundException,
            IllegalArgumentException {
        if (storyId == null) {
            throw new IllegalArgumentException("Story id should be given");
        }

        Story persisted = this.retrieve(storyId);

        setResponsibles(persisted, responsibleIds);
        populateStoryFields(persisted, dataItem);

        // Store the story
        storyDAO.store(persisted);

        if (tasksToDone && persisted.getBacklog() instanceof Iteration) {
            for (Task t : persisted.getTasks()) {
                taskBusiness.setTaskToDone(t);
            }
            iterationHistoryEntryBusiness.updateIterationHistory(persisted
                    .getBacklog().getId());
        }
        
        // Set the backlog if backlogId given
        if (backlogId != null) {
            this.moveStoryToBacklog(persisted, backlogBusiness
                    .retrieve(backlogId));
        }
        else if (dataItem.getBacklog() != persisted.getBacklog() &&
                 dataItem.getBacklog() != null) {
            this.moveStoryToBacklog(persisted, dataItem.getBacklog());
        }

        backlogHistoryEntryBusiness.updateHistory(persisted.getBacklog()
                .getId());
        
        return persisted;
    }

    public Story updateStoryRanks(Story story) {
        if (story.getBacklog() instanceof Product) {
            return story;
        }

        if (!story.getChildren().isEmpty() && !story.getStoryRanks().isEmpty()) {
            // need to remove ranks
            storyRankBusiness.removeStoryRanks(story);

        } else if (story.getChildren().isEmpty()
                && story.getStoryRanks().isEmpty()) {
            createStoryRanks(story, story.getBacklog());
        }
        return story;
    }

    private void createStoryRanks(Story story, Backlog backlog) {
        if(!(backlog instanceof Product)) {
            this.storyRankBusiness.rankToBottom(story, backlog);
            if (backlog instanceof Iteration) {
                this.storyRankBusiness.rankToBottom(story, backlog.getParent());
            }
        }
    }

    @Transactional
    public void storeBatch(Collection<Story> stories) {
        for (Story story : stories) {
            if (story.getId() == 0) {
                throw new IllegalArgumentException("non persited story");
            }
            this.store(story);
        }
    }

    public Collection<Story> retrieveMultiple(Collection<Story> stories) {
        Collection<Story> ret = new ArrayList<Story>();
        for (Story story : stories) {
            ret.add(this.retrieve(story.getId()));
        }
        return ret;
    }

    private void populateStoryFields(Story persisted, Story dataItem) {
        persisted.setDescription(dataItem.getDescription());
        persisted.setName(dataItem.getName());
        persisted.setState(dataItem.getState());
        persisted.setStoryPoints(dataItem.getStoryPoints());
        persisted.setParent(dataItem.getParent());
    }

    private void setResponsibles(Story story, Set<Integer> responsibleIds) {
        if (responsibleIds != null) {
            story.getResponsibles().clear();
            for (Integer userId : responsibleIds) {
                story.getResponsibles().add(userDAO.get(userId));
            }
        }
    }
    
    public Story createStoryUnder(int referenceStoryId, Story data, Set<Integer> responsibleIds) {
        Story referenceStory = this.retrieve(referenceStoryId);
        Backlog backlog = referenceStory.getBacklog();
        Story story = this.persistNewStory(data, backlog.getId(), responsibleIds);
        this.storyHierarchyBusiness.moveUnder(story,referenceStory);
        return story;
    }
    
    public Story createStorySibling(int referenceStoryId, Story data, Set<Integer> responsibleIds) {
        Story referenceStory = this.retrieve(referenceStoryId);
        Backlog backlog = referenceStory.getBacklog();
        Story story = this.persistNewStory(data, backlog.getId(), responsibleIds);
        this.storyHierarchyBusiness.moveAfter(story,referenceStory);
        return story;
    }

    public Story create(Story dataItem, Integer backlogId,
            Set<Integer> responsibleIds) throws IllegalArgumentException,
            ObjectNotFoundException {
        
        Story persisted = this.persistNewStory(dataItem, backlogId, responsibleIds);        
        storyHierarchyBusiness.moveToBottom(persisted);
        return persisted;
    }
    
    @Transactional
    /* * {@inheritDoc} */
    private Story persistNewStory(Story dataItem, Integer backlogId,
            Set<Integer> responsibleIds) throws IllegalArgumentException,
            ObjectNotFoundException {
        if (dataItem == null || backlogId == null) {
            throw new IllegalArgumentException(
                    "DataItem and backlogId should not be null");
        }
        Backlog backlog = this.backlogBusiness.retrieve(backlogId);
        if (backlog == null) {
            throw new ObjectNotFoundException("backlog.notFound");
        }

        Story story = new Story();

        this.setResponsibles(story, responsibleIds);
        this.populateStoryFields(story, dataItem);
        story.setBacklog(backlog);

        int newId = create(story);
        Story persisted = storyDAO.get(newId);
        
        return persisted;
    }

    @Transactional
    @Override
    public int create(Story story) {
        Backlog backlog = story.getBacklog();
        int newId = (Integer) storyDAO.create(story);
        story = storyDAO.get(newId);

        createStoryRanks(story, backlog);

        if (backlog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(backlog
                    .getId());
            backlogHistoryEntryBusiness.updateHistory(backlog.getId());
        } else if (backlog instanceof Project) {
            backlogHistoryEntryBusiness.updateHistory(backlog.getId());
        }

        return newId;
    }

    @Transactional
    public void moveStoryToBacklog(Story story, Backlog backlog) {
        /* Check for moving to other product */
        if (!story.getChildren().isEmpty()) {
            if (backlogBusiness.getParentProduct(story.getBacklog()) != backlogBusiness
                    .getParentProduct(backlog)) {
                throw new OperationNotPermittedException(
                        "Can't move a story with children to another product");
            }
        }
        if(!storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, backlog)) {
            throw new OperationNotPermittedException("Story tree integrity violation");
        }
        
        moveStory(story, backlog);
    }
    
    public void moveStoryAndChildren(Story story, Backlog backlog) {
        if (backlogBusiness.getParentProduct(story.getBacklog()) != backlogBusiness
                .getParentProduct(backlog)) {
            throw new OperationNotPermittedException(
                    "Can't move a story with children to another product");
        }
        if(this.storyTreeIntegrityBusiness.hasParentStoryConflict(story, backlog)) {
            story.setParent(null);
        }
        recursiveMoveStory(story, backlog);
    }
    
    private void recursiveMoveStory(Story story, Backlog backlog) {
        for(Story child : story.getChildren()) {
            recursiveMoveStory(child, backlog);
        }
        moveStory(story, backlog);
    }
    
    public void moveSingleStoryToBacklog(Story story, Backlog backlog) {
        if (backlogBusiness.getParentProduct(story.getBacklog()) != backlogBusiness
                .getParentProduct(backlog)) {
            throw new OperationNotPermittedException(
                    "Can't move a story with children to another product");
        }
        //move children to the parent story
        Story parent = story.getParent();
        List<Story> childStories = new ArrayList<Story>(story.getChildren());
        for(Story childStory : childStories) {
            childStory.setParent(parent);
            storyDAO.store(childStory);
        }
        //reset parent story
        if(this.storyTreeIntegrityBusiness.hasParentStoryConflict(story, backlog)) {
            story.setParent(null);
        }
        moveStory(story, backlog);
    }

    private void moveStory(Story story, Backlog backlog) {
        Backlog oldBacklog = story.getBacklog();
        oldBacklog.getStories().remove(story);
        story.setBacklog(backlog);
        backlog.getStories().add(story);
        storyDAO.store(story);
        rankToBottom(story, backlog, oldBacklog);

        backlogHistoryEntryBusiness.updateHistory(oldBacklog.getId());
        backlogHistoryEntryBusiness.updateHistory(backlog.getId());
        if (oldBacklog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(oldBacklog
                    .getId());
        }
        if (backlog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(backlog
                    .getId());
        }
    }

    private void rankToBottom(Story story, Backlog backlog, Backlog oldBacklog) {
        if (backlog == null || oldBacklog == null) {
            throw new IllegalArgumentException("backlogs can not be null");
        }
        // if target is product remove all ranks
        if ((backlog instanceof Product) && !(oldBacklog instanceof Product)) {
            if (oldBacklog instanceof Iteration) {
                storyRankBusiness.removeRank(story, oldBacklog.getParent());
            }
            storyRankBusiness.removeRank(story, oldBacklog);

        } else if (backlog instanceof Project) {
            rankToProjectBottom(story, backlog, oldBacklog);
        } else if (backlog instanceof Iteration) {
            rankToIterationBottom(story, backlog, oldBacklog);
        }
    }

    private void rankToIterationBottom(Story story, Backlog backlog,
            Backlog oldBacklog) {
        if (oldBacklog instanceof Product) {
            storyRankBusiness.rankToBottom(story, backlog);
            storyRankBusiness.rankToBottom(story, backlog.getParent());
        } else if (oldBacklog instanceof Iteration) {// from iteration to an
            // other
            // iterations are under the same project
            if (backlog.getParent() == oldBacklog.getParent()) {
                storyRankBusiness.removeRank(story, oldBacklog);
                storyRankBusiness.rankToBottom(story, backlog);
            } else {
                storyRankBusiness.removeRank(story, oldBacklog);
                storyRankBusiness.rankToBottom(story, backlog);
                storyRankBusiness.removeRank(story, oldBacklog.getParent());
                storyRankBusiness.rankToBottom(story, backlog.getParent());
            }

        } else if (oldBacklog instanceof Project) {// project to iteration
            // iteration is under the project
            if (backlog.getParent() == oldBacklog) {
                storyRankBusiness.rankToBottom(story, backlog);
            } else {
                storyRankBusiness.removeRank(story, oldBacklog);
                storyRankBusiness.rankToBottom(story, backlog);
                storyRankBusiness.rankToBottom(story, backlog.getParent());
            }
        }
    }

    private void rankToProjectBottom(Story story, Backlog backlog,
            Backlog oldBacklog) {
        if (oldBacklog instanceof Product) {
            storyRankBusiness.rankToBottom(story, backlog);
        } else if (oldBacklog instanceof Project
                && story.getChildren().isEmpty()) { // project to project
            storyRankBusiness.removeRank(story, oldBacklog);
            storyRankBusiness.rankToBottom(story, backlog);
        } else if (oldBacklog instanceof Iteration
                && story.getChildren().isEmpty()) { // iteration to project
            // move to the parent project
            if (backlog == oldBacklog.getParent()) {
                storyRankBusiness.removeRank(story, oldBacklog);
            } else {
                storyRankBusiness.removeRank(story, oldBacklog
                        .getParent());
                storyRankBusiness.rankToBottom(story, backlog);
                storyRankBusiness.removeRank(story, oldBacklog);
            }
        }
    }


    /** {@inheritDoc} */
    @Transactional
    public Story rankStoryUnder(Story story, Story upperStory, Backlog backlog) {
        backlog = checkRankingArguments(story, upperStory, backlog);
        storyRankBusiness.rankBelow(story, backlog, upperStory);
        return story;
    }

    /** {@inheritDoc} */
    @Transactional
    public Story rankStoryOver(Story story, Story lowerStory, Backlog backlog) {
        backlog = checkRankingArguments(story, lowerStory, backlog);
        storyRankBusiness.rankAbove(story, backlog, lowerStory);
        return story;
    }

    private Backlog checkRankingArguments(Story story, Story otherStory,
            Backlog backlog) {
        if (story == null) {
            throw new IllegalArgumentException("Story should be given");
        }
        // backlog mismatch
        if (otherStory != null && backlog == null
                && !isValidRankTarget(story, otherStory)) {
            throw new IllegalArgumentException("Invalid backlogs");
        }
        if (backlog == null) {
            backlog = story.getBacklog();
        }
        if (otherStory == null) {
            throw new IllegalArgumentException("Upper story should be given");
        }
        return backlog;
    }

    private boolean isValidRankTarget(Story story, Story upperStory) {
        boolean hasSameBacklog = upperStory.getBacklog() == story.getBacklog();
        boolean underReference = upperStory.getBacklog() == story.getBacklog()
                .getParent();
        boolean referenceUnder = story.getBacklog() == upperStory.getBacklog()
                .getParent();
        return hasSameBacklog || underReference || referenceUnder;
    }

    @Transactional(readOnly = true)
    public StoryMetrics calculateMetrics(Story story) {
        StoryMetrics metrics = new StoryMetrics();
        int tasks = 0;
        int doneTasks = 0;
        for (Task task : story.getTasks()) {
            if (task.getOriginalEstimate() != null) {
                metrics.setOriginalEstimate(metrics.getOriginalEstimate()
                        + task.getOriginalEstimate().getMinorUnits());
            }
            if (task.getEffortLeft() != null) {
                metrics.setEffortLeft(metrics.getEffortLeft()
                        + task.getEffortLeft().getMinorUnits());
            }
            tasks += 1;
            if (task.getState() == TaskState.DONE) {
                doneTasks += 1;
            }
        }
        metrics.setEffortSpent(hourEntryDAO.calculateSumByStory(story.getId()));
        metrics.setDoneTasks(doneTasks);
        metrics.setTotalTasks(tasks);
        return metrics;
    }

    @Transactional(readOnly = true)
    public StoryMetrics calculateMetrics(int storyId) {
        StoryMetrics metrics = storyDAO.calculateMetrics(storyId);
        metrics.setEffortSpent(hourEntryDAO.calculateSumByStory(storyId));
        return metrics;
    }

    @Transactional(readOnly = true)
    public StoryMetrics calculateMetricsWithoutStory(int iterationId) {
        StoryMetrics metrics = storyDAO
                .calculateMetricsWithoutStory(iterationId);
        metrics.setEffortSpent(hourEntryDAO
                .calculateSumFromTasksWithoutStory(iterationId));
        return metrics;
    }

    @Transactional(readOnly = true)
    public List<HistoryRowTO> retrieveStoryHistory(int id) {
        return storyHistoryDAO.retrieveLatestChanges(id, null);
    }

    @Transactional(readOnly = true)
    public StoryTO retrieveStoryWithMetrics(int storyId) {
        Story story = this.retrieve(storyId);
        StoryTO storyTo = this.transferObjectBusiness.constructStoryTO(story);
        storyTo.setMetrics(this.calculateMetrics(story));
        return storyTo;
    }

    public void delete(Story story, TaskHandlingChoice taskHandlingChoice,
            HourEntryHandlingChoice storyHourEntryHandlingChoice,
            HourEntryHandlingChoice taskHourEntryHandlingChoice) {
      
        for (Story child : story.getChildren()) {
            if(child != null ) {
                child.setParent(null);
            }
        }
        story.getChildren().clear();

        if (taskHandlingChoice != null) {
            switch (taskHandlingChoice) {
            case DELETE:
                for (Task task : story.getTasks()) {
                    if (taskHourEntryHandlingChoice == HourEntryHandlingChoice.MOVE) {
                        hourEntryBusiness.moveToBacklog(task.getHourEntries(),
                                story.getBacklog());
                    }
                    taskBusiness.delete(task.getId(), taskHourEntryHandlingChoice);
                }
                break;
            case MOVE:
                Iteration iteration = (Iteration) story.getBacklog();
                for (Task task : story.getTasks()) {
                    taskBusiness.move(task, iteration.getId(), null);
                    task.setStory(null);
                }
                break;
            }
            story.getTasks().clear();
        }
        if (storyHourEntryHandlingChoice != null) {
            switch (storyHourEntryHandlingChoice) {
            case DELETE:
                hourEntryBusiness.deleteAll(story.getHourEntries());
                break;
            case MOVE:
                hourEntryBusiness.moveToBacklog(story.getHourEntries(), story
                        .getBacklog());
                break;
            }
            story.getHourEntries().clear();
        }
        if (story.getHourEntries().size() != 0) {
            throw new OperationNotPermittedException(
                    "Story contains spent effort entries.");
        }
        if (story.getTasks().size() != 0) {
            throw new OperationNotPermittedException("Story contains tasks.");
        }
        if (story.getChildren().size() > 0) {
            throw new OperationNotPermittedException("Story has child stories.");
        }
        Backlog backlog = story.getBacklog();
        if (backlog != null) {
            backlog.getStories().remove(story);
        }
        Story parentStory = story.getParent();

        /* if last child of the parent story is removed the parent story may
           need to be ranked.
           The parent story's children's treeRanks are also updated
        */
        if (parentStory != null) {
            parentStory.getChildren().remove(story);
            updateStoryRanks(parentStory);
            storyHierarchyBusiness.updateChildrenTreeRanks(parentStory);
        }
        storyRankBusiness.removeStoryRanks(story);
        super.delete(story);
        
    }
    
    public void forceDelete(Story story) {
        // Remove children (set parent to null)
        for (Story s : story.getChildren()) {
            s.setParent(null);
        }
        story.getChildren().clear();
        
        // Remove tasks
        Set<Task> tasks = new HashSet<Task>(story.getTasks());
        for (Task t : tasks) {
            taskBusiness.delete(t, HourEntryHandlingChoice.DELETE);
        }
        
        // Remove own hour entries
        hourEntryBusiness.deleteAll(story.getHourEntries());
        
        super.delete(story.getId());
    }
    
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public void setBacklogHistoryEntryBusiness(
            BacklogHistoryEntryBusiness backlogHistoryEntryBusiness) {
        this.backlogHistoryEntryBusiness = backlogHistoryEntryBusiness;
    }

    public void setIterationHistoryEntryBusiness(
            IterationHistoryEntryBusiness iterationHistoryEntryBusiness) {
        this.iterationHistoryEntryBusiness = iterationHistoryEntryBusiness;
    }

    public int getStoryPointSumByBacklog(Backlog backlog) {
        return storyDAO.getStoryPointSumByBacklog(backlog.getId());
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.hourEntryDAO = hourEntryDAO;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }

    public void setStoryRankBusiness(StoryRankBusiness storyRankBusiness) {
        this.storyRankBusiness = storyRankBusiness;
    }
    
    public void setStoryHierarchyBusiness(StoryHierarchyBusiness storyHierarchyBusiness) {
        this.storyHierarchyBusiness = storyHierarchyBusiness;
    }

}
