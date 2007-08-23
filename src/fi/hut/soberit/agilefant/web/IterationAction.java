package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;

import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.util.BacklogValueInjector;

public class IterationAction extends ActionSupport implements CRUDAction {

	private static final long serialVersionUID = -448825368336871703L;
	private int iterationId;
	private Iteration iteration;
	private Backlog backlog;
	private IterationDAO iterationDAO;
	private DeliverableDAO deliverableDAO;
	private TaskEventDAO taskEventDAO;
	private BacklogItemDAO backlogItemDAO;
	private BacklogDAO backlogDAO;
	private Deliverable deliverable;
	private int deliverableId;
	private IterationGoalDAO iterationGoalDAO;
	private int iterationGoalId;
	private String startDate;
	private String endDate;

	public String create(){
		iterationId = 0;
		iteration = new Iteration();
		backlog = iteration;
		return Action.SUCCESS;		
	}
	
	public String edit(){
		iteration = iterationDAO.get(iterationId);
		Date startDate = iteration.getStartDate();
		
		if (iteration == null){
//			super.addActionError(super.getText("iteration.notFound"));
//			return Action.INPUT;
			return Action.SUCCESS;
		}
		if (startDate == null) {
			startDate = new Date(0);
		}
		
		deliverable = iteration.getDeliverable();
		
		/* We need Backlog-class object to generate backlog list in 
		 * _backlogList.jsp */
		backlog = iteration;
		BacklogValueInjector.injectMetrics(backlog,	startDate, 
				taskEventDAO, backlogItemDAO);
		
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
		
		try {
			this.fillObject(fillable);
		} catch (ParseException e) {
			super.addActionError(e.toString());
			return Action.ERROR;
		}
		
		if (super.hasActionErrors()){
			return Action.ERROR;
		}
		
		if(iterationId == 0)
			iterationId = (Integer) iterationDAO.create(fillable);
		else
			iterationDAO.store(fillable);
		return Action.SUCCESS;
	}
	
	public String delete(){
		iteration = iterationDAO.get(iterationId);
		if (iteration == null){
			super.addActionError(super.getText("activityType.notFound"));
			return Action.ERROR;
		}
		if(iteration.getBacklogItems().size() > 0 || iteration.getIterationGoals().size() > 0) {
			super.addActionError(super.getText("iteration.notEmptyWhenDeleting"));
			return Action.ERROR;			
		}
		iterationDAO.remove(iteration);
		return Action.SUCCESS;
	}
	
	protected void fillObject(Iteration fillable) throws ParseException {
		fillable.setEndDate(endDate);
		fillable.setStartDate(startDate);
		if(this.iteration.getName().equals("")) {
			super.addActionError(super.getText("iteration.missingName"));
			return;
		}
		if (fillable.getStartDate().after(fillable.getEndDate())){
			super.addActionError(super.getText("backlog.startDateAfterEndDate"));
			return;
		}
		fillable.setDeliverable(this.deliverable);
		fillable.setName(this.iteration.getName());
		fillable.setDescription(this.iteration.getDescription());
		if (fillable.getStartDate().after(fillable.getEndDate())){
			super.addActionError(
					super.getText("backlog.startDateAfterEndDate"));
			return;
		}
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
		this.backlog = iteration;
	}

	public Backlog getBacklog() {
		return backlog;
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

	/**
	 * @return the taskEventDAO
	 */
	public TaskEventDAO getTaskEventDAO() {
		return taskEventDAO;
	}

	/**
	 * @param taskEventDAO the taskEventDAO to set
	 */
	public void setTaskEventDAO(TaskEventDAO taskEventDAO) {
		this.taskEventDAO = taskEventDAO;
	}

	/**
	 * @return the backlogItemDAO
	 */
	public BacklogItemDAO getBacklogItemDAO() {
		return backlogItemDAO;
	}

	/**
	 * @param backlogItemDAO the backlogItemDAO to set
	 */
	public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
		this.backlogItemDAO = backlogItemDAO;
	}

	/**
	 * @return the backlogDAO
	 */
	public BacklogDAO getBacklogDAO() {
		return backlogDAO;
	}

	/**
	 * @param backlogDAO the backlogDAO to set
	 */
	public void setBacklogDAO(BacklogDAO backlogDAO) {
		this.backlogDAO = backlogDAO;
	}

	/**
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	/**
	 * @return the startDate
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
}