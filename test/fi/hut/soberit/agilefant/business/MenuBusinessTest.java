package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.MenuBusinessImpl;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.transfer.MenuDataNode;
import fi.hut.soberit.agilefant.transfer.ScheduleStatus;

public class MenuBusinessTest {

    MenuBusinessImpl menuBusiness;
    
    ProductBusiness productBusiness;
    
    TransferObjectBusiness transferObjectBusiness;
    
    @Before
    public void setUp_dependencies() {
        menuBusiness = new MenuBusinessImpl();
        
        productBusiness = createStrictMock(ProductBusiness.class);
        menuBusiness.setProductBusiness(productBusiness);
        
        transferObjectBusiness = createStrictMock(TransferObjectBusiness.class);
        menuBusiness.setTransferObjectBusiness(transferObjectBusiness);
    }

    private void replayAll() {
        replay(productBusiness, transferObjectBusiness);
    }

    private void verifyAll() {
        verify(productBusiness, transferObjectBusiness);
    }
    
    @Test
    public void constructBacklogMenuData() {
        Product firstProduct = new Product();
        firstProduct.setId(123);
        firstProduct.setName("Foo product");
        
        Project firstProject = new Project();
        firstProject.setId(23);
        firstProject.setName("Project");
        firstProduct.getChildren().add(firstProject);
        
        Iteration firstIteration = new Iteration();
        firstIteration.setId(666);
        firstIteration.setName("Iteration");
        firstProduct.getChildren().add(firstIteration);
        
        Product secondProduct = new Product();
        secondProduct.setId(444);
        secondProduct.setName("Bar product");
        
        expect(productBusiness.retrieveAllOrderByName()).andReturn(
                Arrays.asList(firstProduct, secondProduct));
        
        expect(transferObjectBusiness.getBacklogScheduleStatus(isA(Backlog.class)))
            .andReturn(ScheduleStatus.FUTURE).times(4);
        replayAll();
        List<MenuDataNode> actual = menuBusiness.constructBacklogMenuData();
        verifyAll();
        
        assertEquals(2, actual.size());
        checkReturnedData(actual);
    }
    
    private void checkReturnedData(List<MenuDataNode> nodes) {
        checkNode(nodes, 123, "Foo product");
        checkNode(nodes, 444, "Bar product");
        
        checkNode(getNodeById(nodes, 123).getChildren(), 23, "Project");
        checkNode(getNodeById(nodes, 123).getChildren(), 666, "Iteration");
    }
    
    private void checkNode(List<MenuDataNode> nodes, int nodeId, String name) {
        MenuDataNode actual = getNodeById(nodes, nodeId);
        assertEquals(name, actual.getTitle());
        assertEquals(ScheduleStatus.FUTURE, actual.getScheduleStatus());
    }
    
    private MenuDataNode getNodeById(List<MenuDataNode> nodes, int id) {
        for (MenuDataNode node : nodes) {
            if (node.getId() == id) {
                return node;
            }
        }
        return null;
    }
}
