package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.MenuBusiness;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.MenuData;
import fi.hut.soberit.agilefant.web.page.PageItem;

/**
 * The implementation class for calculating data to the lefthand
 * menu.
 * @author rjokelai
 */
@Service("menuBusiness")
public class MenuBusinessImpl implements MenuBusiness {

    private ProductDAO productDAO;
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public MenuData getSubMenuData(PageItem pageitem) {
        MenuData data = new MenuData();
        data.setMenuItems(new ArrayList<PageItem>());
        
        // If the requested pageitem is null, return all product backlogs
        if (pageitem == null) {
            data.getMenuItems().addAll(productDAO.getAllOrderByName());
        }
        else {
            if (pageitem.getChildren() != null) {
                data.getMenuItems().addAll(pageitem.getChildren());
            }
        }
        
        // Update the hasChildren and objectType properties
        for (PageItem item : data.getMenuItems()) {
            String type = "";
            // Check the type
            if (item instanceof Product) {
                type = "product";
            }
            else if (item instanceof Project) {
                type = "project";
            }
            else if (item instanceof Iteration) {
                type = "iteration";
            }
            
            data.getHasChildren().put(item, item.hasChildren());
            data.getObjectTypes().put(item, type);
        }
        
        return data;
    }

    
    
    public ProductDAO getProductDAO() {
        return productDAO;
    }

    @Autowired
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }
    
}
