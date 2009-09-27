package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.AssignmentBusinessImpl;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;
import fi.hut.soberit.agilefant.model.User;

public class AssignmentBusinessTest {

    AssignmentBusinessImpl testable;
    UserBusiness userBusiness;
    AssignmentDAO assignmentDAO;

    Assignment assignment;

    @Before
    public void setUp() {
        testable = new AssignmentBusinessImpl();

        userBusiness = createMock(UserBusiness.class);
        testable.setUserBusiness(userBusiness);

        assignmentDAO = createMock(AssignmentDAO.class);
        testable.setAssignmentDAO(assignmentDAO);

        assignment = new Assignment();
    }

    private void replayAll() {
        replay(userBusiness, assignmentDAO);
    }

    private void verifyAll() {
        verify(userBusiness, assignmentDAO);
    }

    @Test
    public void testGetAssignedUserIds() {
        Iteration iter = new Iteration();
        User user = new User();
        user.setId(1);
        Assignment as1 = new Assignment(user, iter);
        iter.getAssignments().add(as1);
        Set<Integer> actual = testable.getAssignedUserIds(iter);
        assertEquals(1, actual.size());
        assertTrue(actual.contains(1));
    }
    
    @Test
    public void testGetAssignedUserIds_Project() {
        Project iter = new Project();
        User user = new User();
        user.setId(1);
        Assignment as1 = new Assignment(user, iter);
        iter.getAssignments().add(as1);
        Set<Integer> actual = testable.getAssignedUserIds(iter);
        assertEquals(1, actual.size());
        assertTrue(actual.contains(1));
    }
    
    @Test
    public void testGetAssignedUserIds_Product() {
        Product iter = new Product();
        Set<Integer> actual = testable.getAssignedUserIds(iter);
        assertEquals(0, actual.size());
    }
    
    @Test
    public void testStore() {
        SignedExactEstimate personalLoad = new SignedExactEstimate(100L);
        expect(assignmentDAO.get(10)).andReturn(assignment);
        assignmentDAO.store(assignment);
        replayAll();
        testable.store(10, personalLoad, 100);
        assertEquals(100, assignment.getAvailability());
        assertEquals(personalLoad, assignment.getPersonalLoad());
        verifyAll();
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testStore_notFound() {
        SignedExactEstimate personalLoad = new SignedExactEstimate(100L);
        expect(assignmentDAO.get(10)).andThrow(new ObjectNotFoundException());
        replayAll();
        testable.store(10, personalLoad, 100);
        verifyAll();
    }

    @Test
    public void testAddMultiple() {
        User user1 = new User();
        user1.setId(1);
        User user2 = new User();
        user2.setId(2);
        User user3 = new User();
        user3.setId(3);
        Iteration iter = new Iteration();
        Assignment existingAssignment = new Assignment(user3, iter);
        iter.getAssignments().add(existingAssignment);
        SignedExactEstimate personalLoad = new SignedExactEstimate(100L);

        Capture<Assignment> capt1 = new Capture<Assignment>();
        Capture<Assignment> capt2 = new Capture<Assignment>();
        
        expect(userBusiness.retrieve(1)).andReturn(user1);
        expect(userBusiness.retrieve(2)).andReturn(user2);
        expect(assignmentDAO.create(EasyMock.capture(capt1))).andReturn(new Integer(111));
        expect(assignmentDAO.get(111)).andReturn(new Assignment());
        expect(assignmentDAO.create(EasyMock.capture(capt2))).andReturn(new Integer(112));
        expect(assignmentDAO.get(112)).andReturn(new Assignment());

        replayAll();
        
        Set<Assignment> actual = testable.addMultiple(iter, new HashSet<Integer>(Arrays.asList(1, 2)),
                personalLoad, 100);
        
        assertEquals(3, actual.size());
        
        assertEquals(iter, capt1.getValue().getBacklog());
        assertEquals(iter, capt2.getValue().getBacklog());
        
        Set<User> users = new HashSet<User>(Arrays.asList(capt1.getValue().getUser(), capt2.getValue().getUser()));
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user2));
        
        assertEquals((short)100, capt1.getValue().getAvailability());
        assertEquals((short)100, capt2.getValue().getAvailability());
        
        assertEquals(personalLoad, capt1.getValue().getPersonalLoad());
        assertEquals(personalLoad, capt2.getValue().getPersonalLoad());

        verifyAll();
    }
}
