package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.IterationDataContainer;
import fi.hut.soberit.agilefant.util.StoryMetrics;
import fi.hut.soberit.agilefant.util.StoryTO;
import fi.hut.soberit.agilefant.util.TaskTO;

@Service("iterationBusiness")
@Transactional
public class IterationBusinessImpl extends GenericBusinessImpl<Iteration> implements
        IterationBusiness {

    private IterationDAO iterationDAO;
    
    @Autowired
    private TransferObjectBusiness transferObjectBusiness;
    @Autowired
    private ProjectBusiness projectBusiness;
    @Autowired
    private StoryBusiness storyBusiness;

    @Autowired
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.genericDAO = iterationDAO;
        this.iterationDAO = iterationDAO;
    }

    @Transactional(readOnly = true)
    public IterationDataContainer getIterationContents(int iterationId,
            boolean excludeTasks) {

        IterationDataContainer iterationData = new IterationDataContainer();
        Iteration iteration = this.retrieve(iterationId);
        
        // Get the project's assignees
        Collection<User> assignedUsers = projectBusiness
            .getAssignedUsers((Project)iteration.getParent());
        
        // 1. Set iteration's stories as transfer objects
        iterationData.getStories().addAll(
                transferObjectBusiness.constructIterationDataWithUserData(iteration, assignedUsers));
        
        // 2. Set the tasks without a story
        Collection<Task> tasksWithoutStory
            = iterationDAO.getTasksWithoutStoryForIteration(iteration);
        
        iterationData.setTasksWithoutStory(new ArrayList<Task>());
        
        for (Task task : tasksWithoutStory) {
            TaskTO taskTO = transferObjectBusiness.constructTaskTO(task, assignedUsers);
            iterationData.getTasksWithoutStory().add(taskTO);
        }
        
        return iterationData;
    }
    
    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }
    private Collection<Story> iterationContentsASTOs(Iteration iteration) {
        Collection<Story> stories = new ArrayList<Story>();
        for (Story story : iteration.getStories()) {
            StoryTO storyTO = new StoryTO(story);
            storyTO.setMetrics(storyBusiness.calculateMetrics(story));
            stories.add(storyTO);
        }
        return stories;
    }
    
}
