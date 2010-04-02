package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.transfer.ProjectTO;

@Component("productAction")
@Scope("prototype")
public class ProductAction implements CRUDAction, Prefetching {

    private static final long serialVersionUID = 1834399750050895118L;

    @Autowired
    private ProductBusiness productBusiness;

    @PrefetchId
    private int productId;

    private Product product = new Product();

    private Collection<Product> products = new ArrayList<Product>();
    
    private List<ProjectTO> childBacklogs = new ArrayList<ProjectTO>();
    
    private List<Story> stories = new ArrayList<Story>();

    public String create() {
        productId = 0;
        product = new Product();
        return Action.SUCCESS;
    }

    public String delete() {
        productBusiness.delete(productId);
        return Action.SUCCESS;
    }

    public String retrieve() {
        product = productBusiness.retrieve(productId);
        return Action.SUCCESS;
    }

    public String store() {
        this.product = this.productBusiness.store(productId, product);
        this.productId = this.product.getId();
        return Action.SUCCESS;
    }

    public String retrieveAll() {
        products = productBusiness.retrieveAll();
        return Action.SUCCESS;
    }
    
    public String retrieveProjects() {
        this.product = this.productBusiness.retrieve(productId);
        this.childBacklogs = this.productBusiness.retrieveProjects(product);
        return Action.SUCCESS;
    }
    public void initializePrefetchedData(int objectId) {
       product = productBusiness.retrieve(objectId);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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

    public void setProductBusiness(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

    public List<Story> getStories() {
        return stories;
    }

    public List<ProjectTO> getChildBacklogs() {
        return childBacklogs;
    }

}