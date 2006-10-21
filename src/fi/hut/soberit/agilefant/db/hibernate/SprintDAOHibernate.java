package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.model.Sprint;

public class SprintDAOHibernate extends GenericDAOHibernate<Sprint> implements SprintDAO {

	public SprintDAOHibernate(){
		super(Sprint.class);
	}
}
