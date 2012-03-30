package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.StoryRankBusiness;
import fi.hut.soberit.agilefant.db.StoryRankDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;
import fi.hut.soberit.agilefant.transfer.StoryTO;

@Service("storyRankBusiness")
@Transactional
public class StoryRankBusinessImpl implements StoryRankBusiness {

    @Autowired
    private StoryRankDAO storyRankDAO;
    @Autowired
    private BacklogBusiness backlogBusiness;

    
    /** {@inheritDoc} */
    public StoryRank getRankByBacklog(Story story, Backlog backlog) {
        return storyRankDAO.retrieveByBacklogAndStory(backlog, story);
    }
    
    /**
     * {@inheritDoc}
     */
    public void rankAbove(Story story, Backlog context, Story upper) {
        if(context instanceof Product) {
            return;
        }
        
        StoryRank rank = this.storyRankDAO.retrieveByBacklogAndStory(context,
                story);
        StoryRank nextRank = this.storyRankDAO.retrieveByBacklogAndStory(
                context, upper);
        if (rank == null) {
            rank = createRank(story, context);
        }
        if (nextRank != null) {
            rankAbove(rank, nextRank);
        }
    }

    /**
     * Create a rank for a story in the given context.
     */
    public StoryRank createRank(Story story, Backlog context) {
        if(context instanceof Product) {
            return null;
        }
        
        StoryRank rank;
        rank = new StoryRank();
        rank.setStory(story);
        rank.setBacklog(context);
        int id = (Integer) this.storyRankDAO.create(rank);
        rank = this.storyRankDAO.get(id);
        return rank;
    }

    private void rankAbove(StoryRank rank, StoryRank next) {
        LinkedList<StoryRank> ranks = prepareRankingContext(rank);

        int nextIndex = ranks.indexOf(next);
        if (nextIndex == -1) {
            ranks.addLast(rank);
        } else {
            ranks.add(nextIndex, rank);
        }

        updateContextRanks(ranks);
    }

    private void rankBelow(StoryRank rank, StoryRank previous) {
        LinkedList<StoryRank> ranks = prepareRankingContext(rank);

        int previousIndex = ranks.indexOf(previous);
        if (previousIndex == -1 || previousIndex == ranks.size() - 1) {
            ranks.addLast(rank);
        } else {
            ranks.add(previousIndex + 1, rank);
        }

        updateContextRanks(ranks);
    }

    private void updateContextRanks(LinkedList<StoryRank> ranks) {
        int currentRankNum = 0;
        for (StoryRank currentRank : ranks) {
            currentRank.setRank(currentRankNum++);
        }
    }

    private LinkedList<StoryRank> prepareRankingContext(StoryRank rank) {
        LinkedList<StoryRank> ranks = retrieveLinkedList(rank);
        if (ranks.contains(rank)) {
            ranks.remove(rank);
        }
        return ranks;
    }

    private LinkedList<StoryRank> retrieveLinkedList(StoryRank rank) {
        LinkedList<StoryRank> ranks = new LinkedList<StoryRank>();
        ranks.addAll(this.storyRankDAO.retrieveRanksByBacklog(rank.getBacklog()));
        return ranks;
    }

    /**
     * Remove item from the linked list.
     */

    private void skipRank(StoryRank rank) {
        LinkedList<StoryRank> ranks = retrieveLinkedList(rank);
        ranks.remove(rank);
        updateContextRanks(ranks);
    }

    /**
     * {@inheritDoc}
     */
    public void rankBelow(Story story, Backlog context, Story upper) {
        if(context instanceof Product) {
            return;
        }
        
        StoryRank rank = this.storyRankDAO.retrieveByBacklogAndStory(context,
                story);
        StoryRank prevRank = this.storyRankDAO.retrieveByBacklogAndStory(
                context, upper);
        if (rank == null) {
            rank = createRank(story, context);
        }
        if (prevRank != null) {
            rankBelow(rank, prevRank);
        }

    }
    
