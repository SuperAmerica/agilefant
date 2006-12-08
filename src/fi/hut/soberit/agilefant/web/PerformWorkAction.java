package fi.hut.soberit.agilefant.web;

import java.util.Date;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.WorkTypeDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.PerformedWork;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class PerformWorkAction extends ActionSupport {

	private static final long serialVersionUID = 1435602610355587814L;
	private int taskId;
	private AFTime amount; // check unit	
	private int workTypeId;
	private TaskDAO taskDAO;
	private WorkTypeDAO workTypeDAO;
	private TaskEventDAO taskEventDAO;
	
	public String execute(){
		Task task = taskDAO.get(taskId);
		PerformedWork event = new PerformedWork();
		event.setActor(SecurityUtil.getLoggedUser());
		event.setTask(task);
		event.setEffort(amount);
		task.getEvents().add(event);
		taskEventDAO.store(event);
		taskDAO.store(task);
		return Action.SUCCESS;
	}

	public AFTime getAmount() {
		return amount;
	}

	public void setAmount(AFTime amount) {
		this.amount = amount;
	}

	public TaskDAO getTaskDAO() {
		return taskDAO;
	}

	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public WorkTypeDAO getWorkTypeDAO() {
		return workTypeDAO;
	}

	public void setWorkTypeDAO(WorkTypeDAO workTypeDAO) {
		this.workTypeDAO = workTypeDAO;
	}

	public int getWorkTypeId() {
		return workTypeId;
	}

	public void setWorkTypeId(int workTypeId) {
		this.workTypeId = workTypeId;
	}

	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}
}
