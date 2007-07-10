package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;

public class BacklogAction extends ActionSupport {
 	private static final long serialVersionUID = 8061288993804046816L;
	private int backlogId;
	private BacklogDAO backlogDAO;
	private int backlogItemId;
	private int[] backlogItemIds;
	private int targetBacklogId;
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
	
	/**
	 * Moves selected baclogitems to selected backlog.
	 * @return ERROR or page to return to
	 */
	public String moveSelectedItems(){
		Log logger = LogFactory.getLog(getClass());
		Backlog targetBacklog = this.backlogDAO.get(targetBacklogId);	
		Backlog currentBacklog = this.backlogDAO.get(backlogId);
		
		if(backlogItemIds == null){
			super.addActionError(super.getText("backlogItems.notSelected"));
			return Action.ERROR;
		}
		
		logger.info("Moving " + backlogItemIds.length + " items + " +
				" to backlog: " + targetBacklogId);
		
		if (targetBacklog == null || currentBacklog == null) {
			super.addActionError(super.getText("backlog.notFound"));
			return Action.ERROR;
		}
		
		for (int i = 0; i < this.backlogItemIds.length; i++) {			
			BacklogItem backlogItem = 
				this.backlogItemDAO.get(this.backlogItemIds[i]);
			if (!backlogItem.getBacklog()
					.getBacklogItems().remove(backlogItem)) {
				super.addActionError(super.getText("backlogItem.notFound"));
				return Action.ERROR;
			}
			
			if(!targetBacklog.getBacklogItems().add(backlogItem)) {
				return Action.ERROR;
			}
			
			backlogItem.setBacklog(targetBacklog);
			backlogItemDAO.store(backlogItem);
		}
		
		return this.solveResult(currentBacklog);
	}
	
	protected String solveResult(Backlog backlog){
		if (backlog == null){
			super.addActionError(super.getText("backlog.notFound"));
			return Action.ERROR;
		} else if (backlog instanceof Product){
			return "editProduct";
		} else if (backlog instanceof Deliverable){
			return "editDeliverable";
		} else if (backlog instanceof Iteration){
			return "editIteration";
		}
		super.addActionError(super.getText("backlog.unknownType"));
		return Action.ERROR;		
	}

	public int getBacklogItemId() {
		return backlogItemId;
	}

	public int[] getSelected() {
		return backlogItemIds;
	}
	
	public int getTargetBacklog() {
		return targetBacklogId;
	}
	
	public void setTargetBacklog(int targetBacklogId) {
		this.targetBacklogId = targetBacklogId;
	}
	
	public void setSelected(int[] backlogItemIds) {
		this.backlogItemIds = backlogItemIds;
	}

	public void setBacklogItemId(int backlogItemId) {
		this.backlogItemId = backlogItemId;
	}

	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}
}
