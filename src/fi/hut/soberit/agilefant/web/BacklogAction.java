package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Product;

public class BacklogAction extends ActionSupport {
	
	private int backlogId;
	private BacklogDAO backlogDAO;
	private int backlogItemId;
	private BacklogItemDAO backlogItemDAO;
	
	public String edit(){
		Backlog backlog = backlogDAO.get(backlogId);
		return solveResult(backlog);
	}

	public int getBacklogId() {
		return backlogId;
	}

	public void setBacklogId(int backlogId) {
		this.backlogId = backlogId;
	}

	public void setBacklogDAO(BacklogDAO backlogDAO) {
		this.backlogDAO = backlogDAO;
	}
	
	public String moveBacklogItem(){
		Backlog backlog = backlogDAO.get(backlogId);
		BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
		if (backlog == null){
			super.addActionError(super.getText("backlog.notFound"));
			return Action.ERROR;
		}
		if (backlogItem == null){
			super.addActionError(super.getText("backlogItem.notFound"));
		}
		
		backlogItem.getBacklog().getBacklogItems().remove(backlogItem);
		backlog.getBacklogItems().add(backlogItem);
		backlogItem.setBacklog(backlog);
		backlogItemDAO.store(backlogItem);
		
		return this.solveResult(backlog);
	}
	
	protected String solveResult(Backlog backlog){
		if (backlog == null){
			super.addActionError(super.getText("backlog.notFound"));
			return Action.ERROR;
		} else if (backlog instanceof Product){
			return "editProduct";
		} else if (backlog instanceof Deliverable){
			return "editDeliverable";
		}
		super.addActionError(super.getText("backlog.unknownType"));
		return Action.ERROR;		
	}

	public int getBacklogItemId() {
		return backlogItemId;
	}

	public void setBacklogItemId(int backlogItemId) {
		this.backlogItemId = backlogItemId;
	}

	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}
}
