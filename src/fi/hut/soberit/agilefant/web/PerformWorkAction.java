package fi.hut.soberit.agilefant.web;

import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.WorkType;

public class PerformWorkAction extends TaskEventAction<PerformedWork> {

	private WorkTypeDAO workTypeDAO;

	private PerformedWork event;

	@Override
	public void validate() {
		super.validate();

		/*
		 * Don't require effort anymore if (this.getEvent().getEffort() ==
		 * null){
		 * super.addActionError(super.getText("performWork.missingEffort")); }
		 */
		if (this.getEvent().getNewEstimate() == null) {
			this.getEvent().setNewEstimate(new AFTime("0h"));
			// super.addActionError(super.getText("performWork.missingNewEstimate"));
		}
	}

	@Override
	protected void doFillEvent(PerformedWork event) {
		WorkType workType = workTypeDAO.get(event.getWorkType().getId());
		event.setWorkType(workType);
		this.getTask().setEffortEstimate(event.getNewEstimate());
	}

	public PerformedWork getEvent() {
		if (event == null) {
			event = new PerformedWork();
		}
		return event;
	}

	public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
		this.workTypeDAO = workTypeDAO;
	}
}
