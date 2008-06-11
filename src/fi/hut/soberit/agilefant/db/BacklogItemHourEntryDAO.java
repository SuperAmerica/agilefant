package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;

import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;

public interface BacklogItemHourEntryDAO extends GenericDAO<BacklogItemHourEntry> {
    
    /**
     * Returns a list of all the hour entries for the specified BacklogItem.
     */
    public List<BacklogItemHourEntry> getEntriesByBacklogItem(BacklogItem target);
    
    /**
     * Returns a list of all the hour entries in the specified Backlog's BLIs.
     */
    public List<BacklogItemHourEntry> getSumsByBacklog(Backlog backlog);
}
