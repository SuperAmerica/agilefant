package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import org.hibernate.Criteria;

import fi.hut.soberit.agilefant.db.IterationTokenDAO;
import fi.hut.soberit.agilefant.model.IterationToken;

/**
 * This class  
 * 
 * @see fi.hut.soberit.agilefant.db.IteartionTokenDAO
 * @see fi.hut.soberit.agilefant.model.IteartionToken
 * 
 * @author Dustin Fennell
 *
 */

@Repository("iterationTokenDAO")
public class IterationTokenDAOHibernate extends GenericDAOHibernate<IterationToken> implements IterationTokenDAO {

    public IterationTokenDAOHibernate() {
        super(IterationToken.class);
    }

    @Override
    public boolean isValidToken(String token) {
        Criteria crit = this.getCurrentSession().createCriteria(IterationToken.class);
        
        return false;
    }

    @Override
    public boolean hasToken(int iterationId) {
        
        return false;
    }

    @Override
    public int getIterationIdFromToken(String token) {
        
        return 0;
    }
    
    
    
}
