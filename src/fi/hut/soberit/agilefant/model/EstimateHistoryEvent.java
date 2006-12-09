package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import org.hibernate.annotations.Type;

@Entity
public class EstimateHistoryEvent extends TaskComment {
	
	private AFTime newEstimate;

	@Type(type="af_time")	
	public AFTime getNewEstimate() {
		return newEstimate;
	}

	public void setNewEstimate(AFTime newEstimate) {
		this.newEstimate = newEstimate;
	}
}
