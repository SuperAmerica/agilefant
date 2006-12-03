package fi.hut.soberit.agilefant.web;


import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;

public class IterationAction extends ActionSupport {

	private static final long serialVersionUID = -448825368336871703L;
	private int iterationId;
	private Iteration iteration;
	private IterationDAO iterationDAO;
	private DeliverableDAO deliverableDAO;
	private Deliverable deliverable;
	private int deliverableId;

	public String create(){
		Deliverable deliverable =  deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("iteration.deliverableNotFound"));
			return Action.INPUT;
		}
		iterationId = 0;
		iteration = new Iteration();
		return Action.SUCCESS;		
	}
	
	public String edit(){
		iteration = iterationDAO.get(iterationId);
		if (iteration == null){
			super.addActionError(super.getText("iteration.notFound"));
			return Action.INPUT;
		}
		deliverable = iteration.getDeliverable();
		if (deliverable == null){
			super.addActionError(super.getText("iteration.deliverableNotFound"));
			return Action.INPUT;
		}
		deliverableId = deliverable.getId();
		return Action.SUCCESS;
	}
	
	public String store(){
		if (iteration == null){
			super.addActionError(super.getText("iteration.missingForm"));
			return Action.INPUT;			
		}
		deliverable =  deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("iteration.deliverableNotFound"));
			return Action.INPUT;
		}
		Iteration fillable = new Iteration();
		if (iterationId > 0){
		    fillable = iterationDAO.get(iterationId);
			if (iteration == null){
				super.addActionError(super.getText("iteration.notFound"));
				return Action.INPUT;
			}
		}
		this.fillObject(fillable);
		iterationDAO.store(fillable);
		return Action.SUCCESS;
	}
	
	public String delete(){
		iteration = iterationDAO.get(iterationId);
		if (iteration == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		iterationDAO.remove(iteration);
		return Action.SUCCESS;
	}
	
	protected void fillObject(Iteration fillable){
		fillable.setDeliverable(this.deliverable);
		fillable.setName(this.iteration.getName());
		fillable.setDescription(this.iteration.getDescription());
	}

	public int getIterationId() {
		return iterationId;
	}

	public void setIterationId(int iterationId) {
		this.iterationId = iterationId;
	}

	public Iteration getIteration() {
		return iteration;
	}
	
	public void setIteration(Iteration iteration){
		this.iteration = iteration;
	}

	public void setIterationDAO(IterationDAO iterationDAO) {
		this.iterationDAO = iterationDAO;
	}

	public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
	    this.deliverableDAO = deliverableDAO;
	}

	public int getDeliverableId() {
	    return deliverableId;
	}

	public void setDeliverableId(int deliverableId) {
	    this.deliverableId = deliverableId;
	}
}