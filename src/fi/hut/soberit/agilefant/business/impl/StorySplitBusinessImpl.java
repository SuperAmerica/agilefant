package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StorySplitBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Story;

@Transactional
public class StorySplitBusinessImpl implements StorySplitBusiness {

    private StoryDAO storyDAO;

    @Autowired
    public void setStoryDAO(StoryDAO storyDAO) {
        this.storyDAO = storyDAO;
    }
    
    @Transactional
    public Story splitStory(Story original, Collection<Story> newStories) {
        if (original == null || newStories.size() == 0) {
            throw new IllegalArgumentException("Original story and new stories should be given");
        }
        return new Story();
    }
    
    @Transactional(readOnly = true)
    public Story getStory(int id) {
        return storyDAO.get(id);        
    }
    
}
