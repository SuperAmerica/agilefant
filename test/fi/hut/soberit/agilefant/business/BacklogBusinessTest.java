package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.BacklogBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;


/**
 * A spring test case for testing the Backlog business layer.
 * 
 * @author hhaataja, rstrom
 * 
 */

public class BacklogBusinessTest {

    private BacklogBusinessImpl backlogBusiness = new BacklogBusinessImpl();
    private BacklogDAO backlogDAO;
    private ProductDAO productDAO;
    
    @Before
    public void setUp() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogBusiness.setBacklogDAO(backlogDAO);
        
        productDAO = createMock(ProductDAO.class);
        backlogBusiness.setProductDAO(productDAO);
    }
    
    @Test
    public void testGetNumberOfChildren() {
        Backlog backlog = new Product();
        backlog.setId(5);
        
        expect(backlogDAO.getNumberOfChildren(backlog)).andReturn(2);
        replay(backlogDAO);
        
        assertEquals(2, backlogBusiness.getNumberOfChildren(backlog));
        
        verify(backlogDAO);
    }

    @Test
    public void testRetrieveMultipleBacklogs() {
        Collection<Integer> idList = Arrays.asList(1,2);
        Collection<Backlog> retrievedBacklogs = new ArrayList<Backlog>();
        Product prod1 = new Product();
        Product prod2 = new Product();
        retrievedBacklogs.add(prod1);
        retrievedBacklogs.add(prod2);
        
        expect(backlogDAO.retrieveMultiple(idList)).andReturn(retrievedBacklogs);
        replay(backlogDAO, productDAO);
        
        backlogBusiness.retrieveMultiple(idList);
        
        verify(backlogDAO, productDAO);
    }
    
    @Test
    public void testGetChildBacklogs_allProducts() {
        expect(productDAO.getAll()).andReturn(Arrays.asList(new Product()));
        replay(backlogDAO, productDAO);
        backlogBusiness.getChildBacklogs(null);
        verify(backlogDAO, productDAO);
    }
    
    @Test
    public void testGetChildBacklogs_forProduct() {
        Backlog product = new Product();
        Project project = new Project();
        product.getChildren().add(project);
        replay(backlogDAO, productDAO);
        
        Collection<Backlog> actualChildren = backlogBusiness.getChildBacklogs(product);
        
        assertTrue(actualChildren.contains(project));
        assertEquals(1, actualChildren.size());
        
        verify(backlogDAO, productDAO);
    }
    
    @Test
    public void testGetChildBacklogs_forProject() {
        Backlog project = new Project();
        Iteration iteration = new Iteration();
        project.getChildren().add(iteration);
        replay(backlogDAO, productDAO);
        
        Collection<Backlog> actualChildren = backlogBusiness.getChildBacklogs(project);
        assertTrue(actualChildren.contains(iteration));
        assertEquals(1, actualChildren.size());
        
        verify(backlogDAO, productDAO);
    }
    
    @Test
    public void testGetParentProduct() {
        Product product = new Product();
        Iteration iterationUnderProject = new Iteration();
        Iteration iterationUnderProduct = new Iteration();
        Project project = new Project();
        
        iterationUnderProduct.setParent(product);
        iterationUnderProject.setParent(project);
        project.setParent(product);
        
        assertSame(product, backlogBusiness.getParentProduct(product));
        assertSame(product, backlogBusiness.getParentProduct(project));
        assertSame(product, backlogBusiness.getParentProduct(iterationUnderProduct));
        assertSame(product, backlogBusiness.getParentProduct(iterationUnderProject));
    }
}
