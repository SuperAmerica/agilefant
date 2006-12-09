package fi.hut.soberit.agilefant.web;

import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.TaskEvent;

public class PerformWorkAction extends TaskEventAction {
	
	@Override
	protected void doFillEvent(TaskEvent event) {
		PerformedWork workEvent = (PerformedWork)event;
		this.getTask().setEffortEstimate(workEvent.getNewEstimate());
	}

	private PerformedWork event;
	
	public PerformedWork getEvent(){
		if (event == null){
			event = new PerformedWork();
		}
		return event;		
	}
}
