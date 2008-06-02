package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.BacklogItemBusinessImpl;
import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;

public class BacklogItemBusinessTest extends TestCase {

    private BacklogItemBusinessImpl bliBusiness = new BacklogItemBusinessImpl();
    private BacklogItemDAO bliDAO;
    private HourEntryBusinessImpl hourEntryBusiness = new HourEntryBusinessImpl();
    private HistoryBusiness historyBusiness = createMock(HistoryBusiness.class);
    private UserBusiness userBusiness;
    private BacklogItemHourEntryDAO bliheDAO;

    public void testRemoveBacklogItem_found() {
        bliDAO = createMock(BacklogItemDAO.class);
        bliheDAO = createMock( BacklogItemHourEntryDAO.class );
        bliBusiness.setBacklogItemDAO(bliDAO);
        bliBusiness.setHistoryBusiness(historyBusiness);
        bliBusiness.setHourEntryBusiness(hourEntryBusiness);
        hourEntryBusiness.setHourEntryDAO( bliheDAO );
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
     * Test the getPossibleResponsibles method.
     * <p>
     * Should return the union of the bli's responsibles
     * and all enabled users.
     */
    public void testGetPossibleResponsibles() {
        userBusiness = createMock(UserBusiness.class);
        bliBusiness.setBacklogItemDAO(bliDAO);
        bliBusiness.setUserBusiness(userBusiness);
        
        // Enabled user
        User user1 = new User();
        user1.setId(1);
        user1.setEnabled(true);
        
        // Disabled user
        User user2 = new User();
        user2.setId(2);
        user2.setEnabled(false);
        
        // Backlog item with no previous responsibles
        BacklogItem bli1 = new BacklogItem();
        bli1.setResponsibles(new ArrayList<User>());
        
        // Backlog item with previous, enabled responsible
        BacklogItem bli2 = new BacklogItem();
        bli2.setResponsibles(new ArrayList<User>());
        bli2.getResponsibles().add(user1);
        
        // Backlog item with previous, disabled responsible
        BacklogItem bli3 = new BacklogItem();
        bli3.setResponsibles(new ArrayList<User>());
        bli3.getResponsibles().add(user2);
        
        // Backlog item with previous, enabled and disabled responsible
        BacklogItem bli4 = new BacklogItem();
        bli4.setResponsibles(new ArrayList<User>());
        bli4.getResponsibles().add(user1);
        bli4.getResponsibles().add(user2);

        
        //The lists
        List<User> enabledList = new ArrayList<User>();
        enabledList.add(user1);
       
        // Record expected behavior
        expect(userBusiness.getEnabledUsers()).andReturn(enabledList);
        expect(userBusiness.getEnabledUsers()).andReturn(enabledList);
        expect(userBusiness.getEnabledUsers()).andReturn(enabledList);
        expect(userBusiness.getEnabledUsers()).andReturn(enabledList);
        
        // Test it
        replay(userBusiness);
        
        // Call the methods
        List<User> bli1list = bliBusiness.getPossibleResponsibles(bli1);
        List<User> bli2list = bliBusiness.getPossibleResponsibles(bli2);
        List<User> bli3list = bliBusiness.getPossibleResponsibles(bli3);
        List<User> bli4list = bliBusiness.getPossibleResponsibles(bli4);
        
        // Check, that correct users are in the list
        assertTrue(bli1list.contains(user1));
        assertFalse(bli1list.contains(user2));
        
        assertTrue(bli2list.contains(user1));
        assertFalse(bli2list.contains(user2));
        
        assertTrue(bli3list.contains(user1));
        assertTrue(bli3list.contains(user2));
        
        assertTrue(bli4list.contains(user1));
        assertTrue(bli4list.contains(user2));
        
        // Check for duplicates
        Set<User> previousUsers = new HashSet<User>();
        for (User user : bli1list) {
            if (previousUsers.contains(user)) {
                fail("Duplicate entry");
            }
            else {
                previousUsers.add(user);
            }
        }
        
        previousUsers.clear();
        for (User user : bli2list) {
            if (previousUsers.contains(user)) {
                fail("Duplicate entry");
            }
            else {
                previousUsers.add(user);
            }
        }
        
        previousUsers.clear();
        for (User user : bli3list) {
            if (previousUsers.contains(user)) {
                fail("Duplicate entry");
            }
            else {
                previousUsers.add(user);
            }
        }
        
        previousUsers.clear();
        for (User user : bli4list) {
            if (previousUsers.contains(user)) {
                fail("Duplicate entry");
            }
            else {
                previousUsers.add(user);
            }
        }

        verify(userBusiness);
    }   
}
