package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.model.IterationGoal;

public class IterationGoalDAOHibernate extends GenericDAOHibernate<IterationGoal> implements IterationGoalDAO {

	public IterationGoalDAOHibernate(){
		super(IterationGoal.class);
	}
}
