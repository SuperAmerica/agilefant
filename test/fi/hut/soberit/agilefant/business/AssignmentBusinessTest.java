package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.AssignmentBusinessImpl;
import fi.hut.soberit.agilefant.db.AssignmentDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
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
    public void testStore() {
        ExactEstimate personalLoad = new ExactEstimate(100L);
        expect(assignmentDAO.get(10)).andReturn(assignment);
        assignmentDAO.store(assignment);
        replayAll();
        testable.store(10, personalLoad, (short) 100);
        assertEquals((short) 100, assignment.getAvailability());
        assertEquals(personalLoad, assignment.getPersonalLoad());
        verifyAll();
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testStore_notFound() {
        ExactEstimate personalLoad = new ExactEstimate(100L);
        expect(assignmentDAO.get(10)).andThrow(new ObjectNotFoundException());
        replayAll();
        testable.store(10, personalLoad, (short) 100);
        verifyAll();
    }

    @Test
    public void testAddMultiple() {
        User user1 = new User();
        User user2 = new User();
        Iteration iter = new Iteration();
        ExactEstimate personalLoad = new ExactEstimate(100L);

        Capture<Assignment> capt1 = new Capture<Assignment>();
        Capture<Assignment> capt2 = new Capture<Assignment>();
        
        expect(userBusiness.retrieve(1)).andReturn(user1);
        expect(userBusiness.retrieve(2)).andReturn(user2);
        expect(assignmentDAO.create(EasyMock.capture(capt1))).andReturn(new Integer(111));
        expect(assignmentDAO.get(111)).andReturn(null);
        expect(assignmentDAO.create(EasyMock.capture(capt2))).andReturn(new Integer(112));
        expect(assignmentDAO.get(112)).andReturn(null);

        replayAll();
        
        Collection<Assignment> actual = testable.addMultiple(iter, new HashSet<Integer>(Arrays.asList(1, 2)),
                personalLoad, (short) 100);
        
        assertEquals(2, actual.size());
        
        assertEquals(iter, capt1.getValue().getBacklog());
        assertEquals(iter, capt2.getValue().getBacklog());
        
        assertEquals(user1, capt1.getValue().getUser());
        assertEquals(user2, capt2.getValue().getUser());
        
        assertEquals((short)100, capt1.getValue().getAvailability());
        assertEquals((short)100, capt2.getValue().getAvailability());
        
        assertEquals(personalLoad, capt1.getValue().getPersonalLoad());
        assertEquals(personalLoad, capt2.getValue().getPersonalLoad());

        verifyAll();
    }
}
