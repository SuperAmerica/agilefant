package fi.hut.soberit.agilefant.business.impl;

import java.text.SimpleDateFormat;

import fi.hut.soberit.agilefant.business.TimelineBusiness;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.JSONUtils;
import flexjson.JSONSerializer;

public class TimelineBusinessImpl implements TimelineBusiness {
    
    private ProductDAO productDAO;

    /** {@inheritDoc} */
    public String productContentsToJSON(int productId) throws ObjectNotFoundException {
        return productContentsToJSON(productDAO.get(productId));
    }

    /** {@inheritDoc} */
    public String productContentsToJSON(Product product) throws ObjectNotFoundException {
        if (product == null) {
            throw new ObjectNotFoundException();
        }
 
        JSONSerializer ser = new JSONSerializer();
        ser.exclude("*.description");
        ser.exclude("description");
        ser.include("projects");
        ser.include("projects.iterations");
        
        return ser.serialize(product);
    }

    public String getThemeJSON(Product prod) {
        if(prod == null) {
            return "";
        }
        JSONSerializer ser = new JSONSerializer();
        ser.include("backlogBindings.boundEffort");
        ser.include("name");
        ser.include("class");
        ser.include("id");
        ser.include("backlogBindings.backlog.id");
        ser.include("backlogBindings.backlog.startDate");
        ser.include("backlogBindings.backlog.endDate");
        ser.exclude("*");
       
        return ser.serialize(prod.getBusinessThemes());
    }
    public String getThemeJSON(int productId) {
        return getThemeJSON(productDAO.get(productId));
    }
    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
    
    
}
