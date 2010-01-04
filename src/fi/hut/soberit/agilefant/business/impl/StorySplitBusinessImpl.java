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
    public Story splitStory(Story original, Collection<Story> newStories,
            Collection<Story> oldChangedStories) {
        if (original == null || newStories.size() == 0) {
            throw new IllegalArgumentException(
                    "Original story and new stories should be given");
        }
        if (original.getId() == 0) {
            throw new RuntimeException("Original story not persisted.");
        }

        if (!newStories.isEmpty()) {
            storyBusiness.updateStoryRanks(original);
        }

        persistChildStories(original, newStories);
        original.getChildren().addAll(newStories);
        if (oldChangedStories != null) {
            updateChangedStories(oldChangedStories);
        }
        return original;
    }

    /*
     * Backlog changes have to be handled through move story as the story ranks
     * and backlog histories must be updated.
     */
    private void updateChangedStories(Collection<Story> stories) {
        for (Story story : stories) {
            Story oldStory = this.storyBusiness.retrieve(story.getId());
            if (oldStory.getBacklog() != story.getBacklog()) {
                this.storyBusiness.moveStoryToBacklog(oldStory, story
                        .getBacklog());
            }
        }
        this.storyBusiness.storeBatch(stories);
    }

    private void persistChildStories(Story original,
            Collection<Story> newStories) {
        int currentTreeRank = original.getChildren().size();
        for (Story story : newStories) {
            story.setParent(original);
            story.setTreeRank(currentTreeRank++);
            // copy responsible from the parent story
            story.getResponsibles().addAll(original.getResponsibles());
            this.storyBusiness.create(story);
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
