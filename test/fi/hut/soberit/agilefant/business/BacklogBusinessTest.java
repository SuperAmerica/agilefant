package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.BacklogBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;


/**
 * A spring test case for testing the Backlog business layer.
 * 
 * @author hhaataja, rstrom
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class BacklogBusinessTest extends MockedTestCase {
    
    @TestedBean
    private BacklogBusinessImpl backlogBusiness;
    @Mock
    private BacklogDAO backlogDAO;
    @Mock
    private ProductDAO productDAO;
    @Mock
    private StoryDAO storyDAO;
    
    @Test
    @DirtiesContext
    public void testGetNumberOfChildren() {
        Backlog backlog = new Product();
        backlog.setId(5);
        
        expect(backlogDAO.getNumberOfChildren(backlog)).andReturn(2);
        replay(backlogDAO);
        
        assertEquals(2, backlogBusiness.getNumberOfChildren(backlog));
        
        verify(backlogDAO);
    }

    @Test
    @DirtiesContext
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
    @DirtiesContext
    public void testGetChildBacklogs_allProducts() {
        expect(productDAO.getAll()).andReturn(Arrays.asList(new Product()));
        replay(backlogDAO, productDAO);
        backlogBusiness.getChildBacklogs(null);
        verify(backlogDAO, productDAO);
    }
    
    @Test
    @DirtiesContext
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
    @DirtiesContext
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
    @DirtiesContext
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
    
    @Test
    @DirtiesContext
    public void testGetStoryPointSumByBacklog() {
        Backlog backlog = new Iteration();
        backlog.setId(4);
        expect(storyDAO.getStoryPointSumByBacklog(backlog.getId()))
            .andReturn(6);
        replayAll();
        
        assertEquals(6, backlogBusiness.getStoryPointSumByBacklog(backlog));
        
        verifyAll();
    }
}
