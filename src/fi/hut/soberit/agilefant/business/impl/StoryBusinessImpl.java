package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.RankingBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.impl.RankinkBusinessImpl.RankDirection;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.history.StoryHistoryDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Rankable;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.HistoryRowTO;
import fi.hut.soberit.agilefant.util.Pair;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import fi.hut.soberit.agilefant.util.StoryMetrics;

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
    private RankingBusiness rankingBusiness;

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
    public Collection<ResponsibleContainer> getStoryResponsibles(Story story) {
        Collection<ResponsibleContainer> responsibleContainers = new ArrayList<ResponsibleContainer>();
        Collection<User> storyResponsibles = story.getResponsibles();
        for (User user : storyResponsibles) {
            responsibleContainers.add(new ResponsibleContainer(user, true));
        }
        return responsibleContainers;
    }
    
    @Transactional(readOnly = true)
    public Collection<User> getStorysProjectResponsibles(Story story) {
        if (story.getBacklog() instanceof Project) {
            return projectBusiness.getAssignedUsers((Project)story.getBacklog());
        }
        else if (story.getBacklog() instanceof Iteration){
            return projectBusiness.getAssignedUsers((Project)story.getBacklog().getParent()); 
        }
        return new ArrayList<User>();
    }


    /** {@inheritDoc} */
    @Override
    @Transactional
    public void delete(int storyId) throws ObjectNotFoundException {
        Story story = this.retrieve(storyId);
        this.delete(story);
    }

    @Override
    public void delete(Story story) {
        if(story.getHourEntries().size() != 0) {
            throw new OperationNotPermittedException("Story contains spent effort entries.");
        }
        if(story.getTasks().size() != 0) {
            throw new OperationNotPermittedException("Story contains tasks.");
        }
        Backlog backlog = story.getBacklog();
        if (backlog != null) {
            backlog.getStories().remove(story);
        }
        super.delete(story);
        backlogHistoryEntryBusiness.updateHistory(backlog.getId());
    }
    /** {@inheritDoc} */
    @Transactional
    public Story store(Integer storyId, Story dataItem, Integer backlogId, Set<Integer> responsibleIds)
            throws ObjectNotFoundException, IllegalArgumentException {
        if (storyId == null) {
            throw new IllegalArgumentException("Story id should be given");
        }
        
        Story persisted = this.retrieve(storyId);
        
        setResponsibles(persisted, responsibleIds);
        populateStoryFields(persisted, dataItem);
        
        // Store the story
        storyDAO.store(persisted);
        
        // Set the backlog if backlogId given
        if (backlogId != null) {
            this.moveStoryToBacklog(persisted, backlogBusiness.retrieve(backlogId));
        }
        
        
        return persisted;
    }

    private void populateStoryFields(Story persisted, Story dataItem) {
        persisted.setDescription(dataItem.getDescription());
        persisted.setName(dataItem.getName());
        persisted.setState(dataItem.getState());
        persisted.setStoryPoints(dataItem.getStoryPoints());
    }

    private void setResponsibles(Story story, Set<Integer> responsibleIds) {
        if (responsibleIds != null) {
            story.getResponsibles().clear();
            for (Integer userId : responsibleIds) {
                story.getResponsibles().add(userDAO.get(userId));
            }
        }
    }
    
    
    @Transactional
    /** {@inheritDoc} */
    public Story create(Story dataItem, Integer backlogId, Set<Integer> responsibleIds)
        throws IllegalArgumentException, ObjectNotFoundException {
        if (dataItem == null || backlogId == null) {
            throw new IllegalArgumentException("DataItem and backlogId should not be null");
        }
        Backlog backlog = this.backlogBusiness.retrieve(backlogId);
        if (backlog == null) {
            throw new ObjectNotFoundException("backlog.notFound");
        }
        
        Story story = new Story();
        
        this.setResponsibles(story, responsibleIds);
        this.populateStoryFields(story, dataItem);
        story.setBacklog(backlog);
        this.rankToBottom(story, backlogId);
        
        int newId = (Integer)storyDAO.create(story);
        
        if (backlog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(backlog.getId());
            backlogHistoryEntryBusiness.updateHistory(backlog.getId());
        }
        else if (backlog instanceof Project) {
            backlogHistoryEntryBusiness.updateHistory(backlog.getId());
        }
        
        return storyDAO.get(newId);
    };
    
    
    @Transactional
    public void moveStoryToBacklog(Story story, Backlog backlog) {
        Backlog oldBacklog = story.getBacklog();
        oldBacklog.getStories().remove(story);
        story.setBacklog(backlog);
        backlog.getStories().add(story);
        storyDAO.store(story);
        this.rankToBottom(story, backlog.getId());
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

    /*
     * STORY RANKING
     */
    /** {@inheritDoc} */
    @Transactional
    public Story rankToBottom(Story story, Integer parentBacklogId)
            throws IllegalArgumentException {
        if (parentBacklogId == null) {
            throw new IllegalArgumentException("Parent should be given");
        }
        Backlog parent = backlogBusiness.retrieve(parentBacklogId);
        Story last = storyDAO.getLastStoryInRank(parent);
        story.setRank(last.getRank() + 1);
        return story;
    }
    
    /** {@inheritDoc} */
    @Transactional
    public Story rankUnderStory(Story story, Story upperStory)
            throws IllegalArgumentException {
        if (story == null) {
            throw new IllegalArgumentException("Story should be given");
        }
        else if (upperStory != null && story.getBacklog() != upperStory.getBacklog()) {
            throw new IllegalArgumentException("Stories' parent's should be the same");
        }
        
        RankDirection dir = rankingBusiness.findOutRankDirection(story, upperStory);
        int newRank = rankingBusiness.findOutNewRank(story, upperStory, dir);
        Pair<Integer, Integer> borders = rankingBusiness.getRankBorders(story, upperStory);
        
        Collection<Rankable> storiesToShift = new ArrayList<Rankable>();
        storiesToShift.addAll(storyDAO.getStoriesWithRankBetween(story.getBacklog(), borders.first, borders.second));
        
        rankingBusiness.shiftRanks(dir, storiesToShift);
        
        story.setRank(newRank);
        
        return story;
    }
    
    /** {@inheritDoc} */
    @Transactional
    public Story rankAndMove(Story story, Story upperStory, Backlog newParent) {
        if (newParent != null) {
            moveStoryToBacklog(story, newParent);
        }
        rankUnderStory(story, upperStory);        
        return story;
    };
    


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

    @Transactional(readOnly=true)
    public List<HistoryRowTO> retrieveStoryHistory(int id) {
        return storyHistoryDAO.retrieveLatestChanges(id, null);
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

    public void setRankingBusiness(RankingBusiness rankingBusiness) {
        this.rankingBusiness = rankingBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }
}
