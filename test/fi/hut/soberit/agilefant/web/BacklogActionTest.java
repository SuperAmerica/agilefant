package fi.hut.soberit.agilefant.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.HistoryBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import junit.framework.TestCase;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

/**
 * JUnit integration test for BacklogAction.
 * 
 * @author
 */
public class BacklogActionTest extends TestCase {
    // class under test
    private BacklogAction backlogAction = new BacklogAction();

    private BacklogDAO backlogDAO;
    private BacklogItemDAO backlogItemDAO;
    private IterationGoalDAO iterationGoalDAO;

    private BacklogBusiness backlogBusiness;
    private HistoryBusiness historyBusiness;
 

    /**
     * Test edit operation.
     */
    
    public void testEdit() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogAction.setBacklogDAO(backlogDAO);
        
        Product product = new Product();
        product.setId(123);
        Project project = new Project();
        project.setId(125);
        Iteration iteration = new Iteration();
        iteration.setId(666);
        
        expect(backlogDAO.get(123)).andReturn(product);
        expect(backlogDAO.get(125)).andReturn(project);
        expect(backlogDAO.get(666)).andReturn(iteration);
        expect(backlogDAO.get(-100)).andReturn(null);
        replay(backlogDAO);
        
        // test product
        backlogAction.setBacklogId(product.getId());
        assertEquals("editProduct", backlogAction.edit());
        // test project
        backlogAction.setBacklogId(project.getId());
        assertEquals("editProject", backlogAction.edit());
        // test iteration
        backlogAction.setBacklogId(iteration.getId());
        assertEquals("editIteration", backlogAction.edit());
        // test invalid
        backlogAction.setBacklogId(-100);
        assertEquals("error", backlogAction.edit());
        
