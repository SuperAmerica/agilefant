package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.Setting;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;

public class HourEntryDAOHibernate extends GenericDAOHibernate<HourEntry> implements
        HourEntryDAO {

    protected HourEntryDAOHibernate() {
        super(HourEntry.class);
    }
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("all")
    public List<HourEntry> getEntriesByTarget(TimesheetLoggable target) {    
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.eq("targetType", target.timesheetType()));
        criteria.add(Restrictions.eq("targetId", target.getId()));
        
        return super.getHibernateTemplate().findByCriteria(criteria);       
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("all")
    public Map<Integer, Integer> getSumsByBacklog(Backlog backlog) {
        Map<Integer, Integer> sums = new HashMap<Integer, Integer>();
        // TODO: blaah.
        return sums;
    }


}
