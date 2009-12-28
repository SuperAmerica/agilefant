package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.support.PropertyComparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.MenuBusiness;
import fi.hut.soberit.agilefant.business.ProductBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.transfer.AssignmentMenuNode;
import fi.hut.soberit.agilefant.transfer.AssignmentMenuNodeType;
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
    private IterationDAO iterationDAO;
    
    @Autowired
    private ProjectDAO projectDAO;
    
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
    
    private AssignmentMenuNode constructAssignmentMenuDataNode(Backlog backlog) {
        AssignmentMenuNode node = new AssignmentMenuNode();
        node.setTitle(backlog.getName());
        node.setId(backlog.getId());
        node.setType(AssignmentMenuNodeType.BACKLOG);
        return node;
    }
    
    @Transactional(readOnly = true)
    public List<AssignmentMenuNode> constructMyAssignmentsData(User user) {
        List<AssignmentMenuNode> nodes = new ArrayList<AssignmentMenuNode>();
        List<Project> projects = projectDAO.retrieveActiveWithUserAssigned(user.getId());
        List<Iteration> iterations = iterationDAO.retrieveActiveWithUserAssigned(user.getId());
        Set<Integer> projectIds = new HashSet<Integer>();
        for (Project project : projects) {
            AssignmentMenuNode node = constructAssignmentMenuDataNode(project);
            projectIds.add(project.getId());
            nodes.add(node);
        }
        for (Iteration iteration : iterations) {
            AssignmentMenuNode node = constructAssignmentMenuDataNode(iteration);
            AssignmentMenuNode projectNode = null;
            Project project = (Project) iteration.getParent();
            if (projectIds.contains(project.getId())) {
                for (AssignmentMenuNode existingNode : nodes) {
                    if (existingNode.getType().equals(AssignmentMenuNodeType.BACKLOG) &&
                            existingNode.getId() == project.getId()) {
                        projectNode = existingNode;
                    }
                }
            } else {
                projectNode = constructAssignmentMenuDataNode(project);
                projectIds.add(project.getId());
                nodes.add(projectNode);
            }
            projectNode.getChildren().add(node);                
        }
        return nodes;
    }

    public void setProductBusiness(ProductBusiness productBusiness) {
        this.productBusiness = productBusiness;
    }

    public void setTransferObjectBusiness(
            TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }
    
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }
    
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }
    
}
