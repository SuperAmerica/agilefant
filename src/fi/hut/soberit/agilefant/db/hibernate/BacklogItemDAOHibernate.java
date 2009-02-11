package fi.hut.soberit.agilefant.db.hibernate;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;

/**
 * Hibernate implementation of BacklogItemDAO interface using
 * GenericDAOHibernate.
 */
public class BacklogItemDAOHibernate extends GenericDAOHibernate<BacklogItem>
        implements BacklogItemDAO {

    public BacklogItemDAOHibernate() {
        super(BacklogItem.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<BacklogItem> nonDoneTopLevelBacklogItems(List<Backlog> backlogs) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.ne("state", State.DONE));
        criteria.add(Restrictions.isNull("parentBli"));
        criteria.add(Restrictions.in("backlog", backlogs));
        List<BacklogItem> items = (List<BacklogItem>)super.getHibernateTemplate()
        .findByCriteria(criteria);
        return items;
    }
    

    @SuppressWarnings("unchecked")
    public List<BacklogItem> backlogItemChildren(int fatherId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.eq("parentBli.id", fatherId));
        return (List<BacklogItem>)super.getHibernateTemplate()
        .findByCriteria(criteria);
    }
}
