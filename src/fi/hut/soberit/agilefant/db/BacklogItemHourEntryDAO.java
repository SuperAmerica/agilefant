package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;

public interface BacklogItemHourEntryDAO extends GenericDAO<BacklogItemHourEntry> {
    public List<BacklogItemHourEntry> getEntriesByBacklogItem(BacklogItem target);
    public Map<Integer,Integer> getSumsByBacklog(Backlog backlog);
}
