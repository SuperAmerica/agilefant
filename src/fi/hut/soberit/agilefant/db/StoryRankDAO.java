package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;

public interface StoryRankDAO extends GenericDAO<StoryRank> {
    StoryRank retrieveHeadByBacklog(Backlog backlog);

    StoryRank retrieveByBacklogAndStory(Backlog backlog, Story story);
    
    StoryRank retrieveTailByBacklog(Backlog backlog);
}
