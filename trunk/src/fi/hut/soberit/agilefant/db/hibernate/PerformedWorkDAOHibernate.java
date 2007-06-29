package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fi.hut.soberit.agilefant.db.PerformedWorkDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;

/**
 * Hibernate implementation of PerformedWorkDAO interface using GenericDAOHibernate.
 */
public class PerformedWorkDAOHibernate extends HibernateDaoSupport implements
		PerformedWorkDAO {

	/** {@inheritDoc} */
	public Collection<PerformedWork> getPerformedWork(Task task) {
		return super.getHibernateTemplate().findByNamedParam(
				"from PerformedWork work where work.task.id = :id order by work.created"
				, "id",
				new Integer(task.getId()));
	}

	/** {@inheritDoc} */
	public Collection<PerformedWork> getPerformedWork(BacklogItem backlogItem) {
		return super.getHibernateTemplate().findByNamedParam(
				"from PerformedWork work where work.task.backlogItem.id = :id order by work.created",
				"id",
				new Integer(backlogItem.getId()));
	}

	/** {@inheritDoc} */
	public Collection<PerformedWork> getPerformedWork(Iteration iteration) {
		return super.getHibernateTemplate().findByNamedParam(
				"from PerformedWork work where work.task.backlogItem.backlog.id = :id order by work.created",
				"id",
				new Integer(iteration.getId()));
	}

	/** {@inheritDoc} */
	public Collection<PerformedWork> getPerformedWork(Deliverable deliverable) {
		return super.getHibernateTemplate().findByNamedParam(
				"from PerformedWork work " +
				"where work.task.backlogItem.backlog.id = :id " +
				"or work.task.backlogItem.backlog.deliverable.id = :id " +
				"order by work.created",
				"id",
				new Integer(deliverable.getId()));		
	}
}
