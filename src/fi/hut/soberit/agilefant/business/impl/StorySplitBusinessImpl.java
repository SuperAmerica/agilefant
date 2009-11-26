package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StorySplitBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Story;

@Service("storySplitBusiness")
@Transactional
public class StorySplitBusinessImpl implements StorySplitBusiness {

    @Autowired
    private StoryDAO storyDAO;
    @Autowired
    private StoryBusiness storyBusiness;
    
    @Transactional
    public Story splitStory(Story original, Collection<Story> newStories, Collection<Story> oldChangedStories) {
        if (original == null || newStories.size() == 0) {
            throw new IllegalArgumentException(
                    "Original story and new stories should be given");
        }
        if (original.getId() == 0) {
            throw new RuntimeException("Original story not persisted.");
        }

        persistChildStories(original, newStories);
        this.storyBusiness.storeBatch(oldChangedStories);
        return original;
    }

    /**
     * @TODO: update project histories!
     */
    private void persistChildStories(Story original,
            Collection<Story> newStories) {

        for (Story story : newStories) {
            story.setParent(original);
            //copy responsible from the parent story
            story.getResponsibles().addAll(original.getResponsibles());
            this.storyDAO.create(story);
            this.storyBusiness.rankToBottom(story, story.getBacklog().getId());
            this.storyDAO.store(story);
        }
    }

    @Transactional(readOnly = true)
    public Story getStory(int id) {
        return storyDAO.get(id);
    }

    public void setStoryDAO(StoryDAO storyDAO) {
        this.storyDAO = storyDAO;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }
}
