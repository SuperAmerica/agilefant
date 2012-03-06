package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.IterationToken;

/**
 * This class defines an interface for database access 
 * on behalf of the "IterationToken" model class.
 * 
 * @see fi.hut.soverit.agilefant.IterationToken
 * 
 * @author Dustin Fennell
 *
 */
public interface IterationTokenDAO extends GenericDAO<IterationToken> {
    
    public boolean isValidToken(String token);
    
    public boolean hasToken(int iterationId);
    
    public int getIterationIdFromToken(String token);
    
}
