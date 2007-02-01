package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Product;

public class ProductAction extends ActionSupport implements CRUDAction {

	private static final long serialVersionUID = 1834399750050895118L;
	private ProductDAO productDAO;
	private int productId;
	private Product product;
	private Collection<Product> products = new ArrayList<Product>();

	public String create() {
		productId = 0;
		product = new Product();
		return Action.SUCCESS;
	}

	public String delete() {
		productDAO.remove(productId);
		return Action.SUCCESS;
	}

	public String edit() {
		product = productDAO.get(productId);
		if (product == null){
			super.addActionError(super.getText("product.notFound"));
			return Action.ERROR;
		}
		return Action.SUCCESS;
	}

	public String store() {
		Product storable = new Product();
		if (productId > 0){
			storable = productDAO.get(productId);
			if (storable == null){
				super.addActionError(super.getText("product.notFound"));
				return Action.ERROR;
			}
		}
		this.fillStorable(storable);
		if (super.hasActionErrors()){
			return Action.ERROR;
		}
		productDAO.store(storable);
		return Action.SUCCESS;
	}
	
	protected void fillStorable(Product storable){
		storable.setName(this.product.getName());
		storable.setDescription(this.product.getDescription());		
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public void setProductDAO(ProductDAO productDAO) {
		this.productDAO = productDAO;
	}
	
	protected ProductDAO getProductDAO() {
		return this.productDAO;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public Collection<Product> getProducts() {
		return products;
	}

	public void setProducts(Collection<Product> products) {
		this.products = products;
	}
}