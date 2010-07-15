package fi.hut.soberit.agilefant.db.history;

import fi.hut.soberit.agilefant.model.Story;

public interface StoryHistoryDAO extends GenericHistoryDAO<Story> {
    public Story retrieveClosestRevision(int storyId, int revisionId);
}
