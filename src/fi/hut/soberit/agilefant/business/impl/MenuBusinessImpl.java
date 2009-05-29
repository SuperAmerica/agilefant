package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.MenuBusiness;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.util.MenuData;


/**
 * The implementation class for calculating data to the lefthand
 * menu.
 * @author rjokelai
 */
@Service("menuBusiness")
public class MenuBusinessImpl implements MenuBusiness {

    private ProductDAO productDAO;
    private BacklogBusiness backlogBusiness;
    
    /** {@inheritDoc} */
    @Transactional(readOnly = true)
    public MenuData getSubMenuData(Backlog backlog) {
        MenuData data = new MenuData();
        data.setMenuItems(new ArrayList<Backlog>());
        
        // If the requested pageitem is null, return all product backlogs
        if (backlog == null) {
            data.getMenuItems().addAll(productDAO.getAllOrderByName());
        }
        else {
            if (backlog.getChildren() != null) {
                data.getMenuItems().addAll(backlog.getChildren());
            }
        }
        
        // Update the hasChildren and objectType properties
        for (Backlog item : data.getMenuItems()) {
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
            
            data.getHasChildren().put(item,
                    (backlogBusiness.getNumberOfChildren(backlog) > 0));
            data.getObjectTypes().put(item, type);
        }
        
        return data;
    }

    @Autowired
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @Autowired
    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }
    
}
