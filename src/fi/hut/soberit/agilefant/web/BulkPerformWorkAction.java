package fi.hut.soberit.agilefant.web;

import java.util.Collection;
import java.util.HashSet;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.util.CreateIfNull;
import com.opensymphony.xwork.util.KeyProperty;

import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.WorkType;

public class BulkPerformWorkAction extends TaskEventAction<PerformedWork> {

	private static final long serialVersionUID = -4886214500115776419L;

	private WorkTypeDAO workTypeDAO;

	@CreateIfNull(value = true)
	@KeyProperty(value = "id")
	private Collection<PerformedWork> events = new HashSet<PerformedWork>();

	private TaskEventDAO taskEventDAO;

	@Override
	public void validate() {
		super.validate();

		/*
		 * Don't require effort anymore if (this.getEvent().getEffort() ==
		 * null){
		 * super.addActionError(super.getText("performWork.missingEffort")); }
		 */
		// if (this.getEvent().getNewEstimate() == null){
		// super.addActionError(super.getText("performWork.missingNewEstimate"));
		// }
	}

	@Override
	public String execute() {
		for (PerformedWork event : events) {
			if (event.getId() > 0) {
				fillEvent(event);
				taskEventDAO.store(event);

			}
		}
		return Action.SUCCESS;
	}

	@Override
	protected void doFillEvent(PerformedWork event) {

		WorkType workType = workTypeDAO.get(event.getWorkType().getId());
		event.setWorkType(workType);
		this.getTask().setEffortEstimate(event.getNewEstimate());
	}

	public Collection<PerformedWork> getEvents() {
		return events;
	}

	public void setEvents(Collection<PerformedWork> events) {
		this.events = events;
	}

	public PerformedWork getEvent() {
		return null;
	}

	public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
		this.workTypeDAO = workTypeDAO;
	}

	public TaskEventDAO getTaskEventDAO() {
		return taskEventDAO;
	}

	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}
}
