package fi.hut.soberit.agilefant.db;

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

    public List<Object[]> getResponsiblesByBacklog(Backlog backlog);

    int calculateStoryPointSum(int backlogId);
    
    int calculateDoneStoryPointSum(int backlogId);
    
    /**
     * Calculate the stories story point sum.
     * <p>
     * Includes all child backlogs' stories.
     */
    public int calculateStoryPointSumIncludeChildBacklogs(int backlogId);
    
    public List<Backlog> searchByName(String name);
    
    public List<Backlog> searchByName(String name, Class<?> type);
}
