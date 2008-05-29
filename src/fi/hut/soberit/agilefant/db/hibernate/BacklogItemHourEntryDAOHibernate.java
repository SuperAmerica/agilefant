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

import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.Setting;

public class BacklogItemHourEntryDAOHibernate extends GenericDAOHibernate<BacklogItemHourEntry> implements
        BacklogItemHourEntryDAO {

    protected BacklogItemHourEntryDAOHibernate() {
        super(BacklogItemHourEntry.class);
    }


    @SuppressWarnings("unchecked")
    public List<BacklogItemHourEntry> getEntriesByBacklogItem(BacklogItem target) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.eq("backlogItem", target));
        
        return (List<BacklogItemHourEntry>) super.getHibernateTemplate()
                .findByCriteria(criteria);
    }

    @SuppressWarnings("unchecked")
    public List<BacklogItemHourEntry> getSumsByBacklog(Backlog backlog) {

        final String query = "FROM BacklogItemHourEntry as blihe " +
        		"WHERE blihe.backlogItem.backlog = ?";
 

        return (List<BacklogItemHourEntry>)super.getHibernateTemplate()
                                            .find(query, new Object[] 
                                                                    { backlog });
    }


}
