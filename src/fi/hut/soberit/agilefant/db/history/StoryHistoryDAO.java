package fi.hut.soberit.agilefant.db.history;

import java.util.Map;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;

public interface StoryHistoryDAO extends GenericHistoryDAO<Story> {
    public Story retrieveClosestRevision(int storyId, int revisionId);
    
    public Map<Integer, Long> calculateAccessCounts(DateTime start, DateTime end, User user);
}
