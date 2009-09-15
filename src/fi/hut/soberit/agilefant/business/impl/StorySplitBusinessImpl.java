package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogHistoryEntryBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StorySplitBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

@Service("storySplitBusiness")
@Transactional
public class StorySplitBusinessImpl implements StorySplitBusiness {

    @Autowired
    private StoryDAO storyDAO;
    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private BacklogHistoryEntryBusiness backlogHistoryEntryBusiness;
    
    @Transactional
    public Story splitStory(Story original, Collection<Story> newStories) {
        if (original == null || newStories.size() == 0) {
            throw new IllegalArgumentException(
                    "Original story and new stories should be given");
        }
        if (original.getId() == 0) {
            throw new RuntimeException("Original story not persisted.");
        }

        persistChildStories(original, newStories);
        updateOriginalStoryBacklog(original);
        return original;
    }

    private void updateOriginalStoryBacklog(Story original) {
        Backlog oldParentBacklog = original.getBacklog();
        Backlog newBacklog = null;
        if (oldParentBacklog instanceof Project) {
            newBacklog = oldParentBacklog.getParent();
        } else if (oldParentBacklog instanceof Iteration) {
            newBacklog = oldParentBacklog.getParent().getParent();
        }
        // story should be moved moved to a product backlog
        if (newBacklog != null) {
            //will handle possible iteration history updates and
            //backlog history updates
            this.storyBusiness.moveStoryToBacklog(original, newBacklog);
        } else {
            // story and child stories are in a product backlog, update the
            // backlog history
            this.backlogHistoryEntryBusiness.updateHistory(original.getBacklog()
                    .getId());
        }
    }

    private void persistChildStories(Story original,
            Collection<Story> newStories) {
        Story lastStoryInRank = this.storyDAO.getLastStoryInRank(original
                .getBacklog());
        // there will always be "last story in rank" as the original story is in
        // this backlog
        int currentChildStoryRank = lastStoryInRank.getRank();

        for (Story story : newStories) {
            story.setParent(original);
            story.setBacklog(original.getBacklog());
            //copy responsible from the parent story
            story.getResponsibles().addAll(original.getResponsibles());
            story.setRank(++currentChildStoryRank);
            this.storyDAO.create(story);
        }
    }

    @Transactional(readOnly = true)
    public Story getStory(int id) {
        return storyDAO.get(id);
    }

    public void setStoryDAO(StoryDAO storyDAO) {
        this.storyDAO = storyDAO;
    }

    public void setBacklogHistoryEntryBusiness(
            BacklogHistoryEntryBusiness backlogHistoryEntryBusiness) {
        this.backlogHistoryEntryBusiness = backlogHistoryEntryBusiness;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }
}
