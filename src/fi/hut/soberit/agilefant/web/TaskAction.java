package fi.hut.soberit.agilefant.web;

import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Task;

public class TaskAction extends ActionSupport {
	
	private int taskId;
	private Task task;
	private TaskDAO taskDAO;
	private Collection<Task> tasks;
	
	public String getAll(){
		tasks = taskDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String create(){
		taskId = 0;
		task = new Task();
		return Action.SUCCESS;		
	}
	
	public String edit(){
		task = taskDAO.get(taskId);
		if (task == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public String store(){
		if (task == null){
			super.addActionError(super.getText("activityType.missingForm"));
		}
		Task fillable = new Task();
		if (taskId > 0){
			fillable = taskDAO.get(taskId);
			if (fillable == null){
				super.addActionError(super.getText("activityType.notFound"));
				return Action.ERROR;
			}
		}
		this.fillObject(fillable);
		taskDAO.store(fillable);
		// updating activitytypes here to make listing work correctly after storing
		// - turkka
		tasks = taskDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String delete(){
		task = taskDAO.get(taskId);
		if (task == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		taskDAO.remove(task);
		return Action.SUCCESS;
	}
	
	protected void fillObject(Task fillable){
		fillable.setName(task.getName());
		fillable.setDescription(task.getDescription());
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

	public Collection<Task> getTasks() {
		return tasks;
	}

	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}
}