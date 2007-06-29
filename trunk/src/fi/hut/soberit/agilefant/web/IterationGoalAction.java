package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;

public class IterationGoalAction extends ActionSupport implements CRUDAction {
	
	private static final long serialVersionUID = -8772191083987255387L;
	private IterationDAO iterationDAO;
	private IterationGoalDAO iterationGoalDAO;
	private int iterationId;
	private int iterationGoalId;
	private IterationGoal iterationGoal;
	private Iteration iteration;
	private Collection<IterationGoal> iterationGoals = new ArrayList<IterationGoal>();

	public String create() {
		iterationGoalId = 0;
		iterationGoal = new IterationGoal();
		iteration = iterationDAO.get(iterationId);
		iterationId = iteration.getId();
		return Action.SUCCESS;
	}

	public String delete() {
		iterationGoal = iterationGoalDAO.get(iterationGoalId);
		if (iterationGoal == null){
			super.addActionError(super.getText("iterationgGoal.notFound"));
			return Action.ERROR;
		}
		for(BacklogItem bi : iterationGoal.getBacklogItems()) {
			bi.setIterationGoal(null);
		}
//		iterationId = iteration.getId();
		iterationGoalDAO.remove(iterationGoalId);
		return Action.SUCCESS;
	}

	public String edit() {
		iterationGoal = iterationGoalDAO.get(iterationGoalId);
		if (iterationGoal == null){
			super.addActionError(super.getText("iterationgGoal.notFound"));
			return Action.ERROR;
		}
		iteration = iterationGoal.getIteration();
		iterationId = iteration.getId();
		return Action.SUCCESS;
	}

	public String store() {
		IterationGoal storable = new IterationGoal();
		if (iterationGoalId > 0){
			storable = iterationGoalDAO.get(iterationGoalId);
			if (storable == null){
				super.addActionError(super.getText("iterationgGoal.notFound"));
				return Action.ERROR;
			}
		}
		this.fillStorable(storable);
		if (super.hasActionErrors()){
			return Action.ERROR;
		}
		iterationGoalDAO.store(storable);
		return Action.SUCCESS;
	}
	
	protected void fillStorable(IterationGoal storable){
		if (iterationId > 0 && storable.getId() == 0){
			iteration = iterationDAO.get(iterationId);
			if (iteration == null){
				super.addActionError(super.getText("iterationg.notFound"));
			}
		}
		storable.setName(this.iterationGoal.getName());
		storable.setDescription(this.iterationGoal.getDescription());
		storable.setPriority(this.iterationGoal.getPriority());
		if (storable.getId() == 0){
			storable.setIteration(iteration);
		}		
	}

	public Iteration getIteration() {
		return iteration;
	}

	public void setIteration(Iteration iteration) {
		this.iteration = iteration;
	}

	public int getIterationId() {
		return iterationId;
	}

	public void setIterationId(int iterationId) {
		this.iterationId = iterationId;
	}

	public IterationGoal getIterationGoal() {
		return iterationGoal;
	}

	public void setIterationGoal(IterationGoal iterationGoal) {
		this.iterationGoal = iterationGoal;
	}

	public int getIterationGoalId() {
		return iterationGoalId;
	}

	public void setIterationGoalId(int iterationGoalId) {
		this.iterationGoalId = iterationGoalId;
	}

	public void setIterationDAO(IterationDAO iterationDAO) {
		this.iterationDAO = iterationDAO;
	}

	public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
		this.iterationGoalDAO = iterationGoalDAO;
	}
	public Collection<IterationGoal> getIterationGoals() {
		return iterationGoals;
	}

	public void setIterationGoals(Collection<IterationGoal> iterationGoals) {
		this.iterationGoals = iterationGoals;
	}

}
