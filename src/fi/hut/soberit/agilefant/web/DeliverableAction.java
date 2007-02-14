package fi.hut.soberit.agilefant.web;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.ActivityTypeDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.ActivityType;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Product;

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
			
	public String create(){
		this.prepareActivityTypes();
		//TODO: fiksumpi virheenkäsittely
//		if (this.activityTypes.isEmpty()){
//			super.addActionError("deliverable.activityTypesNotFound");
//			return Action.ERROR;
//		}
		deliverableId = 0;
		deliverable = new Deliverable();
		return Action.SUCCESS;
	}
	
	public String edit(){
		this.prepareActivityTypes();
		if (this.activityTypes.isEmpty()){
			super.addActionError("deliverable.activityTypesNotFound");
			return Action.ERROR;
		}
		deliverable = deliverableDAO.get(deliverableId);
		if (deliverable == null){
//			super.addActionError(super.getText("deliverable.notFound"));
//			return Action.ERROR;
			return Action.SUCCESS;
		}
		productId = deliverable.getProduct().getId();
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
		this.fillStorable(storable);
		if (super.hasActionErrors()){
			return Action.ERROR;
		}
		deliverableDAO.store(storable);
		return Action.SUCCESS;
	}
	
	public String delete(){
		deliverable = deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("deliverable.notFound"));
			return Action.ERROR;
		}
		Product product = deliverable.getProduct();
		productId = product.getId();
		product.getDeliverables().remove(deliverable);
		deliverable.setProduct(null);
		deliverableDAO.remove(deliverable);
		return Action.SUCCESS;
	}
	
	protected void fillStorable(Deliverable storable){
		Date current = Calendar.getInstance().getTime();
		if (deliverable.getStartDate() == null){
			super.addActionError(super.getText("deliverable.missingStartDate"));
			return;
		}
		if (deliverable.getEndDate() == null){
			super.addActionError(super.getText("deliverable.missingEndDate"));
			return;
		}
		if (deliverable.getStartDate().after(deliverable.getEndDate())){
			super.addActionError(super.getText("deliverable.startDateAfterEndDate"));
			return;
		}
		if (deliverable.getEndDate().before(current)){
			super.addActionError(super.getText("deliverable.endDateInPast"));
			return;
		}		
		if (storable.getProduct() == null){
			Product product = productDAO.get(productId);
			if (product == null){
				super.addActionError(super.getText("product.notFound"));
				return;
			}
			storable.setProduct(product);
			product.getDeliverables().add(storable);
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
		storable.setEndDate(deliverable.getEndDate());
		storable.setStartDate(deliverable.getStartDate());
		storable.setName(deliverable.getName());
		storable.setDescription(deliverable.getDescription());
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
		return activityTypes;
	}

	public void setActivityTypes(Collection<ActivityType> activityTypes) {
		this.activityTypes = activityTypes;
	}
}