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

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
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
            Set<Integer> responsibles, int priority) throws ObjectNotFoundException {
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
        if(story.getBacklog() != null) {
            story.getBacklog().getStories().remove(story);
        }
        Collection<Task> tasks = story.getTasks();
        if(tasks != null) {
            for(Task item : tasks) {
                item.setStory(null); 
            }
        }
        storyDAO.remove(storyId);
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

        storable.setPriority(dataItem.getPriority());
        storable.setState(dataItem.getState());

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
            int persistedId = (Integer) storyDAO.create(storable);
            persisted = storyDAO.get(persistedId);
        } else {
            storyDAO.store(storable);
            persisted = storable;
        }
        if (persisted.getBacklog() instanceof Iteration) {
            updateStoryPriority(persisted, priority);
        }
        // if(!historyUpdated) {
        // historyBusiness.updateBacklogHistory(backlog.getId());
        // }
        return persisted;
    }

    public void moveStoryToBacklog(Story story, Backlog backlog) {

        Backlog oldBacklog = story.getBacklog();
        oldBacklog.getStories().remove(story);
        story.setBacklog(backlog);
        backlog.getStories().add(story);
        // historyBusiness.updateBacklogHistory(oldBacklog.getId());
        // historyBusiness.updateBacklogHistory(backlog.getId());

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
            if (task.getState() == State.DONE) {
                doneTasks += 1;
            }
        }
        metrics.setDoneTasks(doneTasks);
        metrics.setTotalTasks(tasks);
        return metrics;
    }
    
    // 090605 Reko: Copied from update iteration goal priority
    public void updateStoryPriority(Story story, int insertAtPriority) {
        if(insertAtPriority == story.getPriority()) {
            return;
        }
        if(story.getBacklog() == null || !(story.getBacklog() instanceof Iteration)) {
            throw new IllegalArgumentException("backlog.notFound");
        }
        Iteration iter = (Iteration)story.getBacklog();
        if(iter.getStories().size() == 0) {
            throw new IllegalArgumentException("story.notFound");
        }
        int oldPriority = story.getPriority();
        
        for(Story item : iter.getStories()) {
            //drop new goal to its place
            if(oldPriority == -1) {
                if(item.getPriority() >= insertAtPriority) {
                    item.setPriority(item.getPriority() + 1);
                    storyDAO.store(item);
                }
            } else {
                //when prioritizing downwards raise all goals by one which are between the old and new priorities 
                if(oldPriority < insertAtPriority && 
                        item.getPriority() > oldPriority && 
                        item.getPriority() <= insertAtPriority) {
                    item.setPriority(item.getPriority() - 1);
                    storyDAO.store(item);
                }
                //vice versa when prioritizing upwards
                if(oldPriority > insertAtPriority &&
                        item.getPriority() >= insertAtPriority &&
                        item.getPriority() < oldPriority) {
                    item.setPriority(item.getPriority() + 1);
                    storyDAO.store(item);
                }
            }

        }
        story.setPriority(insertAtPriority);
        storyDAO.store(story);
    }


    @Transactional(readOnly = true)
    public StoryMetrics calculateMetrics(int storyId) {
        return storyDAO.calculateMetrics(storyId);
    }

    @Transactional(readOnly = true)
    public StoryMetrics calculateMetricsWithoutStory(int iterationId) {
        return storyDAO.calculateMetricsWithoutStory(iterationId);
    }
    
    public void attachStoryToIteration(Story story, int iterationId) throws ObjectNotFoundException {
        this.attachStoryToIteration(story, iterationId, false);
    }
    public void attachStoryToIteration(int storyId, int iterationId, boolean moveTasks) throws ObjectNotFoundException {
        Story story = this.retrieve(storyId);
        this.attachStoryToIteration(story, iterationId, moveTasks);
    }
    public void attachStoryToIteration(Story story, int iterationId, boolean moveTasks) throws ObjectNotFoundException {
        Iteration newIteration = null;
        if(iterationId != 0) {
            newIteration = iterationDAO.get(iterationId);
            if(newIteration == null) {
                throw new ObjectNotFoundException("iteration.notFound");
            }
        }
        // story has to have a parent 
        if(story.getBacklog() == null && iterationId == 0) {
            throw new IllegalArgumentException("iteration.notFound");
        }
        if(story.getBacklog() != null && iterationId != 0) {
            if(story.getBacklog() != newIteration) {
                story.getBacklog().getStories().remove(story);
                story.setBacklog(newIteration);
                story.getBacklog().getStories().add(story);
                for(Task task : story.getTasks()) {
                    if(moveTasks) {
                        task.setIteration(newIteration);
                    } else {
                        task.setStory(null);
                    }
                }
                if(!moveTasks) {
                    story.getTasks().clear();
                }
            }
        } else if(iterationId != 0) {
            story.setBacklog(newIteration);
            story.getBacklog().getStories().add(story);
        }
        
        if(story.getBacklog() == null) {
            throw new IllegalArgumentException("story.noIteration");
        }
    }

    

}
