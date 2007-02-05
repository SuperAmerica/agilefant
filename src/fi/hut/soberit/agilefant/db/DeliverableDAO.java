package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Deliverable;

public interface DeliverableDAO extends GenericDAO<Deliverable> {
	
	public Collection<Deliverable> getOngoingDeliverables();
}
