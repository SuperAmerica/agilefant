package fi.hut.soberit.agilefant.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

@Service("storyHierarchyBusiness")
public class StoryHierarchyBusinessImpl implements StoryHierarchyBusiness {

    @Autowired
    private StoryHierarchyDAO storyHierarchyDAO;
    
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
    
    /*
     * GETTERS AND SETTERS
     */
    
    public void setStoryHierarchyDAO(StoryHierarchyDAO storyHierarchyDAO) {
        this.storyHierarchyDAO = storyHierarchyDAO;
    }

}
