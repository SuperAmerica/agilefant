package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Collection;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import fi.hut.soberit.agilefant.db.EstimateHistoryDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;

public class EstimateHistoryDAOHibernate extends HibernateDaoSupport implements
		EstimateHistoryDAO {

	@SuppressWarnings("unchecked")
	public Collection<EstimateHistoryEvent> getEstimateHistory(Task task) {
		return super
				.getHibernateTemplate()
				.findByNamedParam(
						"from EstimateHistoryEvent event where event.task.id = :id order by event.created",
						"id", new Integer(task.getId()));
	}

	@SuppressWarnings("unchecked")
	public Collection<EstimateHistoryEvent> getEstimateHistory(
			BacklogItem backlogItem) {
		return super
				.getHibernateTemplate()
				.findByNamedParam(
						"from EstimateHistoryEvent event where event.task.backlogItem.id = :id order by event.created",
						"id", new Integer(backlogItem.getId()));
	}

	@SuppressWarnings("unchecked")
	public Collection<EstimateHistoryEvent> getEstimateHistory(
			Iteration iteration) {
		return super
				.getHibernateTemplate()
				.findByNamedParam(
						"from EstimateHistoryEvent event where event.task.backlogItem.backlog.id = :id order by event.created",
						"id", new Integer(iteration.getId()));
	}

	@SuppressWarnings("unchecked")
	public Collection<EstimateHistoryEvent> getEstimateHistory(
			Deliverable deliverable) {
		return super
				.getHibernateTemplate()
				.findByNamedParam(
						"from EstimateHistoryEvent event "
								+ "where event.task.backlogItem.backlog.id = :id "
								+ "or event.task.backlogItem.backlog.deliverable.id = :id "
								+ "order by event.created", "id",
						new Integer(deliverable.getId()));
	}
}
