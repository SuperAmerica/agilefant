package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.IterationHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.ProjectBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.IterationMetrics;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.transfer.TaskTO;
import fi.hut.soberit.agilefant.util.IterationDataContainer;

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
    private HourEntryBusiness hourEntryBusiness;
    @Autowired
    private IterationHistoryEntryBusiness iterationHistoryEntryBusiness;

    @Autowired
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.genericDAO = iterationDAO;
        this.iterationDAO = iterationDAO;
    }

    @Transactional(readOnly = true)
    public IterationDataContainer getIterationContents(int iterationId) {

        IterationDataContainer iterationData = new IterationDataContainer();
        Iteration iteration = this.retrieve(iterationId);
        
        // Get the project's assignees
        Collection<User> assignedUsers = projectBusiness
            .getAssignedUsers((Project)iteration.getParent());
        
        // 1. Set iteration's stories as transfer objects and include story metrics
        for (StoryTO storyTO : transferObjectBusiness.constructIterationDataWithUserData(iteration, assignedUsers)) {
            storyTO.setMetrics(storyBusiness.calculateMetrics(storyTO));
            iterationData.getStories().add(storyTO);
        }
        
        // 2. Set the tasks without a story
        Collection<Task> tasksWithoutStory
            = iterationDAO.getTasksWithoutStoryForIteration(iteration);
        
        iterationData.setTasksWithoutStory(new ArrayList<Task>());
        
        for (Task task : tasksWithoutStory) {
            TaskTO taskTO = transferObjectBusiness.constructTaskTO(task, assignedUsers);
            taskTO.setEffortSpent(hourEntryBusiness.calculateSum(taskTO.getHourEntries()));
            iterationData.getTasksWithoutStory().add(taskTO);
        }
        
        return iterationData;
    }
    
    public IterationMetrics getIterationMetrics(Iteration iteration) {
        if (iteration == null) {
            throw new IllegalArgumentException("Iteration must be not null.");
        }
        
        IterationMetrics metrics = new IterationMetrics();
        
        IterationHistoryEntry latestHistoryEntry
            = iterationHistoryEntryBusiness.retrieveLatest(iteration);
        
        // 1. Set original estimate and effort left
        metrics.setOriginalEstimate(new ExactEstimate(latestHistoryEntry.getOriginalEstimateSum()));
        metrics.setEffortLeft(new ExactEstimate(latestHistoryEntry.getEffortLeftSum()));
        
        // 2. Set story points
        metrics.setStoryPoints(storyBusiness.getStoryPointSumByBacklog(iteration));
        
        // 3. Set spent effort
        long spentEffort = hourEntryBusiness.calculateSumOfIterationsHourEntries(iteration);
        metrics.setSpentEffort(new ExactEstimate(spentEffort));
        
        
        return metrics;
    }
    
    
    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public void setProjectBusiness(ProjectBusiness projectBusiness) {
        this.projectBusiness = projectBusiness;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }

    public void setHourEntryBusiness(HourEntryBusiness hourEntryBusiness) {
        this.hourEntryBusiness = hourEntryBusiness;
    }

    public void setIterationHistoryEntryBusiness(
            IterationHistoryEntryBusiness iterationHistoryEntryBusiness) {
        this.iterationHistoryEntryBusiness = iterationHistoryEntryBusiness;
    }

}
