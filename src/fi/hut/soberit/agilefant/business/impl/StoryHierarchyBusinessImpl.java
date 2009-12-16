package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

@Service("storyHierarchyBusiness")
public class StoryHierarchyBusinessImpl implements StoryHierarchyBusiness {

    @Autowired
    private StoryHierarchyDAO storyHierarchyDAO;
    
    @Autowired
    private StoryBusiness storyBusiness;
    
    @Transactional(readOnly = true)
    public List<Story> retrieveProjectLeafStories(Project project) {
        return storyHierarchyDAO.retrieveProjectLeafStories(project);
    }

    @Transactional(readOnly = true)
    public List<Story> retrieveProjectRootStories(Project project) {
        return storyHierarchyDAO.retrieveProjectRootStories(project);
    }

    public List<Story> retrieveProductRootStories(Product product) {
        return storyHierarchyDAO.retrieveProductRootStories(product);
    }
    
    @Transactional
    public void changeParentStory(Story story, Story newParent) {
        Story oldParent = story.getParent();
        story.setParent(newParent);
        newParent.getChildren().add(story);
        
        storyBusiness.updateStoryRanks(newParent);
        
        if(oldParent != null) {
            oldParent.getChildren().remove(story);
            storyBusiness.updateStoryRanks(oldParent);
        }
        
    }
    
    /*
     * GETTERS AND SETTERS
     */
    
    public void setStoryHierarchyDAO(StoryHierarchyDAO storyHierarchyDAO) {
        this.storyHierarchyDAO = storyHierarchyDAO;
    }



}
