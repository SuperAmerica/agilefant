package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;

public interface WhatsNextEntryDAO extends GenericDAO<WhatsNextEntry> {
    /**
     * Gets the user's "what's next" tasks with rank between and including
     * lower and upper borders.
     * <p>
     * Will not get the iteration's stories' tasks.
     * @param lower lower border of the rank (0 if topmost included)
     * @param upper upper border of the rank
     * @param user the user
     * 
     * @return
     */
    public Collection<WhatsNextEntry> getTasksWithRankBetween(int lower, int upper, User user);
        
    /**
     * Gets the last ranked "what's next" task for given user.
     */
    public WhatsNextEntry getLastTaskInRank(User user);
    
    /**
     * Gets the What's next entry for given user and given task, if exists
     */
    public WhatsNextEntry getWhatsNextEntryFor(User user, Task task);

    /**
     * Gets the What's next entry for given user and given task, if exists
     */
    public Collection<WhatsNextEntry> getWhatsNextEntriesFor(User user);
}
