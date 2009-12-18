package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.MenuBusiness;
import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.transfer.MenuDataNode;

/**
 * The implementation class for calculating data to the lefthand menu.
 * 
 * @author rjokelai
 */
@Service("menuBusiness")
@Transactional
public class MenuBusinessImpl implements MenuBusiness {

    @Autowired
    private ProductBusiness productBusiness;

    @Autowired
    private TransferObjectBusiness transferObjectBusiness;

    @SuppressWarnings("unchecked")
    public List<MenuDataNode> constructBacklogMenuData() {
        List<MenuDataNode> nodes = new ArrayList<MenuDataNode>();
        List<Product> products = new ArrayList<Product>(productBusiness
                .retrieveAllOrderByName());
        Collections.sort(products, new PropertyComparator("name", true, true));
        for (Product prod : products) {
            nodes.add(constructMenuDataNode(prod));
        }
        return nodes;
    }

    @SuppressWarnings("unchecked")
    private MenuDataNode constructMenuDataNode(Backlog backlog) {
        MenuDataNode mdn = new MenuDataNode();
        mdn.setTitle(backlog.getName());
        mdn.setId(backlog.getId());
        mdn.setScheduleStatus(transferObjectBusiness
                .getBacklogScheduleStatus(backlog));
        List<Backlog> children = Collections.EMPTY_LIST;
        if (!(backlog instanceof Iteration)) { // optimization
            children = new ArrayList<Backlog>();
            children.addAll(backlog.getChildren());
            Collections.sort(children, new PropertyComparator("startDate",
                    true, true));
        }
        for (Backlog child : children) {
            mdn.getChildren().add(constructMenuDataNode(child));
        }

        return mdn;
    }

    public void setProductBusiness(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

}
