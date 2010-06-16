package fi.hut.soberit.agilefant.db.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;

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
        return ((Integer) criteria.list().get(0)).intValue();
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
        return (List<Object[]>) hibernateTemplate.find(hql,
                new Object[] { backlog });
    }

    public int calculateStoryPointSum(int backlogId) {
        Criteria crit = getCurrentSession().createCriteria(Story.class);
        crit.setProjection(Projections.sum("storyPoints"));
        crit.createCriteria("backlog").add(Restrictions.idEq(backlogId));
        Integer result = uniqueResult(crit);
        if (result == null) return 0;
        return result.intValue();
    }
    
    public int calculateStoryPointSumIncludeChildBacklogs(int backlogId) {
        Criteria crit = getCurrentSession().createCriteria(Story.class);
        crit.setProjection(Projections.sum("storyPoints"));
        
        crit.createAlias("backlog", "backlog");
        crit.createAlias("backlog.parent", "parentBacklog",
                CriteriaSpecification.LEFT_JOIN);
        crit.createAlias("backlog.parent.parent", "parentParentBacklog",
                CriteriaSpecification.LEFT_JOIN);
        
        crit.add(Restrictions.or(Restrictions.eq("backlog.id", backlogId),
                Restrictions.or(Restrictions.eq("parentBacklog.id", backlogId),
                        Restrictions.eq("parentParentBacklog.id", backlogId))));
        
        crit.add(Restrictions.isNotNull("storyPoints"));
        
        Integer result = uniqueResult(crit);
        
        if (result == null) return 0;
        return result.intValue();
    }
    
    public List<Backlog> searchByName(String name) {
        Criteria crit = getCurrentSession().createCriteria(Backlog.class);
        crit.add(Restrictions.like("name", name, MatchMode.ANYWHERE));
        return asList(crit);
    }
}
