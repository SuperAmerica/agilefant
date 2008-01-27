package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.TaskBusinessImpl;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;

public class TaskBusinessTest extends TestCase {

    private TaskBusinessImpl taskBusiness = new TaskBusinessImpl();
    private TaskDAO taskDao;

    /**
     * Method for testing the most basic functionality of the tested method.
     * 
     */
    public void testUpdateMultipleTaskStates_basic() {
        taskDao = createMock(TaskDAO.class);
        taskBusiness.setTaskDAO(taskDao);

        // Create test tasks
        Task task1 = new Task();
        task1.setId(1);
        task1.setState(State.NOT_STARTED);
        Task task2 = new Task();
        task1.setState(State.NOT_STARTED);

        // Create new states map
        Map<Integer, State> newTaskStates = new HashMap<Integer, State>();
        newTaskStates.put(1, State.BLOCKED);
        newTaskStates.put(2, State.DONE);

        // Prepare mocks
        expect(taskDao.get(1)).andReturn(task1);
        expect(taskDao.get(2)).andReturn(task2);
        replay(taskDao);

        // Run the test case
        try {
            taskBusiness.updateMultipleTaskStates(newTaskStates);
        } catch (ObjectNotFoundException e) {
            fail("Unexpected ObjectNotFoundException.");
        }

        // Check that States were changed
        assertEquals(State.BLOCKED, task1.getState());
        assertEquals(State.DONE, task2.getState());

        // Verify that methods were called on taskDao.
        verify(taskDao);
    }
}
