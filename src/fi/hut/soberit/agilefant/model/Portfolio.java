package fi.hut.soberit.agilefant.model;

import java.util.Collection;
import java.util.HashSet;

public class Portfolio {
	
	public Collection<Deliverable> deliverables = new HashSet<Deliverable>();

	public Collection<Deliverable> getDeliverables() {
		return deliverables;
	}

	public void setDeliverables(Collection<Deliverable> deliverables) {
		this.deliverables = deliverables;
	}
}
