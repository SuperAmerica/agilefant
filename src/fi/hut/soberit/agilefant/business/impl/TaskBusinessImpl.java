package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
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
    public void setTaskDAO(TaskDAO taskDAO) {
        this.genericDAO = taskDAO;
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

    public Collection<ResponsibleContainer> getTaskResponsibles(Task task) {
        Collection<ResponsibleContainer> responsibleContainers = new ArrayList<ResponsibleContainer>();
        Collection<User> storyResponsibles = task.getResponsibles();
        for (User user : storyResponsibles) {
            responsibleContainers.add(new ResponsibleContainer(user, true));
        }
        return responsibleContainers;
    }
    
    /** {@inheritDoc} */
    public Task storeTask(Task task, int iterationId, int storyId, Set<Integer> userIds) {
        Task storedTask;
        Iteration iteration = iterationBusiness.retrieve(iterationId);
        Story story = storyBusiness.retrieveIfExists(storyId);
        
        task.setIteration(iteration);
        task.setStory(story);
        
        // If we are setting the original estimate,
        // set effortLeft accordingly
        if (task.getEffortLeft() == null && task.getOriginalEstimate() != null) {
            task.setEffortLeft(task.getOriginalEstimate());
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
        
        return storedTask;
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
        return task;
    }
    
}