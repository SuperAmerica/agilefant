package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * A Portfolio is a collection of deliverables, not an entity bean.
 */
public class Portfolio {

	public Collection<Deliverable> deliverables = new HashSet<Deliverable>();

	public Collection<Deliverable> getDeliverables() {
		return deliverables;
	}

	public void setDeliverables(Collection<Deliverable> deliverables) {
		this.deliverables = deliverables;
	}
}
