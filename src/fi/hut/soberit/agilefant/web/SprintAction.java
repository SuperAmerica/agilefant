package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Sprint;
import fi.hut.soberit.agilefant.model.WorkType;

public class SprintAction extends ActionSupport {
	
	private int sprintId;
	private Sprint sprint;
	private SprintDAO sprintDAO;
	private Collection<Sprint> sprints;
	private DeliverableDAO deliverableDAO;
	private Deliverable deliverable;
	private int deliverableId;
	
	public String getAll(){
	    	sprints = new ArrayList<Sprint>();
		deliverable = deliverableDAO.get(deliverableId);
		if (deliverable == null){
			sprints = sprintDAO.getAll();
		} else {
			sprints = deliverable.getSprints();
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
		sprint = new Sprint();
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
		Sprint fillable = new Sprint();
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
	
	protected void fillObject(Sprint fillable){
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

	public Sprint getSprint() {
		return sprint;
	}
	
	public void setSprint(Sprint sprint){
		this.sprint = sprint;
	}

	public Collection<Sprint> getSprints() {
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