package fi.hut.soberit.agilefant.web;

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
	
	public String getAll(){
		backLogItems = backLogItemDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String create(){
		backLogId = 0;
		backLogItem = new BackLogItem();
		return Action.SUCCESS;		
	}
	
	public String edit(){
		backLogItem = backLogItemDAO.get(backLogId);
		if (backLogItem == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public String store(){
		if (backLogItem == null){
			super.addActionError(super.getText("activityType.missingForm"));
		}
		BackLogItem fillable = new BackLogItem();
		if (backLogId > 0){
			fillable = backLogItemDAO.get(backLogId);
			if (fillable == null){
				super.addActionError(super.getText("activityType.notFound"));
				return Action.ERROR;
			}
		}
		this.fillObject(fillable);
		backLogItemDAO.store(fillable);
		// updating activitytypes here to make listing work correctly after storing
		// - turkka
		backLogItems = backLogItemDAO.getAll();
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
		fillable.setName(backLogItem.getName());
		fillable.setDescription(backLogItem.getDescription());
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
}