package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

public class WatchObjectAction extends ActionSupport {
	
	private int taskId;
	private int backlogItemId;
	private TaskDAO taskDAO;
	private BacklogItemDAO backlogItemDAO;
	private boolean watch;
	
	public String execute(){
		User user = SecurityUtil.getLoggedUser();
		if (taskId > 0){
			Task task = taskDAO.get(taskId);
			if (watch){
				task.getWatchers().put(user.getId(), user);
				user.getWatchedTasks().add(task);
			} else {
				task.getWatchers().remove(user.getId());
				user.getWatchedTasks().remove(task);
			}
			taskDAO.store(task);
		} else if (backlogItemId > 0){
			BacklogItem bli = backlogItemDAO.get(backlogItemId);
			if (watch){
				bli.getWatchers().put(user.getId(), user);
				user.getWatchedBacklogItems().add(bli);
			} else {
				bli.getWatchers().remove(user.getId());
				user.getWatchedBacklogItems().remove(bli);
			}
			backlogItemDAO.store(bli);
		}
		return Action.SUCCESS;
	}
	
	public int getBacklogItemId() {
		return backlogItemId;
	}
	
	public void setBacklogItemId(int backlogItemId) {
		this.backlogItemId = backlogItemId;
	}
	
	public int getTaskId() {
		return taskId;
	}
	
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	
	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}
	
	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}
	
	public void setWatch(boolean watch) {
		this.watch = watch;
	}
	
	public void validate(){
		if (taskId < 1 && backlogItemId < 1){
			super.addActionError(super.getText("watch.missingObjectId"));
		} else if (taskId > 0 && taskDAO.get(taskId) == null){
			super.addActionError(super.getText("watch.task.notFound"));
		} else if (backlogItemId > 0 && backlogItemDAO.get(backlogItemId) == null){
			super.addActionError(super.getText("watch.backlogItem.notFound"));
		}
	}
}
