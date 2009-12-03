package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;

public interface StoryRankBusiness {

    List<Story> retrieveByRankingContext(Backlog backlog);

    void rankBelow(Story story, Backlog context, Story upper);

    void rankBelow(Story story, Backlog context, Backlog fromContext,
            Story upper);

    void rankAbove(Story story, Backlog context, Story upper);

    void rankAbove(Story story, Backlog context, Backlog fromContext,
            Story upper);
    
    void removeRank(Story story, Backlog context);
}
