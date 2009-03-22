package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Collection;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.BacklogItemBusinessImpl;
import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

public class BacklogItemBusinessTest extends TestCase {

    private BacklogItemBusinessImpl bliBusiness = new BacklogItemBusinessImpl();
    private BacklogItemDAO bliDAO;
    private HourEntryBusinessImpl hourEntryBusiness = new HourEntryBusinessImpl();
    private HistoryBusiness historyBusiness = createMock(HistoryBusiness.class);
    private BacklogItemHourEntryDAO bliheDAO;

    public void testRemoveBacklogItem_found() {
        bliDAO = createMock(BacklogItemDAO.class);
        bliheDAO = createMock( BacklogItemHourEntryDAO.class );
        bliBusiness.setBacklogItemDAO(bliDAO);
        bliBusiness.setHistoryBusiness(historyBusiness);
        bliBusiness.setHourEntryBusiness(hourEntryBusiness);
        hourEntryBusiness.setBacklogItemHourEntryDAO( bliheDAO );
        Backlog backlog = new Iteration();
        backlog.setId(100);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);

        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(bli);
        bliDAO.remove(bli);
        replay(bliDAO);
        historyBusiness.updateBacklogHistory(100);
        replay(historyBusiness);
        // run method under test

        try {
            bliBusiness.removeBacklogItem(68);
        } catch (ObjectNotFoundException onfe) {
            fail();
        }
        // verify behavior
        verify(bliDAO);
        verify(historyBusiness);
    }

    public void testRemoveBacklogItem_notfound() {
        bliDAO = createMock(BacklogItemDAO.class);
        bliBusiness.setBacklogItemDAO(bliDAO);

        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(null);
        replay(bliDAO);
        // run method under test
        try {
            bliBusiness.removeBacklogItem(68);
            fail();
        } catch (ObjectNotFoundException onfe) {

        }

        // verify behavior
        verify(bliDAO);
    }

    private void testUpdateBacklogItemStateAndEffortLeft_parameterized(
            State oldState, AFTime oldOriginalEstimate, AFTime oldEffortLeft,
            State newState, AFTime expectedOriginalEstimate,
            AFTime newEffortLeft, AFTime expectedEffortLeft) {

        // Declare test values
        final int bliId = 68;
        final int backlogId = 99;

        // Create bli mock
        bliDAO = createMock(BacklogItemDAO.class);
        bliBusiness.setBacklogItemDAO(bliDAO);
        bliBusiness.setHistoryBusiness(historyBusiness);

        // Create new backlog item and set test values to it
        BacklogItem bli = new BacklogItem();
        bli.setId(bliId);
        bli.setEffortLeft(oldEffortLeft);
        bli.setOriginalEstimate(oldOriginalEstimate);
        bli.setState(oldState);

        // Create backlog for backlog item
        Backlog backlog = new Iteration();
        backlog.setId(backlogId);
        bli.setBacklog(backlog);

        // Record expectations for mocks
        // bliDao mock
        expect(bliDAO.get(bliId)).andReturn(bli);
        bliDAO.store(bli);
        replay(bliDAO);

        // historyBusiness mock
        historyBusiness.updateBacklogHistory(backlogId);
        replay(historyBusiness);

        try {
            bliBusiness.updateBacklogItemStateAndEffortLeft(bliId, newState,
                    newEffortLeft);
        } catch (ObjectNotFoundException onfe) {
            fail("Object not found.");
        }

        assertEquals(newState, bli.getState());
        assertEquals(expectedEffortLeft, bli.getEffortLeft());
        assertEquals(expectedOriginalEstimate, bli.getOriginalEstimate());

        verify(bliDAO);
        verify(historyBusiness);
    }

    /**
     * Basic test to test the common functionality, when the backlog item is
     * given a new state and effort left.
     */

    public void testUpdateBacklogItemStateAndEffortLeft_basic() {
        testUpdateBacklogItemStateAndEffortLeft_parameterized(
                State.NOT_STARTED, new AFTime("10h"), new AFTime("3h 20min"),
                State.IMPLEMENTED, new AFTime("10h"), new AFTime("1h"),
                new AFTime("1h"));
    }

    /**
     * Expect the new original estimate to be the same as new effort left if it
     * is originally null
     */

    public void testUpdateBacklogItemStateAndEffortLeft_nullOriginalEstimate() {

        testUpdateBacklogItemStateAndEffortLeft_parameterized(
                State.NOT_STARTED, null, new AFTime("3h 20min"),
                State.IMPLEMENTED, new AFTime("1h"), new AFTime("1h"),
                new AFTime("1h"));
    }

    /**
     * Check that the effort left is zeroed when the the status is set to DONE.
     * Even if the business method is given a new non-zero effort left value as
     * parameter.
     */
    public void testUpdateBacklogItemStateAndEffortLeft_newStateDone() {

        testUpdateBacklogItemStateAndEffortLeft_parameterized(
                State.NOT_STARTED, new AFTime("10h"), new AFTime("3h 20min"),
                State.DONE, new AFTime("10h"), new AFTime("100h"), new AFTime(
                        "0h"));
    }

    public void testResetBliOrigEstAndEffortLeft_found() {

        // Declare test values
        final int bliId = 68;
        final int backlogId = 99;

        bliDAO = createMock(BacklogItemDAO.class);
        bliBusiness.setBacklogItemDAO(bliDAO);
        bliBusiness.setHistoryBusiness(historyBusiness);

        // Create new backlog item and set test values to it
        BacklogItem bli = new BacklogItem();
        bli.setId(bliId);
        bli.setEffortLeft(new AFTime("2h"));
        bli.setOriginalEstimate(new AFTime("3h"));

        // Create backlog for backlog item
        Backlog backlog = new Iteration();
        backlog.setId(backlogId);
        bli.setBacklog(backlog);

        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(bli);
        bliDAO.store(bli);
        replay(bliDAO);
        historyBusiness.updateBacklogHistory(99);
        replay(historyBusiness);

        // run method under test
        try {
            bliBusiness.resetBliOrigEstAndEffortLeft(68);
        } catch (ObjectNotFoundException onfe) {
            fail();
        }

        assertEquals(null, bli.getEffortLeft());
        assertEquals(null, bli.getOriginalEstimate());

        // verify behavior
        verify(bliDAO);
        verify(historyBusiness);
    }

    public void testResetBliOrigEstAndEffortLeft_notfound() {
        bliDAO = createMock(BacklogItemDAO.class);
        bliBusiness.setBacklogItemDAO(bliDAO);

        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(null);
        replay(bliDAO);
        // run method under test
        try {
            bliBusiness.resetBliOrigEstAndEffortLeft(68);
            fail();
        } catch (ObjectNotFoundException onfe) {

        }

        // verify behavior
        verify(bliDAO);
    }
    
    
    /**
     * Move backlog item to another backlog within the same product
     */
    public void testMoveItemToBacklog_sameProductWithThemes() {
        BacklogBusiness blBusiness = createMock(BacklogBusiness.class);
        HistoryBusiness hBuss = createMock(HistoryBusiness.class);
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        testable.setBacklogBusiness(blBusiness);
        testable.setHistoryBusiness(hBuss);
        
        Project proj1 = new Project();
        proj1.setId(1);
        Project proj2 = new Project();
        proj2.setId(2);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(proj1);
        proj1.getBacklogItems().add(bli);
        BusinessTheme prodTheme = new BusinessTheme();
        prodTheme.setGlobal(false);
        BusinessTheme globalTheme = new BusinessTheme();
        globalTheme.setGlobal(true);
        bli.getBusinessThemes().add(globalTheme);
        bli.getBusinessThemes().add(prodTheme);
        
        //fake under same backlog
        expect(blBusiness.isUnderSameProduct(proj1, proj2)).andReturn(true);
        //expect history updates
        hBuss.updateBacklogHistory(1);
        hBuss.updateBacklogHistory(2);
        
        replay(hBuss);
        replay(blBusiness);
        
        testable.moveItemToBacklog(bli, proj2);
        
        assertEquals(2, bli.getBusinessThemes().size());
        assertEquals(0, proj1.getBacklogItems().size());
        assertEquals(1, proj2.getBacklogItems().size());
        assertEquals(proj2, bli.getBacklog());
        
        verify(hBuss);
        verify(blBusiness);
    }
    
    /**
     * Move backlog item to a backlog in a different product.
     */
    public void testMoveItemToBacklog_DifferentProduct() {
        BacklogBusiness blBusiness = createMock(BacklogBusiness.class);
        HistoryBusiness hBuss = createMock(HistoryBusiness.class);
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        testable.setBacklogBusiness(blBusiness);
        testable.setHistoryBusiness(hBuss);
        
        Project proj1 = new Project();
        proj1.setId(1);
        Project proj2 = new Project();
        proj2.setId(2);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(proj1);
        proj1.getBacklogItems().add(bli);
        BusinessTheme prodTheme = new BusinessTheme();
        prodTheme.setGlobal(false);
        BusinessTheme globalTheme = new BusinessTheme();
        globalTheme.setGlobal(true);
        bli.getBusinessThemes().add(globalTheme);
        bli.getBusinessThemes().add(prodTheme);

        //fake under same backlog
        expect(blBusiness.isUnderSameProduct(proj1, proj2)).andReturn(false);
        //expect history updates
        hBuss.updateBacklogHistory(1);
        hBuss.updateBacklogHistory(2);
        
        replay(hBuss);
        replay(blBusiness);
        
        testable.moveItemToBacklog(bli, proj2);
        
        assertEquals(1, bli.getBusinessThemes().size());
        assertTrue(bli.getBusinessThemes().contains(globalTheme));
        assertEquals(0, proj1.getBacklogItems().size());
        assertEquals(1, proj2.getBacklogItems().size());
        assertEquals(proj2, bli.getBacklog());
        
        verify(hBuss);
        verify(blBusiness);
        
    }
    
    public void testSetBacklogItemIterationGoal_RightIteration() {
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        
        Iteration iter = new Iteration();
        IterationGoal goal = new IterationGoal();
        BacklogItem bli = new BacklogItem();
        goal.setIteration(iter);
        bli.setBacklog(iter);
        
        testable.setBacklogItemIterationGoal(bli, goal);
        assertEquals(goal, bli.getIterationGoal());
        assertTrue(goal.getBacklogItems().contains(bli));
        
    }
    
    public void testSetBacklogItemIterationGoal_ChangeGoal() {
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        
        Iteration iter = new Iteration();
        IterationGoal goal = new IterationGoal();
        IterationGoal oldGoal = new IterationGoal();
        BacklogItem bli = new BacklogItem();
        goal.setIteration(iter);
        bli.setBacklog(iter);
        bli.setIterationGoal(oldGoal);
        oldGoal.getBacklogItems().add(bli);
        
        testable.setBacklogItemIterationGoal(bli, goal);
        assertEquals(goal, bli.getIterationGoal());
        assertTrue(goal.getBacklogItems().contains(bli));
        assertFalse(oldGoal.getBacklogItems().contains(bli));
        
    }
    
    public void testSetBacklogItemIterationGoal_WrongIteration() {
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        
        Iteration iter = new Iteration();
        IterationGoal goal = new IterationGoal();
        BacklogItem bli = new BacklogItem();
        goal.setIteration(iter);
        
        Iteration target = new Iteration();
        bli.setBacklog(target);
        
        testable.setBacklogItemIterationGoal(bli, goal);
        assertEquals(null, bli.getIterationGoal());
        assertFalse(goal.getBacklogItems().contains(bli));
    }
    
    public void testSetBacklogItemIterationGoal_InProject() {
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        
        Iteration iter = new Iteration();
        IterationGoal goal = new IterationGoal();
        BacklogItem bli = new BacklogItem();
        goal.setIteration(iter);

        Project target = new Project();
        bli.setBacklog(target);
        
        testable.setBacklogItemIterationGoal(bli, goal);
        assertEquals(null, bli.getIterationGoal());
        assertFalse(goal.getBacklogItems().contains(bli));
    }
    
    public void testStoreBacklogItem_NoEffortLeft() {
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        BacklogItemDAO dao = createMock(BacklogItemDAO.class);
        HistoryBusiness hBuss = createMock(HistoryBusiness.class);
        testable.setBacklogItemDAO(dao);
        testable.setHistoryBusiness(hBuss);
        
        BacklogItem persisted = new BacklogItem();
        BacklogItem dataItem = new BacklogItem();
        Backlog backlog = new Project();
        dataItem.setOriginalEstimate(new AFTime(100));
        dataItem.setEffortLeft(null);
        persisted.setBacklog(backlog);
        backlog.setId(1);
        persisted.setId(1);
        
        dao.store(persisted);
        hBuss.updateBacklogHistory(1); 
        
        replay(dao);
        replay(hBuss);
        
        testable.storeBacklogItem(persisted, backlog, dataItem, null, null);
        assertEquals(persisted.getEffortLeft(), dataItem.getOriginalEstimate());
        
        verify(dao);
        verify(hBuss);

    }
    
    public void testStoreBacklogItem_createNew() {
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        BacklogItemDAO dao = createMock(BacklogItemDAO.class);
        HistoryBusiness hBuss = createMock(HistoryBusiness.class);
        testable.setBacklogItemDAO(dao);
        testable.setHistoryBusiness(hBuss);

        BacklogItem persisted = new BacklogItem();
        BacklogItem dataItem = new BacklogItem();
        Backlog backlog = new Project();
        dataItem.setState(State.NOT_STARTED);
        dataItem.setOriginalEstimate(new AFTime(100));
        dataItem.setName("TEST");
        dataItem.setDescription("");
        backlog.setId(1);
        
        expect(dao.create(persisted)).andReturn(new Integer(1));
        expect(dao.get(1)).andReturn(persisted);
        hBuss.updateBacklogHistory(1);

        replay(dao);
        replay(hBuss);

        BacklogItem item = testable.storeBacklogItem(persisted, backlog, dataItem, null, null);
        assertEquals(dataItem.getState(), item.getState());
        assertEquals(dataItem.getDescription(), item.getDescription());
        assertEquals(dataItem.getName(), item.getName());
        assertEquals(dataItem.getOriginalEstimate(), item.getOriginalEstimate());
        assertEquals(dataItem.getOriginalEstimate(), item.getEffortLeft());
        assertEquals(backlog, item.getBacklog());
        
        
        verify(dao);
        verify(hBuss);
    }
    
    
    public void testStoreBacklogItem_DoneItem() {
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        BacklogItemDAO dao = createMock(BacklogItemDAO.class);
        HistoryBusiness hBuss = createMock(HistoryBusiness.class);
        testable.setBacklogItemDAO(dao);
        testable.setHistoryBusiness(hBuss);

        BacklogItem persisted = new BacklogItem();
        persisted.setId(1);
        BacklogItem dataItem = new BacklogItem();
        Backlog backlog = new Project();
        dataItem.setState(State.DONE);
        dataItem.setEffortLeft(new AFTime(100));
        dataItem.setBacklog(backlog);
        backlog.setId(1);
        
        dao.store(persisted);
        hBuss.updateBacklogHistory(1);

        replay(dao);
        replay(hBuss);

        testable.storeBacklogItem(persisted, backlog, dataItem, null, null);
        assertEquals(persisted.getEffortLeft().getTime(), 0);
        
        verify(dao);
        verify(hBuss);
    }
    public void testStoreBacklogItem_ChangeBacklog() {
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        BacklogItemDAO dao = createMock(BacklogItemDAO.class);
        HistoryBusiness hBuss = createMock(HistoryBusiness.class);
        BacklogBusiness blBuss = createMock(BacklogBusiness.class);
        testable.setBacklogItemDAO(dao);
        testable.setHistoryBusiness(hBuss);
        testable.setBacklogBusiness(blBuss);

        BacklogItem persisted = new BacklogItem();
        persisted.setId(1);
        BacklogItem dataItem = new BacklogItem();
        Backlog backlog = new Project();
        Backlog iter = new Iteration();
        iter.setId(2);
        persisted.setBacklog(backlog);
        backlog.setId(1);
        
        dao.store(persisted);
        hBuss.updateBacklogHistory(1);
        hBuss.updateBacklogHistory(2);
        expect(blBuss.isUnderSameProduct(backlog, iter)).andReturn(true);

        replay(dao);
        replay(hBuss);
        replay(blBuss);

        testable.storeBacklogItem(persisted, iter, dataItem, null, null);
        assertEquals(iter, persisted.getBacklog());
        
        verify(dao);
        verify(hBuss);
        verify(blBuss);
    }
    
    public void testCreateFromTodo() {
        TaskBusiness tb = createMock(TaskBusiness.class);
        BacklogItemBusinessImpl testable = new BacklogItemBusinessImpl();
        testable.setTaskBusiness(tb);
        Task from = new Task();
        BacklogItem parent = new BacklogItem();
        Iteration bl = new Iteration();
        parent.setBacklog(bl);
        Collection<User> users = new ArrayList<User>();
        User u1 = new User();
        u1.setId(1);
        users.add(u1);
        parent.setResponsibles(users);
        
        parent.setPriority(Priority.BLOCKER);
        
        from.setBacklogItem(parent);
        from.setState(State.DONE);
        from.setName("foo");
        
        expect(tb.getTask(1)).andReturn(from).once();
        replay(tb);
        
        BacklogItem r = testable.createBacklogItemFromTodo(1);
        assertEquals(from.getState(), r.getState());
        assertEquals(from.getName(), r.getName());
        assertEquals(parent.getResponsibles(), r.getResponsibles());
        assertEquals(parent.getPriority(), r.getPriority());
        
        verify(tb);
    }
}
