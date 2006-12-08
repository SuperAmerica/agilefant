package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;

public class BacklogItemAction extends ActionSupport implements CRUDAction {
	

	private static final long serialVersionUID = -4289013472775815522L;
	private BacklogDAO backlogDAO;
	private BacklogItemDAO backlogItemDAO;
	private int backlogId;
	private int backlogItemId;
	private BacklogItem backlogItem;
	private Backlog backlog;
	private Collection<BacklogItem> backlogItems = new ArrayList<BacklogItem>();

	public String create() {
		backlogItemId = 0;
		backlogItem = new BacklogItem();
		backlog = backlogDAO.get(backlogId);
		backlogId = backlog.getId();
		return Action.SUCCESS;
	}

	public String delete() {
		backlogItem = backlogItemDAO.get(backlogItemId);
		backlogId = backlogItem.getId();
		backlogItemDAO.remove(backlogItemId);
		return Action.SUCCESS;
	}

	public String edit() {
		backlogItem = backlogItemDAO.get(backlogItemId);
		if (backlogItem == null){
			super.addActionError(super.getText("backlogItem.notFound"));
			return Action.ERROR;
		}
		backlog = backlogItem.getBacklog();
		backlogId = backlog.getId();
		return Action.SUCCESS;
	}

	public String store() {
		BacklogItem storable = new BacklogItem();
		if (backlogItemId > 0){
			storable = backlogItemDAO.get(backlogItemId);
			if (storable == null){
				super.addActionError(super.getText("backlogItem.notFound"));
				return Action.ERROR;
			}
		}
		this.fillStorable(storable);
		if (super.hasActionErrors()){
			return Action.ERROR;
		}
		backlogItemDAO.store(storable);
		return Action.SUCCESS;
	}
	
	protected void fillStorable(BacklogItem storable){
		if (backlogId > 0 && storable.getId() == 0){
			backlog = backlogDAO.get(backlogId);
			if (backlog == null){
				super.addActionError(super.getText("backlog.notFound"));
			}
		}
		storable.setName(this.backlogItem.getName());
		storable.setDescription(this.backlogItem.getDescription());
		storable.setAllocatedEffort(this.backlogItem.getAllocatedEffort());
		storable.setPriority(this.backlogItem.getPriority());
		if (storable.getId() == 0){
			storable.setBacklog(backlog);
		}		
	}

	public Backlog getBacklog() {
		return backlog;
	}

	public void setBacklog(Backlog backlog) {
		this.backlog = backlog;
	}

	public int getBacklogId() {
		return backlogId;
	}

	public void setBacklogId(int backlogId) {
		this.backlogId = backlogId;
	}

	public BacklogItem getBacklogItem() {
		return backlogItem;
	}

	public void setBacklogItem(BacklogItem backlogItem) {
		this.backlogItem = backlogItem;
	}

	public int getBacklogItemId() {
		return backlogItemId;
	}

	public void setBacklogItemId(int backlogItemId) {
		this.backlogItemId = backlogItemId;
	}

	public Collection<BacklogItem> getBacklogItems() {
		return backlogItems;
	}

	public void setBacklogItems(Collection<BacklogItem> backlogItems) {
		this.backlogItems = backlogItems;
	}

	public void setBacklogDAO(BacklogDAO backlogDAO) {
		this.backlogDAO = backlogDAO;
	}

	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}
}
