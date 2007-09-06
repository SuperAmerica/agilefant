package fi.hut.soberit.agilefant.web;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TaskEventDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Product;

import fi.hut.soberit.agilefant.util.BacklogValueInjector;

public class DeliverableAction extends ActionSupport implements CRUDAction {

	private static final long serialVersionUID = -4636900464606739866L;
	private int deliverableId;
	private int productId;
	private int activityTypeId;
	private Deliverable deliverable;
	private DeliverableDAO deliverableDAO;
	private ActivityTypeDAO activityTypeDAO;
	private ProductDAO productDAO;
	private Collection<ActivityType> activityTypes;
	private Backlog backlog;
	private TaskEventDAO taskEventDAO;
	private BacklogItemDAO backlogItemDAO;
	private String startDate;
	private String endDate;
	private String dateFormat;
	
	/**
	 * @return the dateFormat
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat the dateFormat to set
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String create(){
		this.prepareActivityTypes();
		//TODO: fiksumpi virheenkäsittely
//		if (this.activityTypes.isEmpty()){
//			super.addActionError("deliverable.activityTypesNotFound");
//			return Action.ERROR;
//		}
		deliverableId = 0;
		deliverable = new Deliverable();
		backlog = deliverable;
		return Action.SUCCESS;
	}
	
	public String edit(){
		Date startDate;
		this.prepareActivityTypes();
		if (this.activityTypes.isEmpty()){
			super.addActionError("deliverable.activityTypesNotFound");
			return Action.ERROR;
		}
		deliverable = deliverableDAO.get(deliverableId);
		startDate = deliverable.getStartDate();
		
		if (deliverable == null){
//			super.addActionError(super.getText("deliverable.notFound"));
//			return Action.ERROR;
			return Action.SUCCESS;
		}
		if (startDate == null) {
			startDate = new Date(0);
		}
		
		productId = deliverable.getProduct().getId();
		backlog = deliverable;

		BacklogValueInjector.injectMetrics(backlog, startDate, 
				taskEventDAO, backlogItemDAO);
		
		return Action.SUCCESS;
	}
	
	public String store(){
		Deliverable storable = new Deliverable();
		if (deliverableId > 0){
			storable = deliverableDAO.get(deliverableId);
			if (storable == null){
				super.addActionError(super.getText("deliverable.notFound"));
				return Action.ERROR;
			}
		}
		
		try {
			this.fillStorable(storable);
		} catch (ParseException e) {
			super.addActionError(e.toString());
			return Action.ERROR;
		}
		
		if (super.hasActionErrors()){
			return Action.ERROR;
		}
		
		if(deliverableId == 0)
			deliverableId = (Integer) deliverableDAO.create(storable);
		else
			deliverableDAO.store(storable);
		
		return Action.SUCCESS;
	}
	
	public String delete(){
		deliverable = deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("deliverable.notFound"));
			return Action.ERROR;
		}
		if(deliverable.getBacklogItems().size() > 0 || deliverable.getIterations().size() > 0) {
			super.addActionError(super.getText("deliverable.notEmptyWhenDeleting"));
			return Action.ERROR;
		}
		Product product = deliverable.getProduct();
		productId = product.getId();
		product.getDeliverables().remove(deliverable);
		deliverable.setProduct(null);
		deliverableDAO.remove(deliverable);
		return Action.SUCCESS;
	}
	
	protected void fillStorable(Deliverable storable) throws ParseException {
		Date current = Calendar.getInstance().getTime();
		if(this.deliverable.getName().equals("")) {
			super.addActionError(super.getText("project.missingName"));
			return;
		}
		
		deliverable.setStartDate(startDate, dateFormat);
		if (deliverable.getStartDate() == null){
			super.addActionError(super.getText("deliverable.missingStartDate"));
			return;
		}
		
		deliverable.setEndDate(endDate, dateFormat);
		if (deliverable.getEndDate() == null){
			super.addActionError(super.getText("deliverable.missingEndDate"));
			return;
		}
		if (deliverable.getStartDate().after(deliverable.getEndDate())){
			super.addActionError(super.getText("backlog.startDateAfterEndDate"));
			return;
		}

		Product product = productDAO.get(productId);
		if (product == null){
			super.addActionError(super.getText("product.notFound"));
			return;
		} else if(storable.getProduct() != product){
			/* Setting the relation in one end of the relation is enought to
			 * change the relation in both ends! Hibernate takes care of 
			 * both ends. */
			storable.setProduct(product);
			// product.getDeliverables().add(storable);
		}
		
		if (storable.getActivityType() == null ||
			storable.getActivityType().getId() != activityTypeId){
			ActivityType activityType = null;
			if (activityTypeId > 0){
				activityType = activityTypeDAO.get(activityTypeId);
			}
			if (activityType != null){
				storable.setActivityType(activityType);
			} else {
				super.addActionError(super.getText("deliverable.missingActivityType"));
				return;
			}
		}
		storable.setEndDate(endDate, dateFormat);
		storable.setStartDate(startDate, dateFormat);
		storable.setName(deliverable.getName());
		storable.setDescription(deliverable.getDescription());
	}

	public int getDeliverableId() {
		return deliverableId;
	}

	public void setDeliverableId(int deliverableId) {
		this.deliverableId = deliverableId;
	}
	
	public Collection<Deliverable> getAllDeliverables() {
		return this.deliverableDAO.getAll();
	}

	public Deliverable getDeliverable() {
		return deliverable;
	}
	
	public void setDeliverable(Deliverable deliverable){
		this.deliverable = deliverable;
		this.backlog = deliverable;
	}

	public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
		this.deliverableDAO = deliverableDAO;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}

	public int getActivityTypeId() {
		return activityTypeId;
	}

	public void setActivityTypeId(int activityTypeId) {
		this.activityTypeId = activityTypeId;
	}

	public void setActivityTypeDAO(ActivityTypeDAO activityTypeDAO) {
		this.activityTypeDAO = activityTypeDAO;
	}
	
	private void prepareActivityTypes(){
		this.activityTypes = activityTypeDAO.getAll();
	}

	public Collection<ActivityType> getActivityTypes() {
		return this.activityTypes;
	}

	public void setActivityTypes(Collection<ActivityType> activityTypes) {
		this.activityTypes = activityTypes;
	}

	public Backlog getBacklog() {
		return this.backlog;
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

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
}