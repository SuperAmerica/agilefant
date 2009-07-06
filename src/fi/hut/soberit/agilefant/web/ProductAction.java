package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.model.Product;
import flexjson.JSONSerializer;

@Component("productAction")
@Scope("prototype")
public class ProductAction extends BacklogContentsAction implements CRUDAction {

    private static final long serialVersionUID = 1834399750050895118L;

    @Autowired
    private ProductBusiness productBusiness;
    
    private int productId;

    private Product product;

    private Collection<Product> products = new ArrayList<Product>();
     
    private String jsonData = "";

    public String create() {
        productId = 0;
        product = new Product();
        backlog = product;
        return Action.SUCCESS;
    }

    public String delete() {
        Product p = productBusiness.retrieve(productId);
        if (p == null) {
            super.addActionError(super.getText("product.notFound"));
            return Action.ERROR;
        }
//        if (p.getBacklogItems().size() > 0 || p.getProjects().size() > 0) {
//            super.addActionError(super.getText("product.notEmptyWhenDeleting"));
//            return Action.ERROR;
//        }
//        if (p.getBusinessThemes().size() > 0) {
//            super.addActionError(super.getText("product.notEmptyOfThemesWhenDeleting"));
//            return Action.ERROR;
//        }
        productBusiness.delete(productId);
        return Action.SUCCESS;
    }

    public String retrieve() {
        // Date startDate = new Date(0);
        product = productBusiness.retrieve(productId);

        backlog = product;

        super.initializeContents();

        return Action.SUCCESS;
    }

    public String store() {
        Product storable = new Product();

        if (productId > 0) {
            storable = productBusiness.retrieveIfExists(productId);
            if (storable == null) {
                super.addActionError(super.getText("product.notFound"));
                return Action.ERROR;
            }
        }
        this.fillStorable(storable);
        if (super.hasActionErrors()) {
            return Action.ERROR;
        }

        if (productId == 0) {
            productId = (Integer) backlogBusiness.create(storable);
        } else {
            backlogBusiness.store(storable);
        }
        return Action.SUCCESS;
    }

    protected void fillStorable(Product storable) {
        if (this.product == null) {
            super.addActionError(super.getText("product.internalError"));
            return;
        }
        if (this.product.getName() == null || 
                this.product.getName().trim().equals("")) {
            super.addActionError(super.getText("product.missingName"));
            return;
        }
        storable.setName(this.product.getName());
        storable.setDescription(this.product.getDescription());
    }
    
    public String retrieveAll() {
        products = productBusiness.retrieveAll();
        return Action.SUCCESS;
    }
    
//    /**
//     * @return a string of JSON serialized products 
//     */
//    public String getAllProductsAsJSON() {
//        return new JSONSerializer().serialize(productBusiness.retrieveAll());
//    }
//    
//    public String getProductAsJSON() {
//        return new JSONSerializer().serialize(productBusiness.retrieve(productId));
//    }
//    
//    /**
//     * Get product data as JSON.
//     * <p>
//     * If given product id is greater than 0, return the product's data.
//     * Otherwise, return all products' data.
//     * @return product data as JSON string  
//     */
//    public String getProductJSON() {
//        if (productId > 0) {
//            jsonData = this.getProductAsJSON();
//        }
//        else {
//            jsonData = this.getAllProductsAsJSON();
//        }
//        return Action.SUCCESS;
//    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.backlog = product;
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

    
//    public Map<Project, EffortSumData> getEffLeftSums() {
//        return effLeftSums;
//    }
//
//    public Map<Project, EffortSumData> getOrigEstSums() {
//        return origEstSums;
//    }

//
//    public void setEffLeftSums(Map<Project, EffortSumData> effLeftSums) {
//        this.effLeftSums = effLeftSums;
//    }
//
//    public void setOrigEstSums(Map<Project, EffortSumData> origEstSums) {
//        this.origEstSums = origEstSums;
//    }
//    
//    public Collection<BusinessTheme> getActiveBusinessThemes() {
//        return businessThemeBusiness.getActiveBusinessThemes(productId);
//    }
//
//    public Collection<BusinessTheme> getNonActiveBusinessThemes() {
//        return businessThemeBusiness.getNonActiveBusinessThemes(productId);
//    }
//
//    public Map<BusinessTheme, BusinessThemeMetrics> getBusinessThemeMetrics() {
//        return themeMetrics;
//    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public void setProductBusiness(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

}