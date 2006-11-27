package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AssignEvent;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class AssignTaskAction extends ActionSupport {
	
	private int assigneeId;
	private int taskId;
	private UserDAO userDAO;
	private TaskDAO taskDAO;	
	
	public String execute(){
		User assignee = userDAO.get(assigneeId);
		Task task = taskDAO.get(taskId);
		
		AssignEvent event = new AssignEvent();
		event.setActor(SecurityUtil.getLoggedUser());
		event.setTask(task);
		event.setNewAssignee(assignee);
		event.setOldAssignee(task.getAssignee());
		task.setAssignee(assignee);
		task.getEvents().add(event);
		
		taskDAO.store(task);
		
		return Action.SUCCESS;
	}
	
	public int getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(int assigneeId) {
		this.assigneeId = assigneeId;
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

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}
