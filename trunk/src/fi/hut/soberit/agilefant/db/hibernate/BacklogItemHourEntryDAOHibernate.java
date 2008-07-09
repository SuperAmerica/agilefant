package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;

public class BacklogItemHourEntryDAOHibernate extends GenericDAOHibernate<BacklogItemHourEntry> implements
        BacklogItemHourEntryDAO {

    protected BacklogItemHourEntryDAOHibernate() {
        super(BacklogItemHourEntry.class);
    }


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<BacklogItemHourEntry> getEntriesByBacklogItem(BacklogItem target) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.eq("backlogItem", target));
        
        return (List<BacklogItemHourEntry>) super.getHibernateTemplate()
                .findByCriteria(criteria);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<BacklogItemHourEntry> getSumsByBacklog(Backlog backlog) {

        final String query = "FROM BacklogItemHourEntry as blihe " +
        		"WHERE blihe.backlogItem.backlog = ?";
 

        return (List<BacklogItemHourEntry>)super.getHibernateTemplate()
                                            .find(query, new Object[] 
                                                                    { backlog });
    }


}
