package fi.hut.soberit.agilefant.db;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Deliverable;

/**
 * Interface for a DAO of a Deliverable.
 * 
 * @see GenericDAO
 */
public interface DeliverableDAO extends GenericDAO<Deliverable> {
	
	/**
	 * Get all currently ongoing deliverables.
	 */
	public Collection<Deliverable> getOngoingDeliverables();
}
