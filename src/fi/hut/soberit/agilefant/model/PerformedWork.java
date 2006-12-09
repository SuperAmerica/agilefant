package fi.hut.soberit.agilefant.model;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;

@Entity
public class PerformedWork extends EstimateHistoryEvent{
	
	private AFTime effort;
	private WorkType workType;
	
	@ManyToOne
	public WorkType getWorkType() {
		return workType;
	}
	
	public void setWorkType(WorkType workType) {
		this.workType = workType;
	}

	@Type(type="af_time")
	public AFTime getEffort() {
		return effort;
	}

	public void setEffort(AFTime effort) {
		this.effort = effort;
	}
}
