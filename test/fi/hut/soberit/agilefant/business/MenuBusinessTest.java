package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.MenuBusinessImpl;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.transfer.MenuDataNode;

public class MenuBusinessTest {

    MenuBusinessImpl menuBusiness;
    
    ProductBusiness productBusiness;
    
    @Before
    public void setUp_dependencies() {
        menuBusiness = new MenuBusinessImpl();
        
        productBusiness = createStrictMock(ProductBusiness.class);
        menuBusiness.setProductBusiness(productBusiness);
    }

    private void replayAll() {
        replay(productBusiness);
    }

    private void verifyAll() {
        verify(productBusiness);
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
        
        expect(productBusiness.retrieveAll()).andReturn(
                Arrays.asList(firstProduct, secondProduct));
        
        replayAll();
        List<MenuDataNode> actual = menuBusiness.constructBacklogMenuData();
        verifyAll();
        
        assertEquals(2, actual.size());
        checkReturnedData(actual);
    }
    
    private void checkReturnedData(List<MenuDataNode> nodes) {
        checkNodeTitle(nodes, 123, "Foo product");
        checkNodeTitle(nodes, 444, "Bar product");
        
        checkNodeTitle(getNodeById(nodes, 123).getChildren(), 23, "Project");
        checkNodeTitle(getNodeById(nodes, 123).getChildren(), 666, "Iteration");
    }
    
    private void checkNodeTitle(List<MenuDataNode> nodes, int nodeId, String name) {
        MenuDataNode actual = getNodeById(nodes, nodeId);
        assertEquals(name, actual.getTitle());
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
