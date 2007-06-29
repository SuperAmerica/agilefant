package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.EstimateHistoryEvent;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

/**
 * Task Action
 * 
 * @author khel
 */
public class TaskAction extends ActionSupport implements CRUDAction {

	private static final long serialVersionUID = -8560828440589313663L;
	private int taskId;
	private int backlogItemId;
	private Task task;
	private TaskDAO taskDAO;
	private TaskEventDAO taskEventDAO;
	private BacklogItemDAO backlogItemDAO;
	private UserDAO userDAO;
		
	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}

	/**
	 * Creates a new task.
	 * 
	 * @return Action.SUCCESS
	 */
	public String create(){
		taskId = 0;
		task = new Task();
		return Action.SUCCESS;		
	}
	
	/**
	 * Fetches a task for editing (based on taskId set)
	 * 
	 * @return Action.SUCCESS, if task was found or Action.ERROR if task wasn't found
	 */
	public String edit(){
		task = taskDAO.get(taskId);
		if (task == null){
			super.addActionError(super.getText("task.notFound"));
			return Action.ERROR;
		}
		backlogItemId = task.getBacklogItem().getId();
		return Action.SUCCESS;
	}
	
	/**
	 * Stores the task (a new task created with create() or an old one fetched with edit()
	 * 
	 * @return Action.SUCCESS if task is saved ok or Action.ERROR if there's something wrong. (more information with getActionErrors())
	 */
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
	
	/**
	 * Deletes a task  (based on taskId set)
	 * @return Action.SUCCESS if task was deleted or Action.ERROR if task wasn't found
	 */
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
			storable.setCreator(SecurityUtil.getLoggedUser());
		}
		if (task.getAssignee() != null && task.getAssignee().getId() > 0){
			User assignee = userDAO.get(task.getAssignee().getId());
			storable.setAssignee(assignee);
		}
		if(task.getName().equals("")) {
			super.addActionError(super.getText("task.missingName"));
			return;			
		}
		storable.setPriority(task.getPriority());
		storable.setStatus(task.getStatus());
		storable.setName(task.getName());
		storable.setDescription(task.getDescription());
		
		AFTime oldEstimate = storable.getEffortEstimate();
		AFTime newEstimate = task.getEffortEstimate();
		
		if (storable.getId() == 0 || 
			oldEstimate == null && newEstimate != null ||			
			(oldEstimate != null && !oldEstimate.equals(newEstimate))){
			
			EstimateHistoryEvent event = new EstimateHistoryEvent();
			event.setActor(SecurityUtil.getLoggedUser());
			event.setNewEstimate(newEstimate);
			storable.setEffortEstimate(newEstimate);
			taskDAO.store(storable);
			event.setTask(storable);
			storable.getEvents().add(event);
			taskEventDAO.store(event);
		}
	}

	// TODO - should this be so?
	public int getDeliverableId() {
		return taskId;
	}
	// TODO - should this be so?
	public void setDeliverableId(int deliverableId) {
		this.taskId = deliverableId;
	}

	public Task getTask() {
		return task;
	}
	
	// TODO - is this used/needed?
	public void setDeliverable(Task task){
		this.task = task;
	}

	public void setTaskDAO(TaskDAO taskDAO) {
		this.taskDAO = taskDAO;
	}
	
	protected TaskDAO getTaskDAO() {
		return this.taskDAO;
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

	/**
	 * Set the task id (used by edit() and delete() -methods)
	 * @param taskId Id of the wanted task.
	 */
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	public void setUserDAO(UserDAO userDAO) {
		this.userDAO = userDAO;
	}
}