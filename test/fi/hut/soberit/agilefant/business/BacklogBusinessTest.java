package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Ignore;

import fi.hut.soberit.agilefant.business.impl.BacklogBusinessImpl;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.User;

/**
 * A spring test case for testing the Backlog business layer.
 * 
 * @author hhaataja, rstrom
 * 
 */

public class BacklogBusinessTest extends TestCase {

    private BacklogBusinessImpl backlogBusiness = new BacklogBusinessImpl();
    private HistoryBusiness historyBusiness;
    private BacklogItemDAO bliDAO;
    private BacklogDAO backlogDAO;
    private AssignmentDAO assignmentDAO;
    private UserDAO userDAO;

    public void testChangePriorityOfMultipleItems() throws Exception {
        bliDAO = createMock(BacklogItemDAO.class);
        backlogBusiness.setBacklogItemDAO(bliDAO);
        BacklogItem bli = new BacklogItem();

        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(bli);
        replay(bliDAO);
        // run method under test
        int ids[] = { 68 };
        backlogBusiness.changePriorityOfMultipleItems(ids, Priority.BLOCKER);
        assertEquals(Priority.BLOCKER, bli.getPriority());

        // verify behavior
        verify(bliDAO);
    }

    public void testCreateBacklogItemToBacklog() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogBusiness.setBacklogDAO(backlogDAO);
        Backlog backlog = new Iteration();

        // Record expected behavior
        expect(backlogDAO.get(68)).andReturn(backlog);
        replay(backlogDAO);
        // run method under test
        BacklogItem bli = backlogBusiness.createBacklogItemToBacklog(68);
        assertEquals(backlog, bli.getBacklog());
        assertTrue(backlog.getBacklogItems().contains(bli));

        // verify behavior
        verify(backlogDAO);
    }

    public void testCreateBakclogItemToBacklog_notFound() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogBusiness.setBacklogDAO(backlogDAO);
        Backlog backlog = new Iteration();

        // Record expected behavior
        expect(backlogDAO.get(-100)).andReturn(null);
        replay(backlogDAO);
        // run method under test
        BacklogItem bli = backlogBusiness.createBacklogItemToBacklog(-100);
        assertEquals(null, bli);
        assertEquals(0, backlog.getBacklogItems().size());

        // verify behavior
        verify(backlogDAO);
    }

    public void testDeleteMultipleItems() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogBusiness.setBacklogDAO(backlogDAO);
        bliDAO = createMock(BacklogItemDAO.class);
        backlogBusiness.setBacklogItemDAO(bliDAO);
        historyBusiness = createMock(HistoryBusiness.class);
        backlogBusiness.setHistoryBusiness(historyBusiness);

        Backlog backlog = new Iteration();
        backlog.setId(100);
        BacklogItem bli = new BacklogItem();
        bli.setId(68);
        bli.setBacklog(backlog);
        ArrayList<BacklogItem> blis = new ArrayList<BacklogItem>();
        blis.add(bli);
        backlog.setBacklogItems(blis);

        // Record expected behavior
        expect(backlogDAO.get(backlog.getId())).andReturn(backlog);
        bliDAO.remove(bli.getId());
        historyBusiness.updateBacklogHistory(backlog.getId());
        replay(backlogDAO);
        replay(bliDAO);
        replay(historyBusiness);

        // run method under test
        int[] bliIds = { bli.getId() };
        try {
            backlogBusiness.deleteMultipleItems(backlog.getId(), bliIds);
        } catch (ObjectNotFoundException e) {
            fail();
        }
        assertFalse(backlog.getBacklogItems().contains(bli));

        // verify behavior
        verify(backlogDAO);
        verify(bliDAO);
        verify(historyBusiness);
    }

    @Ignore
    public void testSetAssignments() {
        backlogDAO = createMock(BacklogDAO.class);
        userDAO = createMock(UserDAO.class);
        assignmentDAO = createMock(AssignmentDAO.class);
        backlogBusiness.setBacklogDAO(backlogDAO);
        backlogBusiness.setUserDAO(userDAO);
        backlogBusiness.setAssignmentDAO(assignmentDAO);

        Backlog backlog = new Project();
        backlog.setId(100);
        // create users
        User user1, user2;
        user1 = new User();
        user1.setLoginName("user1");
        user2 = new User();
        user2.setLoginName("user2");
        user1.setId(1);
        user2.setId(2);
        Assignment assignment1 = new Assignment(user1, backlog);
        Assignment assignment2 = new Assignment(user2, backlog);

        // Record expected behavior
        expect(userDAO.get(user1.getId())).andReturn(user1);
        assignmentDAO.store(assignment1);
        userDAO.store(user1);
        backlogDAO.store(backlog);
        
        expect(userDAO.get(user2.getId())).andReturn(user2);
        assignmentDAO.store(assignment2);
        userDAO.store(user2);
        backlogDAO.store(backlog);
        
        replay(userDAO);

        // run method under test
        int[] selectedUserIds = { user1.getId(), user2.getId() };
        assertEquals(0, backlog.getAssignments().size());
        backlogBusiness.setAssignments(selectedUserIds, backlog);
        assertEquals(2, backlog.getAssignments().size());     
       
        
        // verify behavior
        verify(userDAO);

    }
}
