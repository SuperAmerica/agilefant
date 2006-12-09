package fi.hut.soberit.agilefant.web;

import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.TaskEvent;
import fi.hut.soberit.agilefant.model.WorkType;

public class PerformWorkAction extends TaskEventAction {
	
	private WorkTypeDAO workTypeDAO;
	
	@Override
	protected void doFillEvent(TaskEvent event) {
		PerformedWork workEvent = (PerformedWork)event;
		WorkType workType = workTypeDAO.get(workEvent.getWorkType().getId());
		workEvent.setWorkType(workType);
		this.getTask().setEffortEstimate(workEvent.getNewEstimate());
	}

	private PerformedWork event;
	
	public PerformedWork getEvent(){
		if (event == null){
			event = new PerformedWork();
		}
		return event;		
	}
	
	public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
		this.workTypeDAO = workTypeDAO;
	}
}
