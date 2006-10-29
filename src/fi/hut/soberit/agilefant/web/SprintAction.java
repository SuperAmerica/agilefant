package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.WorkType;

public class SprintAction extends ActionSupport {
	
	private int sprintId;
	private Iteration sprint;
	private SprintDAO sprintDAO;
	private Collection<Iteration> sprints;
	private DeliverableDAO deliverableDAO;
	private Deliverable deliverable;
	private int deliverableId;
	
	public String getAll(){
	    	sprints = new ArrayList<Iteration>();
		deliverable = deliverableDAO.get(deliverableId);
		if (deliverable == null){
			sprints = sprintDAO.getAll();
		} else {
			sprints = deliverable.getIterations();
		}
//		sprints = sprintDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String create(){
		Deliverable deliverable =  deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("sprint.deliverableNotFound"));
			return Action.INPUT;
		}
		sprintId = 0;
		sprint = new Iteration();
		return Action.SUCCESS;		
	}
	
	public String edit(){
		sprint = sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("sprint.notFound"));
			return Action.INPUT;
		}
		deliverable = sprint.getDeliverable();
		if (deliverable == null){
			super.addActionError(super.getText("sprint.deliverableNotFound"));
			return Action.INPUT;
		}
		deliverableId = deliverable.getId();
		return Action.SUCCESS;
	}
	
	public String store(){
		if (sprint == null){
			super.addActionError(super.getText("sprint.missingForm"));
			return Action.INPUT;			
		}
		deliverable =  deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("sprint.deliverableNotFound"));
			return Action.INPUT;
		}
		Iteration fillable = new Iteration();
		if (sprintId > 0){
		    fillable = sprintDAO.get(sprintId);
			if (sprint == null){
				super.addActionError(super.getText("sprint.notFound"));
				return Action.INPUT;
			}
		}
		this.fillObject(fillable);
		sprintDAO.store(fillable);
		// updating activitytypes here to make listing work correctly after storing
		// - turkka
//		sprints = sprintDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String delete(){
		sprint = sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		sprintDAO.remove(sprint);
		return Action.SUCCESS;
	}
	
	protected void fillObject(Iteration fillable){
		fillable.setDeliverable(this.deliverable);
		fillable.setName(this.sprint.getName());
		fillable.setDescription(this.sprint.getDescription());
	}

	public int getSprintId() {
		return sprintId;
	}

	public void setSprintId(int sprintId) {
		this.sprintId = sprintId;
	}

	public Iteration getSprint() {
		return sprint;
	}
	
	public void setSprint(Iteration sprint){
		this.sprint = sprint;
	}

	public Collection<Iteration> getSprints() {
		return sprints;
	}

	public void setSprintDAO(SprintDAO sprintDAO) {
		this.sprintDAO = sprintDAO;
	}

	public DeliverableDAO getDeliverableDAO() {
	    return deliverableDAO;
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