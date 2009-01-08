package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
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
    public List<BacklogItem> productNonDoneTopLevelBacklogItems(int productId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(this
                .getPersistentClass());
        criteria.add(Restrictions.ne("state", State.DONE));
        criteria.add(Restrictions.isNull("parentBli"));
        List<BacklogItem> items = (List<BacklogItem>)super.getHibernateTemplate()
        .findByCriteria(criteria);
        List<BacklogItem> ret = new ArrayList<BacklogItem>();
        for(BacklogItem bli : items) {
            if(bli.getProduct().getId() == productId) {
                ret.add(bli);
            }
        }
        return ret;
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