    /**
     * {@inheritDoc}
     */
    public List<StoryTO> retrieveByRankingContext(int backlogId) {
        List<StoryTO> ret = new ArrayList<StoryTO>();
        int current = 0;
        for(Story story : this.retrieveByRankingContext(this.backlogBusiness.retrieve(backlogId))) {
            StoryTO storyto = new StoryTO(story);
            storyto.setRank(current++);
            ret.add(storyto);
        }
        return ret;
    }

    /**
     * {@inheritDoc}
     */
    public List<Story> retrieveByRankingContext(Backlog backlog) {
        List<StoryRank> ranks = this.storyRankDAO.retrieveRanksByBacklog(backlog);
        List<Story> stories = new ArrayList<Story>();
        for(StoryRank rank : ranks ) {
            stories.add(rank.getStory());
        }
        return stories;
    }

    /**
     * {@inheritDoc}
     */
    public void removeRank(Story story, Backlog context) {
        if (context == null) {
            return;
        }
        StoryRank rank = this.storyRankDAO.retrieveByBacklogAndStory(context,story);
        if (rank != null) {
            skipRank(rank);
            this.storyRankDAO.remove(rank);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void removeStoryRanks(Story story) {
        for (StoryRank rank : story.getStoryRanks()) {
            skipRank(rank);
            this.storyRankDAO.remove(rank);
        }
        story.getStoryRanks().clear();
    }

    /**
     * {@inheritDoc}
     */
    public void removeBacklogRanks(Backlog backlog) {
        for (StoryRank rank : backlog.getStoryRanks()) {
            rank.getStory().getStoryRanks().remove(rank);
            this.storyRankDAO.remove(rank);
        }
        backlog.getStoryRanks().clear();
    }

    /**
     * {@inheritDoc}
     */
    public void rankToBottom(Story story, Backlog context) {
        if(context instanceof Product) {
            return;
        }
        
        StoryRank rank = this.storyRankDAO.retrieveByBacklogAndStory(context, story);
        LinkedList<StoryRank> ranks = new LinkedList<StoryRank>();
        ranks.addAll(this.storyRankDAO.retrieveRanksByBacklog(context));
        StoryRank tailRank = null;
        try {
            tailRank = ranks.getLast();
        } catch (Exception e) {

        }
        if (rank == null) {
            rank = createRank(story, context);
        }
        
        if(tailRank != null && (rank.getRank() == tailRank.getRank())){
            //story is already at the bottom
            return;
        }
        
        if (tailRank != null) {
            rankBelow(rank, tailRank);
        }

    }

    public void rankToHead(Story story, Backlog backlog) {
        if(backlog instanceof Product) {
            return;
        }
        
        StoryRank rank = this.storyRankDAO.retrieveByBacklogAndStory(backlog, story);
        
        LinkedList<StoryRank> ranks = new LinkedList<StoryRank>();
        ranks.addAll(this.storyRankDAO.retrieveRanksByBacklog(backlog));
        
        StoryRank topRank = null;
        
        try {
            topRank = ranks.getFirst();
        } catch (Exception e) {
        }
        
        if (rank == null) {
            rank = createRank(story, backlog);
        } 
        else if (rank.getRank() == 0 && (topRank == null || topRank == rank)) {
            //story is already at top
            return;
        }
        
        if (topRank != null) {
            rankAbove(rank, topRank);
        }
    }

    public void setStoryRankDAO(StoryRankDAO storyRankDAO) {
        this.storyRankDAO = storyRankDAO;
    }

    public void fixContext(Backlog backlog) {
        List<Story> ranked = this.retrieveByRankingContext(backlog);
        Set<Integer> storyIds = new HashSet<Integer>();
        
        for(Story story : backlog.getStories()) {
            storyIds.add(story.getId());
        }
        
        for(Story story : ranked) {
            if(!storyIds.contains(story.getId())) {
                this.removeRank(story, backlog);
            }
        }
    }
}
