package fi.hut.soberit.agilefant.db.history;

import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;

public interface BacklogHistoryDAO extends GenericHistoryDAO<Backlog> {
    public List<AgilefantHistoryEntry> retrieveAddedStories(Backlog backlog);
    public List<AgilefantHistoryEntry> retrieveDeletedStories(Backlog backlog);
    public List<AgilefantHistoryEntry> retrieveModifiedStories(Backlog backlog);
}
