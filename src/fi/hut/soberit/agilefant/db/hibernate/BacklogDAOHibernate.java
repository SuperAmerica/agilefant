package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.Backlog;

/**
 * Hibernate implementation of BacklogDAO interface using GenericDAOHibernate.
 */
@Repository("backlogDAO")
public class BacklogDAOHibernate extends GenericDAOHibernate<Backlog> implements
        BacklogDAO {

    public BacklogDAOHibernate() {
        super(Backlog.class);
    }
    
    /** {@inheritDoc} */
    public int getNumberOfChildren(Backlog backlog) {
        DetachedCriteria crit = DetachedCriteria.forClass(Backlog.class);
        crit.add(Restrictions.eq("parent", backlog));
        crit.setProjection(Projections.rowCount());
        Integer numberOfChildren = (Integer)(hibernateTemplate.findByCriteria(crit).get(0));
        return numberOfChildren;
    }
    
    @SuppressWarnings("unchecked")
    public Collection<Backlog> retrieveMultiple(Collection<Integer> idList) {
        if (idList == null || idList.isEmpty()) {
            return new ArrayList<Backlog>();
        }
        DetachedCriteria crit = DetachedCriteria.forClass(Backlog.class);
        crit.add(Restrictions.in("id", idList));
        return hibernateTemplate.findByCriteria(crit);
    }
}
