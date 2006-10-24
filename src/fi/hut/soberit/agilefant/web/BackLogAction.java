package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BackLogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.BackLogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Sprint;
import fi.hut.soberit.agilefant.model.Task;

public class BackLogAction extends ActionSupport {
	
	private int backLogId;
	private BackLogItem backLogItem;
	private BackLogItemDAO backLogItemDAO;
	private Collection<BackLogItem> backLogItems;
	private SprintDAO sprintDAO;
	private Sprint sprint;
	private int sprintId;
	
	
	public String getAll(){
	    	backLogItems = new ArrayList<BackLogItem>();
		sprint = sprintDAO.get(sprintId);
		if (sprint == null){
			backLogItems = backLogItemDAO.getAll();
		} else {
		    	backLogItems = sprint.getBackLogs();
		}    
//		backLogItems = backLogItemDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String create(){
		Sprint sprint =  sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("backLogItem.sprintNotFound"));
			return Action.INPUT;
		}		
		backLogId = 0;
		backLogItem = new BackLogItem();
		return Action.SUCCESS;		
	}
	
	public String edit(){
	    
		sprint =  sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("backLogItem.sprintNotFound"));
			return Action.INPUT;
		}
		backLogItem= backLogItemDAO.get(backLogId);
		if (backLogItem == null){
			super.addActionError(super.getText("backLogItem.notFound"));
			return Action.INPUT;
		}	
		return Action.SUCCESS;
	}
	
	public String store(){
	    
		if (backLogItem == null){
			super.addActionError(super.getText("backLogItem.missingForm"));
			return Action.INPUT;			
		}
		sprint =  sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("backLogItem.sprintNotFound"));
			return Action.INPUT;
		}
		BackLogItem fillable = new BackLogItem();
		if (backLogId > 0){
		    fillable = backLogItemDAO.get(backLogId);
			if (backLogItem == null){
				super.addActionError(super.getText("backLogItem.notFound"));
				return Action.INPUT;
			}
		}
	    
		this.fillObject(fillable);
		backLogItemDAO.store(fillable);
		// updating activitytypes here to make listing work correctly after storing
		// - turkka
//		backLogItems = backLogItemDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String delete(){
		backLogItem = backLogItemDAO.get(backLogId);
		if (backLogItem == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		backLogItemDAO.remove(backLogItem);
		return Action.SUCCESS;
	}
	
	protected void fillObject(BackLogItem fillable){
	    	fillable.setSprint(this.sprint);
	    	fillable.setName(this.backLogItem.getName());
		fillable.setDescription(this.backLogItem.getDescription());
	}

	public int getDeliverableId() {
		return backLogId;
	}

	public void setDeliverableId(int deliverableId) {
		this.backLogId = deliverableId;
	}

	public BackLogItem getBackLogItem() {
		return backLogItem;
	}
	
	public void setBackLogItem(BackLogItem backLogItem){
		this.backLogItem = backLogItem;
	}

	public Collection<BackLogItem> getBackLogItems() {
		return backLogItems;
	}

	public void setBackLogItemDAO(BackLogItemDAO backLogItemDAO) {
		this.backLogItemDAO = backLogItemDAO;
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

	public int getBackLogId() {
	    return backLogId;
	}

	public void setBackLogId(int backLogId) {
	    this.backLogId = backLogId;
	}
}