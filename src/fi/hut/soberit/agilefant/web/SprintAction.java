package fi.hut.soberit.agilefant.web;

import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.SprintDAO;
import fi.hut.soberit.agilefant.model.Sprint;

public class SprintAction extends ActionSupport {
	
	private int sprintId;
	private Sprint sprint;
	private SprintDAO sprintDAO;
	private Collection<Sprint> sprints;
	
	public String getAll(){
		sprints = sprintDAO.getAll();
		return Action.SUCCESS;
	}
	
	public String create(){
		sprintId = 0;
		sprint = new Sprint();
		return Action.SUCCESS;		
	}
	
	public String edit(){
		sprint = sprintDAO.get(sprintId);
		if (sprint == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}
	
	public String store(){
		if (sprint == null){
			super.addActionError(super.getText("activityType.missingForm"));
		}
		Sprint fillable = new Sprint();
		if (sprintId > 0){
			fillable = sprintDAO.get(sprintId);
			if (fillable == null){
				super.addActionError(super.getText("activityType.notFound"));
				return Action.ERROR;
			}
		}
		this.fillObject(fillable);
		sprintDAO.store(fillable);
		// updating activitytypes here to make listing work correctly after storing
		// - turkka
		sprints = sprintDAO.getAll();
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
		fillable.setName(sprint.getName());
		fillable.setDescription(sprint.getDescription());
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
}