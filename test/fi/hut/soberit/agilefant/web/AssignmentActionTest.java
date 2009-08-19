package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import fi.hut.soberit.agilefant.business.AssignmentBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.SignedExactEstimate;

public class AssignmentActionTest {

    private AssignmentAction testable;
    private Assignment assignment;
    private AssignmentBusiness assignmentBusiness;

    @Before
    public void setUp() {
        testable = new AssignmentAction();
        assignmentBusiness = createStrictMock(AssignmentBusiness.class);
        testable.setAssignmentBusiness(assignmentBusiness);
        assignment = new Assignment();
        assignment.setPersonalLoad(null);
        assignment.setAvailability((short) 0);
        testable.setAssignment(assignment);
    }

    @Test
    public void testModifyAssignment() {
        assignment.setPersonalLoad(new SignedExactEstimate(3400));
        assignment.setAvailability((short) 400);
        expect(
                assignmentBusiness.store(313, assignment.getPersonalLoad(),
                        (short) 400)).andReturn(assignment);
        replay(assignmentBusiness);
        testable.setAssignmentId(313);
        testable.modify();
        verify(assignmentBusiness);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testModifyAssignment_nonExisting() {
        expect(assignmentBusiness.store(313, null, (short) 0)).andThrow(
                new ObjectNotFoundException());
        replay(assignmentBusiness);
        testable.setAssignmentId(313);
        testable.modify();
        verify(assignmentBusiness);
    }

    @Test
    public void testDelete() {
        assignmentBusiness.delete(313);
        replay(assignmentBusiness);
        testable.setAssignmentId(313);
        testable.delete();
        verify(assignmentBusiness);
    }

    @Test(expected = ObjectNotFoundException.class)
    public void testDelete_notFound() {
        assignmentBusiness.delete(313);
        expectLastCall().andThrow(new ObjectNotFoundException());
        replay(assignmentBusiness);
        testable.setAssignmentId(313);
        testable.delete();
        verify(assignmentBusiness);
    }
    
    @Test
    public void testInitializePrefetchedData() {
        expect(assignmentBusiness.retrieve(123)).andReturn(assignment);
        replay(assignmentBusiness);
        testable.initializePrefetchedData(123);
        verify(assignmentBusiness);
        assertEquals(assignment, testable.getAssignment());
    }
}
