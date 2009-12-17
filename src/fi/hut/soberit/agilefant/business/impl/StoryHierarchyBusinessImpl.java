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
    public void moveUnder(Story story, Story refernece) {
        Story oldParent = story.getParent();
        story.setParent(refernece);
        refernece.getChildren().add(story);
        
        storyBusiness.updateStoryRanks(refernece);
        
        if(oldParent != null) {
            oldParent.getChildren().remove(story);
            storyBusiness.updateStoryRanks(oldParent);
        }
        
    }
    
    public void moveAfter(Story story, Story reference) {
        if(story.getParent() != reference.getParent()) {
            if(reference.getParent() != null) {
                moveUnder(story, reference);
            } 
        }
        
    }

    public void moveBefore(Story story, Story reference) {
        if(story.getParent() != reference.getParent()) {
            if(reference.getParent() != null) {
                moveUnder(story, reference);
            } 
        }
        
    }
    
    /*
     * GETTERS AND SETTERS
     */
    
    public void setStoryHierarchyDAO(StoryHierarchyDAO storyHierarchyDAO) {
        this.storyHierarchyDAO = storyHierarchyDAO;
    }
}
