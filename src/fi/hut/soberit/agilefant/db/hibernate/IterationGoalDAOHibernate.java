package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.model.IterationGoal;

/**
 * Hibernate implementation of IterationGoalDAO interface using
 * GenericDAOHibernate.
 */
public class IterationGoalDAOHibernate extends
		GenericDAOHibernate<IterationGoal> implements IterationGoalDAO {

	public IterationGoalDAOHibernate() {
		super(IterationGoal.class);
	}
}
