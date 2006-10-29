package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.model.Iteration;

public class SprintDAOHibernate extends GenericDAOHibernate<Iteration> implements SprintDAO {

	public SprintDAOHibernate(){
		super(Iteration.class);
	}
}
