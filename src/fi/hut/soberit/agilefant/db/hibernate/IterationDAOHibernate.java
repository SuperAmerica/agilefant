package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Iteration;

public class IterationDAOHibernate extends GenericDAOHibernate<Iteration> implements IterationDAO {

	public IterationDAOHibernate(){
		super(Iteration.class);
	}
}
