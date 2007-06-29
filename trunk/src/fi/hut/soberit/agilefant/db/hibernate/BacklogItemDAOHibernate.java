package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;

/**
 * Hibernate implementation of BacklogItemDAO interface using GenericDAOHibernate.
 */
public class BacklogItemDAOHibernate extends GenericDAOHibernate<BacklogItem> implements BacklogItemDAO {

	public BacklogItemDAOHibernate(){
		super(BacklogItem.class);
	}
}
