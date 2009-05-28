package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fi.hut.soberit.agilefant.business.impl.UserBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.TodoDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.Assignment;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.SpringTestCase;
import fi.hut.soberit.agilefant.util.TestUtility;

/**
 * JUnit integration test case for testing userBusiness.
 * 
 * @author rjokelai
 * 
 */
public class UserBusinessTest extends SpringTestCase {

    private TestUtility testUtility;

    private UserDAO userDAO;

    private ProductDAO productDAO;

    private BacklogItemDAO backlogItemDAO;

    private TodoDAO todoDAO;

    private UserBusiness userBusiness;

    public TestUtility getTestUtility() {
        return testUtility;
    }

    public void setTestUtility(TestUtility testUtility) {
        this.testUtility = testUtility;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public TodoDAO getTodoDAO() {
        return todoDAO;
    }

    public void setTodoDAO(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    public UserBusiness getUserBusiness() {
        return userBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    /**
     * A method for testing the getBacklogItemsInProgress method of class
     * UserBusiness.
     */
    public void testGetBacklogItemsInProgressForUser() {
        super.setComplete();

        testUtility.setCleanup(true);
        User user = userDAO.get(testUtility.createUser("test0", "test"));
        SecurityUtil.setLoggedUser(user);
        
        // Create the responsible lists
        Collection<User> respReal = new ArrayList<User>();
        
        respReal.add(user);

        // Create context for backlog items
        int productId = testUtility.createProduct("Test product");
        Product product = productDAO.get(productId);

        // Create the backlog items
        int bli1Id = testUtility.createBacklogItem(
                "Backlog item with not started state", product);
        int bli2Id = testUtility.createBacklogItem(
                "Backlog item with started state", product);
        int bli3Id = testUtility.createBacklogItem(
                "Backlog item with blocked state and responsible", product);
        int bli4Id = testUtility.createBacklogItem(
                "Backlog item with implemented state and responsible", product);
        int bli5Id = testUtility.createBacklogItem(
                "Backlog item with done state and responsible", product);

        BacklogItem bli1 = backlogItemDAO.get(bli1Id);
        BacklogItem bli2 = backlogItemDAO.get(bli2Id);
        BacklogItem bli3 = backlogItemDAO.get(bli3Id);
        BacklogItem bli4 = backlogItemDAO.get(bli4Id);
        BacklogItem bli5 = backlogItemDAO.get(bli5Id);

        bli1.setState(State.NOT_STARTED);
        bli2.setState(State.STARTED);
        bli3.setState(State.BLOCKED);
        bli4.setState(State.IMPLEMENTED);
        bli5.setState(State.DONE);
        
        bli1.setPriority(Priority.BLOCKER);
        bli2.setPriority(Priority.BLOCKER);
        bli3.setPriority(Priority.BLOCKER);
        bli4.setPriority(Priority.BLOCKER);
        bli5.setPriority(Priority.BLOCKER);

        
        // Set the responsibles
        bli3.setResponsibles(respReal);
        bli4.setResponsibles(respReal);
        bli5.setResponsibles(respReal);
        
        // Set the user's backlog items
        Collection<BacklogItem> userBLIs = new ArrayList<BacklogItem>();
        userBLIs.add(bli3);
        userBLIs.add(bli4);
        userBLIs.add(bli5);
        user.setBacklogItems(userBLIs);

        super.endTransaction();
        super.startNewTransaction();
        super.setComplete();

        // Refetch the test backlog items from the mysql database
        bli1 = backlogItemDAO.get(bli1Id);
        bli2 = backlogItemDAO.get(bli2Id);
        bli3 = backlogItemDAO.get(bli3Id);
        bli4 = backlogItemDAO.get(bli4Id);
        bli5 = backlogItemDAO.get(bli5Id);
        
        Collection<BacklogItem> bliList = user.getBacklogItems();
        assertFalse("Failed: list is empty", bliList.isEmpty());

        for (BacklogItem bli : bliList) {
            if (bli.getId() != bli3Id &&
                    bli.getId() != bli4Id &&
                    bli.getId() != bli5Id) {
                fail("List contains backlog items with wrong id's");
            }
            
            assertTrue(
                    "Failed: the list should not contain BLIs with wrong responsibles",
                    bli.getResponsibles().contains(user));
        }
        
        List<BacklogItem> list = userBusiness.getBacklogItemsInProgress(user);
        assertFalse("Failed: list is empty", list.isEmpty());

        for (BacklogItem bli : list) {
            if (bli.getId() != bli3Id &&
                    bli.getId() != bli4Id) {
                fail("List contains backlog items with wrong id's");
            }
            
            assertFalse(
                    "Failed: the list should not contain backlog items with not started state",
                    bli.getState() == State.NOT_STARTED);
            assertFalse(
                    "Failed: the list should not contain backlog items with done state",
                    bli.getState() == State.DONE);
            assertTrue(
                    "Failed: the list should not contain backlog items with wrong responsible",
                    bli.getResponsibles().contains(user));
        }
    }

    /**
     * Clears the database from test data. We must clear database manually if we
     * need to transactions to complete.
     */
    protected void onTearDownInTransaction() throws Exception {
        testUtility.clearDBStack();
    }
    
    public void testhasUserCreatedItems() {
        
        User user = userDAO.get(testUtility.createUser("test0", "test"));
        SecurityUtil.setLoggedUser(user);
 
        assertFalse(userBusiness.hasUserCreatedItems(user));
        
        // Create the backlog items
        int productId = testUtility.createProduct("Test product");
        Product product = productDAO.get(productId);

        int bli1Id = testUtility.createBacklogItem(
                "Backlog item with not started state", product);
        // int bli2Id = testUtility.createBacklogItem(
        //        "Backlog item with started state", product);
        BacklogItem bli1 = backlogItemDAO.get(bli1Id);
        bli1.setCreator(user);
        
        assertTrue(userBusiness.hasUserCreatedItems(user));    
    }   
    
    private Assignment createAssUtil(Date start, Date end, Backlog bl) {
        Assignment ass = new Assignment();
        ass.setBacklog(bl);
        ass.getBacklog().setEndDate(end);
        if(ass.getBacklog() instanceof Iteration) {
            ((Iteration)bl).setStartDate(start);
        }
        if(ass.getBacklog() instanceof Project) {
            ((Project)bl).setStartDate(start);
        }
        return ass;
    }
    
    public void testgetOngoingBacklogsByUserAndInterval() {
        User user = new User();
        List<Assignment> asses = new ArrayList<Assignment>();
        user.setAssignments(asses);
        asses.add(createAssUtil(new Date(100), new Date(200), new Project()));
        asses.add(createAssUtil(new Date(100), new Date(300), new Project()));
        asses.add(createAssUtil(new Date(100), new Date(400), new Project()));
        asses.add(createAssUtil(new Date(300), new Date(500), new Project()));
        asses.add(createAssUtil(new Date(900), new Date(1000), new Project()));
        asses.add(createAssUtil(null, null, new Project()));
        asses.add(createAssUtil(new Date(1), null, new Project()));
        asses.add(createAssUtil(null, new Date(1), new Project()));
        
        UserBusiness testable = new UserBusinessImpl();
        
        assertEquals(8,testable.getOngoingBacklogsByUserAndInterval(user, null, null).size());
        assertEquals(5,testable.getOngoingBacklogsByUserAndInterval(user, new Date(0), null).size());
        assertEquals(5,testable.getOngoingBacklogsByUserAndInterval(user, null, new Date(1001)).size());
        assertEquals(4,testable.getOngoingBacklogsByUserAndInterval(user, new Date(10), new Date(410)).size());
        assertEquals(4,testable.getOngoingBacklogsByUserAndInterval(user, new Date(99), new Date(501)).size());
        assertEquals(1,testable.getOngoingBacklogsByUserAndInterval(user, new Date(300), new Date(300)).size());
        assertEquals(0,testable.getOngoingBacklogsByUserAndInterval(user, new Date(400), new Date(300)).size());
    }
}