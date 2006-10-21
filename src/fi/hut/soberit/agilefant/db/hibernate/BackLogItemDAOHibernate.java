package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.BackLogItemDAO;
import fi.hut.soberit.agilefant.model.BackLogItem;

public class BackLogItemDAOHibernate extends GenericDAOHibernate<BackLogItem> implements BackLogItemDAO {

	public BackLogItemDAOHibernate(){
		super(BackLogItem.class);
	}
}
