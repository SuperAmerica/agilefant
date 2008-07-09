package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;


public class BacklogHourEntryDAOHibernate extends GenericDAOHibernate<BacklogHourEntry> implements
BacklogHourEntryDAO {
    
    protected BacklogHourEntryDAOHibernate(){
        super(BacklogHourEntry.class);
    }

    @SuppressWarnings("unchecked")
    public List<BacklogHourEntry> getEntriesByBacklog(Backlog target) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.eq("backlog", target));
        
        return (List<BacklogHourEntry>) super.getHibernateTemplate()
                .findByCriteria(criteria);
    }
    
    // Remove if no need to sum spent effort according to a given backlog 
    @SuppressWarnings("unchecked")
    public List<BacklogHourEntry> getSumsByBacklog(Backlog backlog) {

        final String query = "FROM BacklogHourEntry as bhe " +
                        "WHERE bhe.backlog.backlog = ?"; 

        return (List<BacklogHourEntry>)super.getHibernateTemplate()
                                            .find(query, new Object[] 
                                                                    { backlog });
    }
    
}
