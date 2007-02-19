package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;
import java.util.Collection;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;

public class IterationAction extends ActionSupport implements CRUDAction {

	private static final long serialVersionUID = -448825368336871703L;
	private int iterationId;
	private Iteration iteration;
	private IterationDAO iterationDAO;
	private DeliverableDAO deliverableDAO;
	private Deliverable deliverable;
	private int deliverableId;
	private IterationGoalDAO iterationGoalDAO;
	private int iterationGoalId;

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
//			super.addActionError(super.getText("iteration.notFound"));
//			return Action.INPUT;
//			TODO: demo kludge
			return Action.SUCCESS;
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
		fillable.setEndDate(this.iteration.getEndDate());
		fillable.setStartDate(this.iteration.getStartDate());
	}

	public String moveIterationGoal(){
		Iteration iteration = iterationDAO.get(iterationId);
		IterationGoal iterationGoal = iterationGoalDAO.get(iterationGoalId);
		if (iteration == null){
			super.addActionError(super.getText("iteration.notFound"));
			return Action.ERROR;
		}
		if (iterationGoal == null){
			super.addActionError(super.getText("iterationGoal.notFound"));
		}
		
		iterationGoal.getIteration().getIterationGoals().remove(iterationGoal);
		iteration.getIterationGoals().add(iterationGoal);
		iterationGoal.setIteration(iteration);
		iterationGoalDAO.store(iterationGoal);
		
		return Action.SUCCESS;
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
	
	public Collection<Iteration> getAllIterations() {
		return this.iterationDAO.getAll();
	}

	public int getDeliverableId() {
	    return deliverableId;
	}

	public void setDeliverableId(int deliverableId) {
	    this.deliverableId = deliverableId;
	}

	public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
		this.iterationGoalDAO = iterationGoalDAO;
	}

	public void setIterationGoalId(int iterationGoalId) {
		this.iterationGoalId = iterationGoalId;
	}

	public int getIterationGoalId() {
		return iterationGoalId;
	}
}