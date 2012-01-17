/**
 * Task History for an iteration
 * 
 * @author aborici
 * 
 */

package fi.hut.soberit.agilefant.db.history;

import java.util.List;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;

public interface TaskHistoryDAO extends GenericHistoryDAO<Iteration> {
    public List<AgilefantHistoryEntry> retrieveAllTaskRevisions(Iteration iteration);
}
