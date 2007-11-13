package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.model.Backlog;

/**
 * Hibernate implementation of BacklogDAO interface using GenericDAOHibernate.
 */
public class BacklogDAOHibernate extends GenericDAOHibernate<Backlog> implements
		BacklogDAO {

	public BacklogDAOHibernate() {
		super(Backlog.class);
	}

}
