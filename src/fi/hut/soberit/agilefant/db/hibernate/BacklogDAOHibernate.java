package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
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
        Criteria criteria = getCurrentSession().createCriteria(Backlog.class);
        criteria.add(Restrictions.idEq(backlog.getId()));
        criteria.createCriteria("children");
        criteria.setProjection(Projections.rowCount());
        return ((Integer)criteria.list().get(0)).intValue();
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
    
    @SuppressWarnings("unchecked")
    public List<Object[]> getResponsiblesByBacklog(Backlog backlog) {
        String hql = "from Story as story left outer join story.responsibles as resp WHERE story.backlog = ?";
        return (List<Object[]>)hibernateTemplate.find(hql, new Object[] {backlog});
    }
}
