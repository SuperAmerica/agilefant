package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
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
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
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
        Iteration iteration = null;
        Story story = null;
        
        if (iterationId != null && storyId != null) {
            throw new IllegalArgumentException("Only one parent can be given");
        }
        else if (iterationId != null) {
            iteration = iterationBusiness.retrieve(iterationId);
            task.setIteration(iteration);
            task.setStory(null);
        }
        else {
            story = storyBusiness.retrieve(storyId);
            task.setStory(story);
            task.setIteration(null);
        }
        
        // Set creator, if a new task
        if (task.getId() == 0) {
            task.setCreator(getLoggedInUser());
            task.setCreatedDate(new DateTime().toDate());
        }
        
        // If we are setting the original estimate,
        // set effortLeft accordingly
        if (task.getEffortLeft() == null && task.getOriginalEstimate() != null) {
            task.setEffortLeft(task.getOriginalEstimate());
        }
        
        if (task.getOriginalEstimate() == null && task.getEffortLeft() != null) {
            task.setOriginalEstimate(task.getEffortLeft());
        }
        
        if (task.getState() == TaskState.DONE) {
            task.setEffortLeft(new ExactEstimate(0));
        }
        
        this.populateUserData(task, userIds);
        
        if (task.getId() == 0) {
            int newTaskId = this.create(task);
            storedTask = this.retrieve(newTaskId);
        }
        else {
            this.store(task);
            storedTask = task;
        }
        
        if (iteration != null) {
            iterationHistoryEntryBusiness.updateIterationHistory(iterationId);
        }
        else if (story != null && story.getBacklog() instanceof Iteration) {
            iterationHistoryEntryBusiness.updateIterationHistory(story.getBacklog().getId());
        }
        
        return storedTask;
    }
    
    public Task move(int taskId, int iterationId, int storyId) {
        Task task = retrieve(taskId);
        Iteration iteration = iterationBusiness.retrieve(iterationId);
        Story story = storyBusiness.retrieveIfExists(storyId);
        Iteration oldIteration = null;
        if (task.getIteration() != null) {
            oldIteration = task.getIteration();
            oldIteration.getTasks().remove(task);
        }
        
        task.setIteration(iteration);
        task.setStory(story);
        
        if (oldIteration != null) {
            iterationHistoryEntryBusiness.updateIterationHistory(oldIteration.getId());
        }
        
        iterationHistoryEntryBusiness.updateIterationHistory(iterationId);
        
        return task;
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
        
        if (task.getIteration() != null) {
            iterationHistoryEntryBusiness.updateIterationHistory(task.getIteration().getId());            
        }
        else if (task.getStory().getBacklog() instanceof Iteration){
            iterationHistoryEntryBusiness.updateIterationHistory(((Iteration)task.getStory().getBacklog()).getId());
        }
        
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
    
}