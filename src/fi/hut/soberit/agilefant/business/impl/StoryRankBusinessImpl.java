package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryRankBusiness;
import fi.hut.soberit.agilefant.db.StoryRankDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;

@Service("storyRankBusiness")
@Transactional
public class StoryRankBusinessImpl implements StoryRankBusiness {

    @Autowired
    private StoryRankDAO storyRankDAO;

    /**
     * {@inheritDoc}
     */
    public void rankAbove(Story story, Backlog context, Story upper) {
        StoryRank rank = this.storyRankDAO.retrieveByBacklogAndStory(context,
                story);
        StoryRank nextRank = this.storyRankDAO.retrieveByBacklogAndStory(
                context, upper);
        if (rank != null) {
            skipRank(rank);
        } else {
            rank = createRank(story, context);
        }
        if (nextRank != null) {
            addRank(rank, nextRank.getPrevious(), nextRank);
        }
    }

    /**
     * Create a rank for a story in the given context.
     */
    private StoryRank createRank(Story story, Backlog context) {
        StoryRank rank;
        rank = new StoryRank();
        rank.setStory(story);
        rank.setBacklog(context);
        int id = (Integer) this.storyRankDAO.create(rank);
        rank = this.storyRankDAO.get(id);
        return rank;
    }

    /**
     * {@inheritDoc}
     */
    public void rankAbove(Story story, Backlog context, Backlog fromContext,
            Story upper) {
        handleContextSwitch(story, context, fromContext);
        this.rankAbove(story, context, upper);
    }

    /**
     * Move ranked story from a context to another context. In the case of
     * duplicate rank, the rank from the old context will be removed.
     */
    private void handleContextSwitch(Story story, Backlog context,
            Backlog fromContext) {
        StoryRank oldRank = this.storyRankDAO.retrieveByBacklogAndStory(
                fromContext, story);
        StoryRank newRank = this.storyRankDAO.retrieveByBacklogAndStory(
                context, story);
        if (oldRank != null && newRank != null) {
            this.storyRankDAO.remove(oldRank);
        } else if (oldRank != null) {
            oldRank.setBacklog(context);
            skipRank(oldRank);
            this.storyRankDAO.store(oldRank);
        }
    }

    /**
     * Remove item from the linked list.
     */
    private void skipRank(StoryRank rank) {
        if (rank.getNext() != null) {
            rank.getNext().setPrevious(rank.getPrevious());
        }
        if (rank.getPrevious() != null) {
            rank.getPrevious().setNext(rank.getNext());
        }
    }

    /**
     * Add item to the linked list.
     */
    private void addRank(StoryRank rank, StoryRank previous, StoryRank next) {
        if (previous != null) {
            previous.setNext(rank);
        }
        if (next != null) {
            next.setPrevious(rank);
        }
        rank.setPrevious(previous);
        rank.setNext(next);
    }

    /**
     * {@inheritDoc}
     */
    public void rankBelow(Story story, Backlog context, Story upper) {
        StoryRank rank = this.storyRankDAO.retrieveByBacklogAndStory(context,
                story);
        StoryRank prevRank = this.storyRankDAO.retrieveByBacklogAndStory(
                context, upper);
        if (rank != null) {
            skipRank(rank);
        } else {
            rank = createRank(story, context);
        }
        if (prevRank != null) {
            addRank(rank, prevRank, prevRank.getNext());
        }

    }

    /**
     * {@inheritDoc}
     */
    public void rankBelow(Story story, Backlog context, Backlog fromContext,
            Story upper) {
        handleContextSwitch(story, context, fromContext);
        rankBelow(story, context, upper);

    }

    /**
     * {@inheritDoc}
     */
    public List<Story> retrieveByRankingContext(Backlog backlog) {
        StoryRank currentRank = this.storyRankDAO
                .retrieveHeadByBacklog(backlog);
        List<Story> result = new ArrayList<Story>();

        while (currentRank != null) {
            result.add(currentRank.getStory());
            currentRank = currentRank.getNext();
        }
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void removeRank(Story story, Backlog context) {
        StoryRank rank = this.storyRankDAO.retrieveByBacklogAndStory(context,
                story);
        if (rank != null) {
            skipRank(rank); //fix the linked list first
            this.storyRankDAO.remove(rank);
        }
    }

    public void setStoryRankDAO(StoryRankDAO storyRankDAO) {
        this.storyRankDAO = storyRankDAO;
    }
}
