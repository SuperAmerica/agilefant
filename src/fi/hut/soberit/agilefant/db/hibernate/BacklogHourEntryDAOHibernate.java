package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.Setting;


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
