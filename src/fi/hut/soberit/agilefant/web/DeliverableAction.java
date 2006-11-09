package fi.hut.soberit.agilefant.web;

import java.util.Collection;	

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Deliverable;
import fi.hut.soberit.agilefant.model.Product;

public class DeliverableAction extends ActionSupport implements CRUDAction{
	
	private int deliverableId;
	private int productId;
	private Deliverable deliverable;
	private DeliverableDAO deliverableDAO;
	private ProductDAO productDAO;
		
	public String create(){
		deliverableId = 0;
		deliverable = new Deliverable();
		return Action.SUCCESS;
	}
	
	public String edit(){
		deliverable = deliverableDAO.get(deliverableId);
		if (deliverable == null){
			super.addActionError(super.getText("deliverable.notFound"));
			return Action.ERROR;
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
		if (storable.getProduct() == null){
			Product product = productDAO.get(productId);
			if (product == null){
				super.addActionError(super.getText("product.notFound"));
				return;
			}
			storable.setProduct(product);
			product.getDeliverables().add(storable);
		}
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
}