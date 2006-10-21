package fi.hut.soberit.agilefant.db.hibernate;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.model.Deliverable;

public class DeliverableDAOHibernate extends GenericDAOHibernate<Deliverable> implements DeliverableDAO {

	public DeliverableDAOHibernate(){
		super(Deliverable.class);
	}
}
