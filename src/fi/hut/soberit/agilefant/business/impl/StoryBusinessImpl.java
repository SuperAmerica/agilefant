package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import fi.hut.soberit.agilefant.util.StoryMetrics;

@Service("storyBusiness")
@Transactional
public class StoryBusinessImpl extends GenericBusinessImpl<Story> implements
        StoryBusiness {

    private StoryDAO storyDAO;
    @Autowired
    private BacklogDAO backlogDAO;
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
    public void setStoryDAO(StoryDAO storyDAO) {
        this.genericDAO = storyDAO;
        this.storyDAO = storyDAO;
    }

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

    public Story store(int storyId, int backlogId, Story dataItem,
            Set<Integer> responsibles, int priority)
            throws ObjectNotFoundException {
        Story item = null;
        if (storyId > 0) {
            item = storyDAO.get(storyId);
            if (item == null) {
                item = new Story();
                item.setPriority(-1);
            }
        }
        Backlog backlog = backlogDAO.get(backlogId);
        if (backlog == null) {
            throw new ObjectNotFoundException("backlog.notFound");
        }

        Set<User> responsibleUsers = new HashSet<User>();

        for (int userId : responsibles) {
            User responsible = userDAO.get(userId);
            if (responsible != null) {
                responsibleUsers.add(responsible);
            }
        }

        return this.store(item, backlog, dataItem, responsibleUsers, priority);
    }

    /** {@inheritDoc} */
    public void remove(int storyId) throws ObjectNotFoundException {
        Story story = this.retrieve(storyId);
        Backlog backlog = story.getBacklog();
        if (backlog != null) {
            backlog.getStories().remove(story);
        }
        Collection<Task> tasks = story.getTasks();
        if (tasks != null) {
            for (Task item : tasks) {
                item.setStory(null);
            }
        }
        storyDAO.remove(storyId);
        backlogHistoryEntryBusiness.updateHistory(backlog.getId());
    }

    public Story store(Story storable, Backlog backlog, Story dataItem,
            Set<User> responsibles, Integer priority) {

        boolean historyUpdated = false;

        if (backlog == null) {
            throw new IllegalArgumentException("Backlog must not be null.");
        }
        if (dataItem == null) {
            throw new IllegalArgumentException("No data given.");
        }
        if (storable == null) {
            storable = new Story();
            storable.setCreatedDate(Calendar.getInstance().getTime());
            try {
                storable.setCreator(SecurityUtil.getLoggedUser()); // may fail
                // if request
                // is
                // multithreaded
            } catch (Exception e) {
            } // however, saving item should not fail.
        }
        storable.setDescription(dataItem.getDescription());
        // storable.setEffortLeft(dataItem.getEffortLeft());
        storable.setName(dataItem.getName());
        // if(storable.getOriginalEstimate() == null) {
        // if(dataItem.getOriginalEstimate() == null) {
        // storable.setOriginalEstimate(dataItem.getEffortLeft());
        // } else {
        // storable.setOriginalEstimate(dataItem.getOriginalEstimate());
        // }
        // }

        // storable.setPriority(dataItem.getPriority());
        storable.setState(dataItem.getState());
        storable.setStoryPoints(dataItem.getStoryPoints());

        // if(dataItem.getState() == State.DONE) {
        // storable.setEffortLeft(new AFTime(0));
        // } else if(dataItem.getEffortLeft() == null) {
        // storable.setEffortLeft(storable.getOriginalEstimate());
        // }

        if (storable.getBacklog() != null && storable.getBacklog() != backlog) {
            this.moveStoryToBacklog(storable, backlog);
            historyUpdated = true;
        } else if (storable.getBacklog() == null) {
            storable.setBacklog(backlog);
        }

        storable.getResponsibles().clear();
        storable.getResponsibles().addAll(responsibles);

        Story persisted;

        if (storable.getId() == 0) {
            storable.setPriority(-1);
            backlog.getStories().add(storable);
            int persistedId = (Integer) storyDAO.create(storable);
            persisted = storyDAO.get(persistedId);
        } else {
            storyDAO.store(storable);
            persisted = storable;
        }
        backlogHistoryEntryBusiness.updateHistory(persisted.getBacklog().getId());
        if (persisted.getBacklog() instanceof Iteration) {
            updateStoryPriority(persisted, priority);
            if (!historyUpdated) {
                iterationHistoryEntryBusiness.updateIterationHistory(backlog
                        .getId());
            }
        } else if (persisted.getBacklog() instanceof Project) {
            updateStoryPriority(persisted, priority);
        }
        return persisted;
    }

    public void moveStoryToBacklog(Story story, Backlog backlog) {

        Backlog oldBacklog = story.getBacklog();
        oldBacklog.getStories().remove(story);
        story.setBacklog(backlog);
        backlog.getStories().add(story);
        backlogHistoryEntryBusiness.updateHistory(oldBacklog.getId());            
        if (oldBacklog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(oldBacklog
                    .getId());
        }
        if (backlog instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(backlog
                    .getId());
        }

        // if(!backlogBusiness.isUnderSameProduct(oldBacklog, backlog)) {
        // //remove only product themes
        // Collection<BusinessTheme> removeThese = new
        // ArrayList<BusinessTheme>();;
        // for(BusinessTheme theme : story.getBusinessThemes()) {
        // if(!theme.isGlobal()) {
        // removeThese.add(theme);
        // }
        // }
        // for(BusinessTheme theme : removeThese) {
        // story.getBusinessThemes().remove(theme);
        // }
        // }
    }

    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
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

    // 090605 Reko: Copied from update iteration goal priority
    public void updateStoryPriority(Story story, int insertAtPriority) {
        if (insertAtPriority == story.getPriority()) {
            return;
        }
        if (story.getBacklog() == null
                || (story.getBacklog() instanceof Product)) {
            throw new IllegalArgumentException("backlog.notFound");
        }
        Backlog backlog = story.getBacklog();
        if (backlog.getStories().size() == 0) {
            throw new IllegalArgumentException("story.notFound");
        }
        int oldPriority = story.getPriority();

        for (Story item : backlog.getStories()) {
            // drop new goal to its place
            if (oldPriority == -1) {
                if (item.getPriority() >= insertAtPriority) {
                    item.setPriority(item.getPriority() + 1);
                    storyDAO.store(item);
                }
            } else {
                // when prioritizing downwards raise all goals by one which are
                // between the old and new priorities
                if (oldPriority < insertAtPriority
                        && item.getPriority() > oldPriority
                        && item.getPriority() <= insertAtPriority) {
                    item.setPriority(item.getPriority() - 1);
                    storyDAO.store(item);
                }
                // vice versa when prioritizing upwards
                if (oldPriority > insertAtPriority
                        && item.getPriority() >= insertAtPriority
                        && item.getPriority() < oldPriority) {
                    item.setPriority(item.getPriority() + 1);
                    storyDAO.store(item);
                }
            }

        }
        story.setPriority(insertAtPriority);
        storyDAO.store(story);
    }

    public void setHourEntryDAO(HourEntryDAO hourEntryDAO) {
        this.hourEntryDAO = hourEntryDAO;
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

    public void attachStoryToBacklog(Story story, int backlogId)
            throws ObjectNotFoundException {
        this.attachStoryToBacklog(story, backlogId, false);
    }

    public void attachStoryToBacklog(int storyId, int backlogId,
            boolean moveTasks) throws ObjectNotFoundException {
        Story story = this.retrieve(storyId);
        this.attachStoryToBacklog(story, backlogId, moveTasks);
    }

    public void attachStoryToBacklog(Story story, int backlogId,
            boolean moveTasks) throws ObjectNotFoundException {
        Backlog newBacklog = null;
        if (backlogId != 0) {
            newBacklog = backlogDAO.get(backlogId);
            if (newBacklog == null) {
                throw new ObjectNotFoundException("backlog.notFound");
            }
        }
        // story has to have a parent
        if (story.getBacklog() == null && backlogId == 0) {
            throw new IllegalArgumentException("backlog.notFound");
        }

        if (backlogId != 0) {

            if (story.getBacklog() != null) {
                if (story.getBacklog() != newBacklog) {
                    Backlog oldBacklog = story.getBacklog();
                    oldBacklog.getStories().remove(story);
                    story.setBacklog(newBacklog);
                    story.getBacklog().getStories().add(story);
                    for (Task task : story.getTasks()) {
                        if (moveTasks) {
                            task.setIteration((Iteration) newBacklog);
                        } else {
                            task.setStory(null);
                        }
                    }
                    if (!moveTasks) {
                        story.getTasks().clear();
                    } else if (oldBacklog instanceof Iteration) {
                        iterationHistoryEntryBusiness
                                .updateIterationHistory(oldBacklog.getId());
                    }
                    backlogHistoryEntryBusiness.updateHistory(oldBacklog.getId());
                }
            } else {
                story.setBacklog(newBacklog);
                story.getBacklog().getStories().add(story);
            }

            storyDAO.store(story);
            backlogHistoryEntryBusiness.updateHistory(backlogId);
            if (newBacklog instanceof Iteration) {
                iterationHistoryEntryBusiness.updateIterationHistory(backlogId);
            }
        }

        if (story.getBacklog() == null) {
            throw new IllegalArgumentException("story.noIteration");
        }
    }
    
    public int getStoryPointSumByBacklog(Backlog backlog) {
        return storyDAO.getStoryPointSumByBacklog(backlog.getId());
    }

}
