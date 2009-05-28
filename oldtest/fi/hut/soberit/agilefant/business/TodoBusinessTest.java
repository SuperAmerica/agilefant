package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.TodoBusinessImpl;
import fi.hut.soberit.agilefant.db.TodoDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Todo;

public class TodoBusinessTest extends TestCase {

    private TodoBusinessImpl todoBusiness = new TodoBusinessImpl();
    private TodoDAO todoDao;
    private Todo todo1;
    private Todo todo2;
    private Todo todo3;
    BacklogItem backlogItem;

    /**
     * Method for testing the most basic functionality of the tested method.
     * 
     */
    public void testUpdateMultipleTodoStates_basic() {
        todoDao = createMock(TodoDAO.class);
        todoBusiness.setTodoDAO(todoDao);

        BacklogItem bli = new BacklogItem();
        
        // Create test todos
        Todo todo1 = new Todo();
        todo1.setId(1);
        todo1.setState(State.NOT_STARTED);
        Todo todo2 = new Todo();
        todo1.setState(State.NOT_STARTED);

        // Create new states map
        Map<Integer, State> newTodoStates = new HashMap<Integer, State>();
        newTodoStates.put(1, State.BLOCKED);
        newTodoStates.put(2, State.DONE);

        // Prepare mocks
        expect(todoDao.get(1)).andReturn(todo1);
        expect(todoDao.get(2)).andReturn(todo2);
        replay(todoDao);

        // Run the test case
        try {
            todoBusiness.updateMultipleTodos(bli, newTodoStates, new HashMap<Integer, String>());
        } catch (ObjectNotFoundException e) {
            fail("Unexpected ObjectNotFoundException.");
        }

        // Check that States were changed
        assertEquals(State.BLOCKED, todo1.getState());
        assertEquals(State.DONE, todo2.getState());

        // Verify that methods were called on todoDao.
        verify(todoDao);
    }

    /**
     * Tests ProjectBusiness class's methods moveUp, moveDown, moveToTop and
     * moveToBottom.
     */

    private void prepareRankTests() {
        todoDao = createMock(TodoDAO.class);
        todoBusiness.setTodoDAO(todoDao);

        backlogItem = new BacklogItem();
        backlogItem.setId(100);
        todo1 = new Todo();
        todo1.setId(1);
        todo1.setRank(1);
        todo1.setBacklogItem(backlogItem);
        todo2 = new Todo();
        todo2.setId(2);
        todo2.setRank(2);
        todo2.setBacklogItem(backlogItem);
        todo3 = new Todo();
        todo3.setId(3);
        todo3.setRank(3);
        todo3.setBacklogItem(backlogItem);
    }

    public void testMoveUp_basic() {
        prepareRankTests();
        expect(todoDao.get(3)).andReturn(todo3);
        expect(todoDao.findUpperRankedTodo(todo3)).andReturn(todo2);
        replay(todoDao);
        try {
            todoBusiness.rankTodoUp(3);
            verify(todoDao);
            // Check that todo3 gets smaller rank than todo2 after moving it up.
            assertTrue((todo2.getRank() > todo3.getRank()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveUp_alreadyOnTop() {
        prepareRankTests();
        expect(todoDao.get(1)).andReturn(todo1);
        expect(todoDao.findUpperRankedTodo(todo1)).andReturn(null);
        replay(todoDao);
        try {
            todoBusiness.rankTodoUp(1);
            verify(todoDao);
            // Check that order was not changed
            assertEquals(new Integer(1), todo1.getRank());
            assertEquals(new Integer(2), todo2.getRank());
            assertEquals(new Integer(3), todo3.getRank());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveDown_basic() {
        prepareRankTests();
        expect(todoDao.get(1)).andReturn(todo1);
        expect(todoDao.findLowerRankedTodo(todo1)).andReturn(todo2);
        replay(todoDao);
        try {
            todoBusiness.rankTodoDown(1);
            verify(todoDao);
            // Check that todo1 gets higher rank value than todo2 after moving
            // it down.
            assertTrue((todo2.getRank() < todo1.getRank()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveDown_alreadyAtBottom() {
        prepareRankTests();
        expect(todoDao.get(3)).andReturn(todo3);
        expect(todoDao.findLowerRankedTodo(todo3)).andReturn(null);
        replay(todoDao);
        try {
            todoBusiness.rankTodoDown(3);
            verify(todoDao);
            // Check that order was not changed
            assertEquals(new Integer(1), todo1.getRank());
            assertEquals(new Integer(2), todo2.getRank());
            assertEquals(new Integer(3), todo3.getRank());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveTop_basic() {
        prepareRankTests();
        expect(todoDao.get(1)).andReturn(todo1);
        expect(todoDao.findLowerRankedTodo(todo1)).andReturn(todo2);
        replay(todoDao);
        try {
            todoBusiness.rankTodoDown(1);
            verify(todoDao);
            // Check that todo1 gets higher rank value than todo2 after moving
            // it down.
            assertTrue((todo2.getRank() < todo1.getRank()));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveTop_alreadyOnTop() {
        prepareRankTests();
        expect(todoDao.get(3)).andReturn(todo3);
        expect(todoDao.findLowerRankedTodo(todo3)).andReturn(null);
        replay(todoDao);
        try {
            todoBusiness.rankTodoDown(3);
            verify(todoDao);
            // Check that order was not changed
            assertEquals(new Integer(1), todo1.getRank());
            assertEquals(new Integer(2), todo2.getRank());
            assertEquals(new Integer(3), todo3.getRank());
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveBottom_basic() {
        prepareRankTests();
        expect(todoDao.get(1)).andReturn(todo1);
        expect(todoDao.getLowestRankedTodo(backlogItem)).andReturn(todo3);
        replay(todoDao);
        try {
            todoBusiness.rankTodoBottom(1);
            verify(todoDao);

            assertTrue((todo2.getRank() < todo3.getRank()));
            assertTrue((todo3.getRank() < todo1.getRank()));

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    public void testMoveBottom_alreadyAtBottom() {
        prepareRankTests();
        expect(todoDao.get(3)).andReturn(todo3);
        expect(todoDao.getLowestRankedTodo(backlogItem)).andReturn(todo3);
        replay(todoDao);
        try {
            todoBusiness.rankTodoBottom(3);
            verify(todoDao);
            // Check that order was not changed
            assertEquals(new Integer(1), todo1.getRank());
            assertEquals(new Integer(2), todo2.getRank());
            assertEquals(new Integer(3), todo3.getRank());

        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}
