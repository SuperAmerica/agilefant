package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.IterationDataContainer;

@Service("iterationBusiness")
@Transactional
public class IterationBusinessImpl extends GenericBusinessImpl<Iteration> implements
        IterationBusiness {

    private IterationDAO iterationDAO;
    
    @Autowired
    private StoryBusiness storyBusiness;

    @Autowired
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.genericDAO = iterationDAO;
        this.iterationDAO = iterationDAO;
    }
   
    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }

    @Transactional(readOnly = true)
    public IterationDataContainer getIterationContents(int iterationId,
            boolean excludeTasks) {

        IterationDataContainer iterationData = new IterationDataContainer();
        Iteration iteration = this.retrieve(iterationId);
        
        // 1. Set iteration's stories
        iterationData.getStories().addAll(iteration.getStories());
        
        // 2. Set the tasks without a story
        iterationData.setTasksWithoutStory(
                iterationDAO.getTasksWithoutStoryForIteration(iteration));
        
        return iterationData;
    }
    
    
    /*
    public IterationDataContainer getIterationContents(Iteration iter, boolean excludeBacklogItems) {
        List<IterationGoal> goals = this.iterationGoalDAO.getGoalsByIteration(iter);
        
        List<BacklogItem> blis = backlogItemBusiness.getBacklogItemsByBacklogWithCache(iter);
        
        IterationDataContainer iterData = new IterationDataContainer();
        iterData.setIterationGoals(goals);
        
        if(excludeBacklogItems) {
            return iterData;
        }
        //calculate efforts from pre-fetched backlog items
        for(IterationGoal goal : goals) {
            goal.setBacklogItems(new ArrayList<BacklogItem>());
            for(BacklogItem bli : blis) {
                if(bli.getIterationGoal() == goal) {
                    if(bli.getEffortLeft() != null) {
                        goal.getMetrics().getEffortLeft().add(bli.getEffortLeft());
                    }
                    if(bli.getEffortSpent() != null) {
                        goal.getMetrics().getEffortSpent().add(bli.getEffortSpent());
                    }
                    if(bli.getOriginalEstimate() != null) {
                        goal.getMetrics().getOriginalEstimate().add(bli.getOriginalEstimate());
                    }
                    goal.getMetrics().addTask(bli);
                    goal.getBacklogItems().add(bli);
                }
            }
        }
        List<BacklogItem> itemsWithoutGoal = new ArrayList<BacklogItem>();
        for(BacklogItem item : blis) {
            if(item.getIterationGoal() == null) {
                itemsWithoutGoal.add(item);
            }
        }
        iterData.setItemsWithoutGoal(itemsWithoutGoal);
        return iterData;
    }*/

}
