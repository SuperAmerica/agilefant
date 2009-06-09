package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.TaskHourEntryDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskHourEntry;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.HourEntryTO;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

@Service("transferObjectBusiness")
@Transactional
public class TransferObjectBusinessImpl implements TransferObjectBusiness {

    @Autowired
    private ProjectBusiness projectBusiness;
    
    @Autowired
    private TaskHourEntryDAO taskHourEntryDAO;
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public Collection<StoryTO> constructIterationDataWithUserData(
            Iteration iteration, Collection<User> assignedUsers) {
        Collection<StoryTO> iterationStories = new ArrayList<StoryTO>();
        
        for (Story story : iteration.getStories()) {
            StoryTO storyTO = this.constructStoryTO(story, assignedUsers);
            storyTO.getTasks().clear();
            
            for (Task task : story.getTasks()) {
                TaskTO taskTO = this.constructTaskTO(task, assignedUsers);
                storyTO.getTasks().add(taskTO);
            }
            iterationStories.add(storyTO);
        }
        return iterationStories;
    }
    
    private Collection<HourEntryTO> constructHourEntries(Task task) {
        Collection<TaskHourEntry> hourEntries = taskHourEntryDAO.retrieveByTask(task);
        if(hourEntries == null) {
            return null;
        }
        Collection<HourEntryTO> toEntries = new ArrayList<HourEntryTO>();

        for(TaskHourEntry t : hourEntries) {
            toEntries.add(new HourEntryTO(t));
        }
        
        return toEntries;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public TaskTO constructTaskTO(Task task, Collection<User> assignedUsers) {
        TaskTO taskTO = new TaskTO(task, constructHourEntries(task));
        
        for (User responsible : taskTO.getResponsibles()) {
            ResponsibleContainer rc
                = new ResponsibleContainer(responsible,
                        assignedUsers.contains(responsible));
            taskTO.getUserData().add(rc);
        }
        
        return taskTO;
    }
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public StoryTO constructStoryTO(Story story, Collection<User> assignedUsers) {
        StoryTO storyTO = new StoryTO(story);
        
        for (User responsible : storyTO.getResponsibles()) {
            ResponsibleContainer rc
                = new ResponsibleContainer(responsible,
                        assignedUsers.contains(responsible));
            storyTO.getUserData().add(rc);
        }
        
        return storyTO;
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

    public void setTaskHourEntryDAO(TaskHourEntryDAO taskHourEntryDAO) {
        this.taskHourEntryDAO = taskHourEntryDAO;
    }

}
