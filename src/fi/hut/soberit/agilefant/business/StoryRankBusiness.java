package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.StoryTO;

public interface StoryRankBusiness {

    /**
     * Retrieve stories in the ranking order by backlog. Note that this method
     * may not return all the stories contained in the backlog, only the stories
     * that have rank will be returned.
     */
    List<Story> retrieveByRankingContext(Backlog backlog);

    /**
     * Get the story's rank in the given backlog.
     */
    int getRankByBacklog(Story story, Backlog backlog);
    
    /**
     * Rank the given story below the given upper story with in the backlog
     * context.
     */
    void rankBelow(Story story, Backlog context, Story upper);

    /**
     * Rank the given story above the lower story within the backlog context.
     */
    void rankAbove(Story story, Backlog context, Story lower);

    /**
     * Rank the given story to the bottom of the given context.
     */
    void rankToBottom(Story story, Backlog context);

    /**
     * Remove rank from the given story in the given context.
     */
    void removeRank(Story story, Backlog context);
    
    void removeStoryRanks(Story story);
    
    void removeBacklogRanks(Backlog backlog);
    /**
     * Rank to the top.
     */
    void rankToHead(Story story, Backlog backlog);
    
    /**
     * @see retrieveByRankingContext(Backlog backlog);
     */
    public List<StoryTO> retrieveByRankingContext(int backlogId);

}
