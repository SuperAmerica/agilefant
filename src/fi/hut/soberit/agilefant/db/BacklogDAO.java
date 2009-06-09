package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;

/**
 * Interface for a DAO of a Backlog.
 * 
 * @see GenericDAO
 */
public interface BacklogDAO extends GenericDAO<Backlog> {

    /**
     * Gets the number of backlog's child backlogs.
     */
    public int getNumberOfChildren(Backlog backlog);

    public Collection<Backlog> retrieveMultiple(Collection<Integer> idList);

    // TODO: REFACTOR
    public List<Object[]> getResponsiblesByBacklog(Backlog backlog);

    int calculateStoryPointSum(int backlogId);

}
