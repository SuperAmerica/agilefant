package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskEvent;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public abstract class TaskEventAction<T extends TaskEvent> extends ActionSupport {
	
	private int taskId;
	private TaskDAO taskDAO;
	private TaskEventDAO taskEventDAO;
	private Task task;
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public int getTaskId() {
		return taskId;
	}

	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}

	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}

	public String execute(){
		T event = this.getEvent();
		this.fillEvent(event);
		
		taskEventDAO.store(event);
		return Action.SUCCESS;
	}
	
	protected final void fillEvent(T event){
		task = taskDAO.get(taskId);
		event.setActor(SecurityUtil.getLoggedUser());
		this.doFillEvent(event);
		task.getEvents().add(event);
		event.setTask(task);		
	}
	
	/**
	 * Override this to add your code event filling code.
	 */
	protected void doFillEvent(T event){
	}
	
	public abstract T getEvent();
}
