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
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;

public class TaskBusinessTest extends TestCase {

    private TaskBusinessImpl taskBusiness = new TaskBusinessImpl();
    private TaskDAO taskDao;
    private Task task1;
    private Task task2;
    private Task task3;
    BacklogItem backlogItem;

    /**
     * Method for testing the most basic functionality of the tested method.
     * 
     */
    public void testUpdateMultipleTaskStates_basic() {
        taskDao = createMock(TaskDAO.class);
        taskBusiness.setTaskDAO(taskDao);

        BacklogItem bli = new BacklogItem();
        
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
            taskBusiness.updateMultipleTasks(bli, newTaskStates, new HashMap<Integer, String>());
        } catch (ObjectNotFoundException e) {
            fail("Unexpected ObjectNotFoundException.");
        }

        // Check that States were changed
        assertEquals(State.BLOCKED, task1.getState());
        assertEquals(State.DONE, task2.getState());

        // Verify that methods were called on taskDao.
        verify(taskDao);
    }

    /**
     * Tests ProjectBusiness class's methods moveUp, moveDown, moveToTop and
     * moveToBottom.
     */

    private void prepareRankTests() {
        taskDao = createMock(TaskDAO.class);
        taskBusiness.setTaskDAO(taskDao);

        backlogItem = new BacklogItem();
        backlogItem.setId(100);
        task1 = new Task();
        task1.setId(1);
        task1.setRank(1);
        task1.setBacklogItem(backlogItem);
        task2 = new Task();
        task2.setId(2);
        task2.setRank(2);
        task2.setBacklogItem(backlogItem);
        task3 = new Task();
        task3.setId(3);
        task3.setRank(3);
        task3.setBacklogItem(backlogItem);
    }

    public void testMoveUp_basic() {
        prepareRankTests();
        expect(taskDao.get(3)).andReturn(task3);
        expect(taskDao.findUpperRankedTask(task3)).andReturn(task2);
        replay(taskDao);
        try {
            taskBusiness.rankTaskUp(3);
            verify(taskDao);
            // Check that task3 gets smaller rank than task2 after moving it up.
            assertTrue((task2.getRank() > task3.getRank()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveUp_alreadyOnTop() {
        prepareRankTests();
        expect(taskDao.get(1)).andReturn(task1);
        expect(taskDao.findUpperRankedTask(task1)).andReturn(null);
        replay(taskDao);
        try {
            taskBusiness.rankTaskUp(1);
            verify(taskDao);
            // Check that order was not changed
            assertEquals(new Integer(1), task1.getRank());
            assertEquals(new Integer(2), task2.getRank());
            assertEquals(new Integer(3), task3.getRank());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveDown_basic() {
        prepareRankTests();
        expect(taskDao.get(1)).andReturn(task1);
        expect(taskDao.findLowerRankedTask(task1)).andReturn(task2);
        replay(taskDao);
        try {
            taskBusiness.rankTaskDown(1);
            verify(taskDao);
            // Check that task1 gets higher rank value than task2 after moving
            // it down.
            assertTrue((task2.getRank() < task1.getRank()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveDown_alreadyAtBottom() {
        prepareRankTests();
        expect(taskDao.get(3)).andReturn(task3);
        expect(taskDao.findLowerRankedTask(task3)).andReturn(null);
        replay(taskDao);
        try {
            taskBusiness.rankTaskDown(3);
            verify(taskDao);
            // Check that order was not changed
            assertEquals(new Integer(1), task1.getRank());
            assertEquals(new Integer(2), task2.getRank());
            assertEquals(new Integer(3), task3.getRank());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveTop_basic() {
        prepareRankTests();
        expect(taskDao.get(1)).andReturn(task1);
        expect(taskDao.findLowerRankedTask(task1)).andReturn(task2);
        replay(taskDao);
        try {
            taskBusiness.rankTaskDown(1);
            verify(taskDao);
            // Check that task1 gets higher rank value than task2 after moving
            // it down.
            assertTrue((task2.getRank() < task1.getRank()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveTop_alreadyOnTop() {
        prepareRankTests();
        expect(taskDao.get(3)).andReturn(task3);
        expect(taskDao.findLowerRankedTask(task3)).andReturn(null);
        replay(taskDao);
        try {
            taskBusiness.rankTaskDown(3);
            verify(taskDao);
            // Check that order was not changed
            assertEquals(new Integer(1), task1.getRank());
            assertEquals(new Integer(2), task2.getRank());
            assertEquals(new Integer(3), task3.getRank());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveBottom_basic() {
        prepareRankTests();
        expect(taskDao.get(1)).andReturn(task1);
        expect(taskDao.getLowestRankedTask(backlogItem)).andReturn(task3);
        replay(taskDao);
        try {
            taskBusiness.rankTaskBottom(1);
            verify(taskDao);

            assertTrue((task2.getRank() < task3.getRank()));
            assertTrue((task3.getRank() < task1.getRank()));

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveBottom_alreadyAtBottom() {
        prepareRankTests();
        expect(taskDao.get(3)).andReturn(task3);
        expect(taskDao.getLowestRankedTask(backlogItem)).andReturn(task3);
        replay(taskDao);
        try {
            taskBusiness.rankTaskBottom(3);
            verify(taskDao);
            // Check that order was not changed
            assertEquals(new Integer(1), task1.getRank());
            assertEquals(new Integer(2), task2.getRank());
            assertEquals(new Integer(3), task3.getRank());

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