        verify(backlogDAO);
    }

    /**
     * Test MoveBacklogItem method.
     */
    public void testMoveBacklogItem() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogItemDAO = createMock(BacklogItemDAO.class);
        iterationGoalDAO = createMock(IterationGoalDAO.class);
        backlogAction.setBacklogDAO(backlogDAO);
        backlogAction.setBacklogItemDAO(backlogItemDAO);
        
        // Generate test data
        Iteration origBacklog = new Iteration();
        origBacklog.setId(135);
        origBacklog.setBacklogItems(new HashSet<BacklogItem>());
        
        Project targetBacklog = new Project();
        targetBacklog.setId(128);
        targetBacklog.setBacklogItems(new HashSet<BacklogItem>());
        
        BacklogItem bli = new BacklogItem();
        bli.setId(66);
        bli.setBacklog(origBacklog);
        origBacklog.getBacklogItems().add(bli);
        
        IterationGoal ig = new IterationGoal();
        ig.setId(987);
        ig.setIteration(origBacklog);
        ig.setBacklogItems(new HashSet<BacklogItem>());
        bli.setIterationGoal(ig);
        
        backlogAction.setBacklogId(128);
        backlogAction.setBacklogItemId(66);
        
        // Record expected behavior
        expect(iterationGoalDAO.get(987)).andReturn(ig);
        expect(backlogDAO.get(128)).andReturn(targetBacklog);
        expect(backlogItemDAO.get(66)).andReturn(bli);
        bli.getBacklog().getBacklogItems().remove(bli);
        targetBacklog.getBacklogItems().add(bli);
        bli.setBacklog(targetBacklog);
        bli.getIterationGoal().getBacklogItems().remove(bli);
        bli.setIterationGoal(null);
        backlogItemDAO.store(bli);
        
        replay(backlogDAO);
        replay(backlogItemDAO);
        
        assertSame(iterationGoalDAO.get(987), bli.getIterationGoal());        
        assertEquals("editProject", backlogAction.moveBacklogItem());
        assertNull(bli.getIterationGoal());
        
        verify(backlogDAO);
        verify(backlogItemDAO);
    }
    
    /**
     * Test with invalid backlog id.
     */
    public void testMoveBacklogItem_invalidBacklogId() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogItemDAO = createMock(BacklogItemDAO.class);
        backlogAction.setBacklogDAO(backlogDAO);
        backlogAction.setBacklogItemDAO(backlogItemDAO);
        
        BacklogItem bli = new BacklogItem();
        bli.setId(66);
        
        backlogAction.setBacklogId(-100);
        backlogAction.setBacklogItemId(66);
        
        expect(backlogDAO.get(-100)).andReturn(null);
        expect(backlogItemDAO.get(66)).andReturn(bli);
        
        replay(backlogDAO);
        replay(backlogItemDAO);
        
        assertEquals(Action.ERROR, backlogAction.moveBacklogItem());
        
        verify(backlogDAO);
        verify(backlogItemDAO);
    }
    
    /**
     * Test with invalid bli id.backlogItemIds
     */
    public void testMoveBacklogItem_invalidBliId() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogItemDAO = createMock(BacklogItemDAO.class);
        backlogAction.setBacklogDAO(backlogDAO);
        backlogAction.setBacklogItemDAO(backlogItemDAO);
        
        Backlog backlog = new Project();
        backlog.setId(66);
        
        backlogAction.setBacklogId(66);
        backlogAction.setBacklogItemId(-100);
        
        expect(backlogDAO.get(66)).andReturn(backlog);
        expect(backlogItemDAO.get(-100)).andReturn(null);
        
        replay(backlogDAO);
        replay(backlogItemDAO);
        
        assertEquals(Action.ERROR, backlogAction.moveBacklogItem());
        
        verify(backlogDAO);
        verify(backlogItemDAO);
    }
    
    /**
     * Test moving selected items.
     */
    public void testDoActionOnMultipleBacklogItems() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogItemDAO = createMock(BacklogItemDAO.class);
        backlogBusiness = createMock(BacklogBusiness.class);
        
        // Generate test data
        Backlog origBacklog = new Iteration();
        origBacklog.setId(255);
        Backlog targetBacklog = new Project();
        targetBacklog.setId(100);
        
        BacklogItem bli1 = new BacklogItem();
        bli1.setId(15);
        bli1.setState(State.NOT_STARTED);
        bli1.setPriority(Priority.UNDEFINED);
        
        BacklogItem bli2 = new BacklogItem();
        bli2.setId(16);
        bli2.setState(State.STARTED);
        bli2.setPriority(Priority.BLOCKER);
        
        BacklogItem bli3 = new BacklogItem();
        bli3.setId(17);
        bli3.setState(State.PENDING);
        bli3.setPriority(Priority.TRIVIAL);
        
        BacklogItem bli4 = new BacklogItem();
        bli4.setId(18);
        bli4.setState(State.DONE);
        bli4.setPriority(Priority.MAJOR);
        
        int[] selected = {15, 17, 18};
        Map<Integer, String> responsibleIds = new HashMap<Integer, String>();
        responsibleIds.put(3, "true");
        responsibleIds.put(5, "true");
        responsibleIds.put(6, "true");
        
        // Set the attributes for the backlogAction
        backlogAction.setBacklogDAO(backlogDAO);
        backlogAction.setBacklogItemDAO(backlogItemDAO);
        backlogAction.setBacklogBusiness(backlogBusiness);
        backlogAction.setBacklogItemIds(selected);
        
        backlogAction.setTargetPriority(3);
        backlogAction.setTargetBacklog(100);
        backlogAction.setTargetIterationGoalId(-1);
        backlogAction.setTargetState(2);
        backlogAction.setBacklogId(255);
        backlogAction.setItemAction("ChangeSelected");
        backlogAction.setUserIds(responsibleIds);
        backlogAction.setKeepResponsibles(0);
        
        // The test
        expect(backlogDAO.get(255)).andReturn(origBacklog);
        try {
            backlogBusiness.changePriorityOfMultipleItems(selected,
                Priority.values()[3]);
            backlogBusiness.changeStateOfMultipleItems(selected,
                    State.values()[2]);
            backlogBusiness.setResponsiblesForMultipleBacklogItems(selected,
                    responsibleIds.keySet());
            backlogBusiness.moveMultipleBacklogItemsToBacklog(selected, 100);
        }
        catch (ObjectNotFoundException e) {
            fail(e.getMessage());
        }
        
        replay(backlogDAO);
        replay(backlogItemDAO);
        replay(backlogBusiness);
        
        assertEquals("editIteration", backlogAction.doActionOnMultipleBacklogItems());
        
        verify(backlogDAO);
        verify(backlogItemDAO);
        verify(backlogBusiness);
        
    }

    /**
     * Test delete multiple backlogItems.
     */
    public void testDeleteMultiple() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogItemDAO = createMock(BacklogItemDAO.class);
        backlogBusiness = createMock(BacklogBusiness.class);
        
        backlogAction.setBacklogDAO(backlogDAO);
        backlogAction.setBacklogItemDAO(backlogItemDAO);
        backlogAction.setBacklogBusiness(backlogBusiness);
        backlogAction.setItemAction("DeleteSelected");
        backlogAction.setBacklogId(10);
        
        // Create test data
        Backlog project = new Project();
        project.setId(10);
        BacklogItem bli1 = new BacklogItem();
        BacklogItem bli2 = new BacklogItem();
        BacklogItem bli3 = new BacklogItem();
        bli1.setId(15);
        bli2.setId(16);
        bli3.setId(17);
        int[] selected = {15, 17};
        
        backlogAction.setBacklogItemIds(selected);
        
        expect(backlogDAO.get(10)).andReturn(project);
        try {
            backlogBusiness.deleteMultipleItems(10, selected);
        }
        catch (ObjectNotFoundException e) {
            fail(e.getMessage());
        }
        
        replay(backlogDAO);
        replay(backlogItemDAO);
        replay(backlogBusiness);
        
        backlogAction.doActionOnMultipleBacklogItems();
        
        verify(backlogDAO);
        verify(backlogItemDAO);
        verify(backlogBusiness);
    }
}
