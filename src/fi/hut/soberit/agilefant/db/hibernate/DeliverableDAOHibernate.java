package fi.hut.soberit.agilefant.db.hibernate;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.model.Deliverable;

public class DeliverableDAOHibernate extends GenericDAOHibernate<Deliverable> implements DeliverableDAO {

	public DeliverableDAOHibernate(){
		super(Deliverable.class);
	}

	public Collection<Deliverable> getOngoingDeliverables() {
		Date current = Calendar.getInstance().getTime();
		return super.getHibernateTemplate().find("from Deliverable d where d.startDate <= ? and d.endDate >= ? order by d.product.name ASC, d.endDate", 
				new Object[]{current, current});
	}
}
