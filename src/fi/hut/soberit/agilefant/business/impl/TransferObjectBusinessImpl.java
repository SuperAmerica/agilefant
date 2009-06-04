package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;
import fi.hut.soberit.agilefant.util.StoryTO;
import fi.hut.soberit.agilefant.util.TaskTO;

@Service("transferObjectBusiness")
public class TransferObjectBusinessImpl implements TransferObjectBusiness {

    @Autowired
    private ProjectBusiness projectBusiness;
    
    /** {@inheritDoc} */
    public Collection<StoryTO> constructIterationDataWithUserData(
            Iteration iteration, Collection<User> assignedUsers) {
        Collection<StoryTO> iterationStories = new ArrayList<StoryTO>();
        
        for (Story story : iteration.getStories()) {
            StoryTO storyTO = new StoryTO(story);
            storyTO.getTasks().clear();
            
            for (Task task : story.getTasks()) {
                TaskTO taskTO = this.constructTaskTO(task, assignedUsers);
                storyTO.getTasks().add(taskTO);
            }
            iterationStories.add(storyTO);
        }
        return iterationStories;
    }
    
    /** {@inheritDoc} */
    public TaskTO constructTaskTO(Task task, Collection<User> assignedUsers) {
        TaskTO taskTO = new TaskTO(task);
        
        for (User responsible : taskTO.getResponsibles()) {
            ResponsibleContainer rc
                = new ResponsibleContainer(responsible,
                        assignedUsers.contains(responsible));
            taskTO.getUserData().add(rc);
        }
        
        return taskTO;
    }
    
    /** {@inheritDoc} */
    public TaskTO constructTaskTO(Task task) {
        Collection<User> assignedUsers
            = projectBusiness.getAssignedUsers((Project)task.getIteration().getParent());
        return this.constructTaskTO(task, assignedUsers);
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }
    
}
