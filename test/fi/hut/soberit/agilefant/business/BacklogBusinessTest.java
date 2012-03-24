package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.envers.RevisionType;
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
import fi.hut.soberit.agilefant.db.history.BacklogHistoryDAO;
import fi.hut.soberit.agilefant.model.AgilefantRevisionEntity;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;


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
    @Mock
    private StoryBusiness storyBusiness;
    @Mock
    private BacklogHistoryDAO backlogHistoryDAO;
    
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
        iter.setEndDate(endDate.toDateMidnight().toDateTime());
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
    
    
    @Test
    @DirtiesContext
    public void testRetrieveUnexpectedStories_empty() {
        Iteration iteration = new Iteration();
        expect(this.backlogHistoryDAO.retrieveAddedStories(iteration)).andReturn(new ArrayList<AgilefantHistoryEntry>());
        expect(this.backlogHistoryDAO.retrieveDeletedStories(iteration)).andReturn(new ArrayList<AgilefantHistoryEntry>());
        replayAll();
        List<Story> actual = this.backlogBusiness.retrieveUnexpectedStories(iteration);
        verifyAll();
        assertEquals(0, actual.size());
    }
    
    @Test
    @DirtiesContext
    public void testRetrieveUnexpectedStories_oneAdded() {
        Iteration iteration = new Iteration();
        iteration.setStartDate(new DateTime(2010,1,1,0,0,0,0));
        iteration.setEndDate(new DateTime(2010,2,1,0,0,0,0));
        
        Story added = new Story();
        Story current = new Story();
        added.setId(1);
        AgilefantRevisionEntity addedRevision = new AgilefantRevisionEntity();
        addedRevision.setTimestamp(new DateTime(2010,1,15,0,0,0,0).getMillis());
        AgilefantHistoryEntry addedEntry = new AgilefantHistoryEntry(added, addedRevision, RevisionType.ADD);
        
        expect(this.backlogHistoryDAO.retrieveAddedStories(iteration)).andReturn(Arrays.asList(addedEntry));
        expect(this.backlogHistoryDAO.retrieveDeletedStories(iteration)).andReturn(new ArrayList<AgilefantHistoryEntry>());
        expect(this.storyBusiness.retrieveIfExists(1)).andReturn(current);
        
        replayAll();
        List<Story> actual = this.backlogBusiness.retrieveUnexpectedStories(iteration);
        verifyAll();
        assertSame(current, actual.get(0));
    }
    
    @Test
    @DirtiesContext
    public void testRetrieveUnexpectedStories_addedAndDeleted() {
        Iteration iteration = new Iteration();
        iteration.setStartDate(new DateTime(2010,1,1,0,0,0,0));
        iteration.setEndDate(new DateTime(2010,2,1,0,0,0,0));
        
        Story added = new Story();
        added.setId(1);
        AgilefantRevisionEntity addedRevision = new AgilefantRevisionEntity();
        addedRevision.setTimestamp(new DateTime(2010,1,15,0,0,0,0).getMillis());
        AgilefantHistoryEntry addedEntry = new AgilefantHistoryEntry(added, addedRevision, RevisionType.ADD);
        
        AgilefantRevisionEntity deletedRevision = new AgilefantRevisionEntity();
        deletedRevision.setTimestamp(new DateTime(2010,1,20,0,0,0,0).getMillis());
        AgilefantHistoryEntry deletedEntry = new AgilefantHistoryEntry(1, RevisionType.DEL, deletedRevision);
        
        expect(this.backlogHistoryDAO.retrieveAddedStories(iteration)).andReturn(Arrays.asList(addedEntry));
        expect(this.backlogHistoryDAO.retrieveDeletedStories(iteration)).andReturn(Arrays.asList(deletedEntry));
        
        replayAll();
        List<Story> actual = this.backlogBusiness.retrieveUnexpectedStories(iteration);
        verifyAll();
        assertEquals(0, actual.size());
    }
    
    @Test
    @DirtiesContext
    public void testRetrieveUnexpectedStories_addedBeforeStart() {
        Iteration iteration = new Iteration();
        iteration.setStartDate(new DateTime(2010,2,1,0,0,0,0));
        iteration.setEndDate(new DateTime(2010,4,1,0,0,0,0));
        
        Story added = new Story();
        added.setId(1);
        AgilefantRevisionEntity addedRevision = new AgilefantRevisionEntity();
        addedRevision.setTimestamp(new DateTime(2010,1,15,0,0,0,0).getMillis());
        AgilefantHistoryEntry addedEntry = new AgilefantHistoryEntry(added, addedRevision, RevisionType.ADD);
        
        expect(this.backlogHistoryDAO.retrieveAddedStories(iteration)).andReturn(Arrays.asList(addedEntry));
        expect(this.backlogHistoryDAO.retrieveDeletedStories(iteration)).andReturn(new ArrayList<AgilefantHistoryEntry>());
        
        replayAll();
        List<Story> actual = this.backlogBusiness.retrieveUnexpectedStories(iteration);
        verifyAll();
        assertEquals(0, actual.size());
    }
    
    @Test
    @DirtiesContext
    public void testRetrieveUnexpectedStories_movedAfterEnd() {
        Iteration iteration = new Iteration();
        iteration.setStartDate(new DateTime(2010,1,1,0,0,0,0));
        iteration.setEndDate(new DateTime(2010,2,1,0,0,0,0));
        
        Story added = new Story();
        added.setId(1);
        AgilefantRevisionEntity addedRevision = new AgilefantRevisionEntity();
        addedRevision.setTimestamp(new DateTime(2010,1,15,0,0,0,0).getMillis());
        AgilefantHistoryEntry addedEntry = new AgilefantHistoryEntry(added, addedRevision, RevisionType.ADD);
        
        AgilefantRevisionEntity deletedRevision = new AgilefantRevisionEntity();
        deletedRevision.setTimestamp(new DateTime(2010,2,20,0,0,0,0).getMillis());
        AgilefantHistoryEntry deletedEntry = new AgilefantHistoryEntry(1, RevisionType.DEL, deletedRevision);
        
        expect(this.backlogHistoryDAO.retrieveAddedStories(iteration)).andReturn(Arrays.asList(addedEntry));
        expect(this.backlogHistoryDAO.retrieveDeletedStories(iteration)).andReturn(Arrays.asList(deletedEntry));
        expect(this.storyBusiness.retrieveIfExists(1)).andReturn(added);
        
        replayAll();
        List<Story> actual = this.backlogBusiness.retrieveUnexpectedStories(iteration);
        verifyAll();
        assertEquals(1, actual.size());
    }
    
    @Test
    @DirtiesContext
    public void testRetrieveUnexpectedStories_deletedAfterEnd() {
        Iteration iteration = new Iteration();
        iteration.setStartDate(new DateTime(2010,1,1,0,0,0,0));
        iteration.setEndDate(new DateTime(2010,2,1,0,0,0,0));
        
        Story added = new Story();
        added.setId(1);
        AgilefantRevisionEntity addedRevision = new AgilefantRevisionEntity();
        addedRevision.setTimestamp(new DateTime(2010,1,15,0,0,0,0).getMillis());
        AgilefantHistoryEntry addedEntry = new AgilefantHistoryEntry(added, addedRevision, RevisionType.ADD);
        
        expect(this.backlogHistoryDAO.retrieveAddedStories(iteration)).andReturn(Arrays.asList(addedEntry));
        expect(this.backlogHistoryDAO.retrieveDeletedStories(iteration)).andReturn(new ArrayList<AgilefantHistoryEntry>());
        expect(this.storyBusiness.retrieveIfExists(1)).andReturn(null);
        
        replayAll();
        List<Story> actual = this.backlogBusiness.retrieveUnexpectedStories(iteration);
        verifyAll();
        assertSame(added, actual.get(0));
    }
}
