package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Sprint;
import fi.hut.soberit.agilefant.model.Task;

public class BacklogAction extends ActionSupport {
	
	private int backlogId;
	private BacklogItem backlogItem;
	private BacklogItemDAO backlogItemDAO;
	private Collection<BacklogItem> backlogItems;
	private SprintDAO sprintDAO;
	private Sprint sprint;
	private int sprintId;
	
	
	public String getAll(){
	    	backlogItems = new ArrayList<BacklogItem>();
		sprint = sprintDAO.get(sprintId);
		if (sprint == null){
			backlogItems = backlogItemDAO.getAll();
		} else {
		    	backlogItems = sprint.getBacklogs();
		}    
//		backlogItems = backlogItemDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String create(){
		Sprint sprint =  sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("backlogItem.sprintNotFound"));
			return Action.INPUT;
		}		
		backlogId = 0;
		backlogItem = new BacklogItem();
		return Action.SUCCESS;		
	}
	
	public String edit(){
	    
		sprint =  sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("backlogItem.sprintNotFound"));
			return Action.INPUT;
		}
		backlogItem= backlogItemDAO.get(backlogId);
		if (backlogItem == null){
			super.addActionError(super.getText("backlogItem.notFound"));
			return Action.INPUT;
		}	
		return Action.SUCCESS;
	}
	
	public String store(){
	    
		if (backlogItem == null){
			super.addActionError(super.getText("backlogItem.missingForm"));
			return Action.INPUT;			
		}
		sprint =  sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("backlogItem.sprintNotFound"));
			return Action.INPUT;
		}
		BacklogItem fillable = new BacklogItem();
		if (backlogId > 0){
		    fillable = backlogItemDAO.get(backlogId);
			if (backlogItem == null){
				super.addActionError(super.getText("backlogItem.notFound"));
				return Action.INPUT;
			}
		}
	    
		this.fillObject(fillable);
		backlogItemDAO.store(fillable);
		// updating activitytypes here to make listing work correctly after storing
		// - turkka
//		backlogItems = backlogItemDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String delete(){
		backlogItem = backlogItemDAO.get(backlogId);
		if (backlogItem == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		backlogItemDAO.remove(backlogItem);
		return Action.SUCCESS;
	}
	
	protected void fillObject(BacklogItem fillable){
	    	fillable.setSprint(this.sprint);
	    	fillable.setName(this.backlogItem.getName());
		fillable.setDescription(this.backlogItem.getDescription());
	}

	public int getDeliverableId() {
		return backlogId;
	}

	public void setDeliverableId(int deliverableId) {
		this.backlogId = deliverableId;
	}

	public BacklogItem getBacklogItem() {
		return backlogItem;
	}
	
	public void setBacklogItem(BacklogItem backlogItem){
		this.backlogItem = backlogItem;
	}

	public Collection<BacklogItem> getBacklogItems() {
		return backlogItems;
	}

	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	public Sprint getSprint() {
	    return sprint;
	}

	public void setSprint(Sprint sprint) {
	    this.sprint = sprint;
	}

	public SprintDAO getSprintDAO() {
	    return sprintDAO;
	}

	public void setSprintDAO(SprintDAO sprintDAO) {
	    this.sprintDAO = sprintDAO;
	}

	public int getSprintId() {
	    return sprintId;
	}

	public void setSprintId(int sprintId) {
	    this.sprintId = sprintId;
	}

	public int getBacklogId() {
	    return backlogId;
	}

	public void setBacklogId(int backlogId) {
	    this.backlogId = backlogId;
	}
}
