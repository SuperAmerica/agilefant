package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.joda.time.DateTime;
import org.joda.time.Days;
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
    
    @Test
    @DirtiesContext
    public void testTimeLeftInSchedulable() {
        Iteration iter = new Iteration();
        DateTime startDate = new DateTime().minusDays(2);
        DateTime endDate = startDate.plusDays(5);
        iter.setStartDate(startDate.toDateMidnight().toDateTime());
        iter.setEndDate(endDate);
        
        Days daysLeft = backlogBusiness.daysLeftInSchedulableBacklog(iter);
        
        assertEquals(3, daysLeft.getDays());
        
    }
    
    @Test
    @DirtiesContext
    public void testTimeLeftInSchedulable_past() {
        Iteration iter = new Iteration();
        DateTime startDate = new DateTime().minusDays(40);
        DateTime endDate = startDate.plusDays(5);
        iter.setStartDate(startDate.toDateMidnight().toDateTime());
        iter.setEndDate(endDate);
        
        Days daysLeft = backlogBusiness.daysLeftInSchedulableBacklog(iter);
        assertEquals(0, daysLeft.getDays());
    }
    
    @Test
    @DirtiesContext
    public void testTimeLeftInSchedulable_future() {
        Iteration iter = new Iteration();
        DateTime startDate = new DateTime().plusDays(2);
        DateTime endDate = startDate.plusDays(50);
        iter.setStartDate(startDate.toDateMidnight().toDateTime());
        iter.setEndDate(endDate);
        
        Days daysLeft = backlogBusiness.daysLeftInSchedulableBacklog(iter);
        assertEquals(50, daysLeft.getDays());
    }
    
    @Test
    @DirtiesContext
    public void testCalculateBacklogTimeframePercentageLeft() {
        Iteration iter = new Iteration();
        DateTime startDate = new DateTime().minusDays(2);
        DateTime endDate = startDate.plusDays(4);
        iter.setStartDate(startDate.toDateMidnight().toDateTime());
        iter.setEndDate(endDate);
        float percentage = backlogBusiness.calculateBacklogTimeframePercentageLeft(iter);
        assertEquals(0.5f,percentage,0);
    }
    
    @Test
    @DirtiesContext
    public void testCalculateBacklogTimeframePercentageLeft_past() {
        Iteration iter = new Iteration();
        DateTime startDate = new DateTime().minusDays(40);
        DateTime endDate = startDate.plusDays(5);
        iter.setStartDate(startDate.toDateMidnight().toDateTime());
        iter.setEndDate(endDate);
        float percentage = backlogBusiness.calculateBacklogTimeframePercentageLeft(iter);
        assertEquals(0f, percentage, 0);
    }
    
    @Test
    @DirtiesContext
    public void testCalculateBacklogTimeframePercentageLeft_future() {
        Iteration iter = new Iteration();
        DateTime startDate = new DateTime().plusDays(4);
        DateTime endDate = startDate.plusDays(50);
        iter.setStartDate(startDate.toDateMidnight().toDateTime());
        iter.setEndDate(endDate);
        float percentage = backlogBusiness.calculateBacklogTimeframePercentageLeft(iter);
        assertEquals(1f, percentage, 1000);
    }
}
