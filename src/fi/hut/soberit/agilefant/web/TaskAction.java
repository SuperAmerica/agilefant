package fi.hut.soberit.agilefant.web;

import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;

public class TaskAction extends ActionSupport {
	
	private int taskId;
	private int backlogItemId;
	private Task task;
	private TaskDAO taskDAO;
	private BacklogItemDAO backlogItemDAO;
		
	public String create(){
		taskId = 0;
		task = new Task();
		return Action.SUCCESS;		
	}
	
	public String edit(){
		task = taskDAO.get(taskId);
		if (task == null){
			super.addActionError(super.getText("task.notFound"));
			return Action.ERROR;
		}
		backlogItemId = task.getBacklogItem().getId();
		return Action.SUCCESS;
	}
	
	public String store(){
		Task storable = new Task();
		if (taskId > 0){
			storable = taskDAO.get(taskId);
			if (storable == null){
				super.addActionError(super.getText("task.notFound"));
				return Action.ERROR;
			}
		}			
		this.fillStorable(storable);
		
		if (super.hasActionErrors()){
			return Action.ERROR;
		}		
		taskDAO.store(storable);
		return Action.SUCCESS;
	}
	
	public String delete(){		
		task = taskDAO.get(taskId);
		if (task == null){
			super.addActionError(super.getText("task.notFound"));
			return Action.ERROR;
		}
		BacklogItem backlogItem = task.getBacklogItem();
		backlogItemId = backlogItem.getId();
		backlogItem.getTasks().remove(task);
		task.setBacklogItem(null);		
		taskDAO.remove(task);
		return Action.SUCCESS;
	}
	
	protected void fillStorable(Task storable){
		if (storable.getBacklogItem() == null){
			BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
			if (backlogItem == null){
				super.addActionError(super.getText("backlogItem.notFound"));
				return;
			}
			storable.setBacklogItem(backlogItem);
			backlogItem.getTasks().add(storable);
		}
		storable.setName(task.getName());
		storable.setDescription(task.getDescription());
	}

	public int getDeliverableId() {
		return taskId;
	}

	public void setDeliverableId(int deliverableId) {
		this.taskId = deliverableId;
	}

	public Task getTask() {
		return task;
	}
	
	public void setDeliverable(Task task){
		this.task = task;
	}

	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
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

	public void setTask(Task task) {
		this.task = task;
	}

	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}
}