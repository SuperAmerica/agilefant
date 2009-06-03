package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.IterationDataContainer;
import fi.hut.soberit.agilefant.util.StoryTO;

@Service("iterationBusiness")
@Transactional
public class IterationBusinessImpl extends GenericBusinessImpl<Iteration> implements
        IterationBusiness {

    private IterationDAO iterationDAO;

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
        
        // 1. Set iteration's stories as transfer objects
        iterationData.getStories().addAll(iterationContentsASTOs(iteration));
        
        // 2. Set the tasks without a story
        iterationData.setTasksWithoutStory(
                iterationDAO.getTasksWithoutStoryForIteration(iteration));
        
        return iterationData;
    }
    
    private Collection<Story> iterationContentsASTOs(Iteration iteration) {
        Collection<Story> stories = new ArrayList<Story>();
        for (Story story : iteration.getStories()) {
            stories.add(new StoryTO(story));
        }
        return stories;
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
