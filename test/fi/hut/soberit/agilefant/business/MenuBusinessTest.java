package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
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
    
    Set<Product> products;
    
    @Before
    public void setUp_dependencies() {
        menuBusiness = new MenuBusinessImpl();
        
        productBusiness = createStrictMock(ProductBusiness.class);
        menuBusiness.setProductBusiness(productBusiness);
        
        transferObjectBusiness = createStrictMock(TransferObjectBusiness.class);
        menuBusiness.setTransferObjectBusiness(transferObjectBusiness);
    }

    @Before
    public void setUp_dataset() {
        products = new HashSet<Product>();
        Product prod1 = new Product();
        prod1.setName("zzz");
        prod1.setId(2);
        products.add(prod1);
        Product prod2 = new Product();
        prod2.setName("aaa");
        products.add(prod2);
        prod2.setId(1);
        
        Project proj1 = new Project();
        proj1.setStartDate(new DateTime(2009,1,1,0,0,0,0));
        proj1.setId(3);
        Project proj2 = new Project();
        proj2.setStartDate(new DateTime(2009,10,1,0,0,0,0));
        proj2.setId(5);
        Project proj3 = new Project();
        proj3.setStartDate(new DateTime(2009,6,1,0,0,0,0));
        proj3.setId(4);
        
        prod1.getChildren().add(proj1);
        prod1.getChildren().add(proj2);
        prod1.getChildren().add(proj3);
        
        Iteration iter1 = new Iteration();
        iter1.setStartDate(new DateTime(2009,12,1,0,0,0,0));
        iter1.setId(8);
        Iteration iter2 = new Iteration();
        iter2.setStartDate(new DateTime(2009,2,1,0,0,0,0));
        iter2.setId(6);
        Iteration iter3 = new Iteration();
        iter3.setStartDate(new DateTime(2009,7,1,0,0,0,0));
        iter3.setId(7);
        
        proj1.getChildren().add(iter1);
        proj1.getChildren().add(iter2);
        proj1.getChildren().add(iter3);
    }
    private void replayAll() {
        replay(productBusiness, transferObjectBusiness);
    }

    private void verifyAll() {
        verify(productBusiness, transferObjectBusiness);
    }
    
    @Test
    public void constructBacklogMenuData() {  
        expect(productBusiness.retrieveAllOrderByName()).andReturn(
                products);
        
        expect(transferObjectBusiness.getBacklogScheduleStatus(isA(Backlog.class)))
            .andReturn(ScheduleStatus.FUTURE).times(8);
        replayAll();
        List<MenuDataNode> actual = menuBusiness.constructBacklogMenuData();
        verifyAll();
        
        assertEquals(2, actual.size());
        checkProducts(actual);
    }
    
    private void checkProducts(List<MenuDataNode> nodes) {
        assertEquals(1, nodes.get(0).getId());
        assertEquals(2, nodes.get(1).getId());
        
        checkProjects(nodes.get(1).getChildren());
    }
    
    private void checkProjects(List<MenuDataNode> nodes) {
        assertEquals(3, nodes.get(0).getId());
        assertEquals(4, nodes.get(1).getId());
        assertEquals(5, nodes.get(2).getId());
        checkIterations(nodes.get(0).getChildren());
    }
    
    private void checkIterations(List<MenuDataNode> nodes) {
        assertEquals(6, nodes.get(0).getId());
        assertEquals(7, nodes.get(1).getId());
        assertEquals(8, nodes.get(2).getId());
    }
}
