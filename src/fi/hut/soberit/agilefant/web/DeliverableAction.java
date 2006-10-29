package fi.hut.soberit.agilefant.web;

import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;

public class DeliverableAction extends ActionSupport {
	
	private int deliverableId;
	private Deliverable deliverable;
	private DeliverableDAO deliverableDAO;
	private Collection<Deliverable> deliverables;
	
	public String getAll(){
		deliverables = deliverableDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String create(){
		deliverableId = 0;
		deliverable = new Deliverable();
		return Action.SUCCESS;		
	}
	
	public String edit(){
		deliverable = deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public String store(){
		if (deliverable == null){
			super.addActionError(super.getText("activityType.missingForm"));
		}
		Deliverable fillable = new Deliverable();
		if (deliverableId > 0){
			fillable = deliverableDAO.get(deliverableId);
			if (fillable == null){
				super.addActionError(super.getText("activityType.notFound"));
				return Action.ERROR;
			}
		}
		this.fillObject(fillable);
		deliverableDAO.store(fillable);
		// updating activitytypes here to make listing work correctly after storing
		// - turkka
		deliverables = deliverableDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String delete(){
		deliverable = deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		deliverableDAO.remove(deliverable);
		return Action.SUCCESS;
	}
	
	protected void fillObject(Deliverable fillable){
		fillable.setName(deliverable.getName());
		fillable.setDescription(deliverable.getDescription());
	}

	public int getDeliverableId() {
		return deliverableId;
	}

	public void setDeliverableId(int deliverableId) {
		this.deliverableId = deliverableId;
	}

	public Deliverable getDeliverable() {
		return deliverable;
	}
	
	public void setDeliverable(Deliverable deliverable){
		this.deliverable = deliverable;
	}

	public Collection<Deliverable> getDeliverables() {
		return deliverables;
	}

	public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
		this.deliverableDAO = deliverableDAO;
	}
}