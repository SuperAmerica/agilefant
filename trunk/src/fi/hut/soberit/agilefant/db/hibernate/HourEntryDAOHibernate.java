package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.User;

public class HourEntryDAOHibernate extends GenericDAOHibernate<HourEntry> implements
        HourEntryDAO{

    protected HourEntryDAOHibernate() {
        super(HourEntry.class);
    }
    
    /**
     * {@inheritDoc}
     */
    public AFTime getEffortSumByUserAndTimeInterval(User user, Date start, Date end) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        
        criteria.add(Restrictions.between("date", start, end));
        criteria.add(Restrictions.eq("user", user));
        criteria.setProjection(Projections.sum("timeSpent"));
        
        return (AFTime) super.getHibernateTemplate()
                            .findByCriteria(criteria).get(0);
    }
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public List<HourEntry> getHourEntriesByUser(User user) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        
        criteria.add(Restrictions.eq("user", user));
        return (List<HourEntry>)super.getHibernateTemplate()
                            .findByCriteria(criteria);
    }
}
