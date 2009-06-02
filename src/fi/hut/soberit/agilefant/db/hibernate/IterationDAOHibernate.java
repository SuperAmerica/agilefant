package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;

/**
 * Hibernate implementation of IterationDAO interface using GenericDAOHibernate.
 */
@Repository("iterationDAO")
public class IterationDAOHibernate extends GenericDAOHibernate<Iteration> implements
        IterationDAO {

    public IterationDAOHibernate() {
        super(Iteration.class);
    }

    @SuppressWarnings("unchecked")
    public Collection<Task> getTasksWithoutStoryForIteration(Iteration iteration) {
        DetachedCriteria crit = DetachedCriteria.forClass(Task.class);
        crit.add(Restrictions.eq("iteration", iteration));
        crit.add(Restrictions.isNull("story"));
        return hibernateTemplate.findByCriteria(crit);
    }
}
