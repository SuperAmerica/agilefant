package fi.hut.soberit.agilefant.db.hibernate;

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
    public Integer getNumberOfChildren(Backlog backlog) {
        return getNumberOfChildren(backlog.getId());
    }
    
    /** {@inheritDoc} */
    public Integer getNumberOfChildren(int backlogId) {
        DetachedCriteria crit = DetachedCriteria.forClass(Backlog.class);
        crit.add(Restrictions.eq("parent.id", backlogId));
        crit.setProjection(Projections.rowCount());
        return (Integer)hibernateTemplate.findByCriteria(crit).get(0);
    }
}
