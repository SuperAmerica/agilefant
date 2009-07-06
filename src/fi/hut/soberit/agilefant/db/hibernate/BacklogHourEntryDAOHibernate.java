package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;

@Repository("backlogHourEntryDAO")
public class BacklogHourEntryDAOHibernate extends GenericDAOHibernate<BacklogHourEntry> implements
        BacklogHourEntryDAO {

    public BacklogHourEntryDAOHibernate() {
        super(BacklogHourEntry.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<BacklogHourEntry> retrieveByBacklog(Backlog target) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.eq("backlog", target));
        
        return (List<BacklogHourEntry>) hibernateTemplate
                .findByCriteria(criteria);
    }

}
