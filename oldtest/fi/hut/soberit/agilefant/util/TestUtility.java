package fi.hut.soberit.agilefant.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Stack;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.GenericDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.TodoDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Todo;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.web.BacklogItemAction;
import fi.hut.soberit.agilefant.web.TodoAction;

/**
 * Utility class for testing
 * 
 */
public class TestUtility extends SpringTestCase {
    //private static Log logger = LogFactory.getLog(TestUtility.class);

    private UserDAO userDAO;

    private ProductDAO productDAO;

    private BacklogItemDAO backlogItemDAO;

    private TodoDAO todoDAO;

    private ProjectDAO projectDAO;

    private IterationDAO iterationDAO;

    private boolean cleanup = false;

    public static final long MINUTE = 1000 * 60;

    public static final long HOUR = MINUTE * 60;

    public static enum TestUser {
        USER1, USER2
    }
    
    /**
     * Mock-up method.
     * Created so, that tests show green, when all tests pass.
     */
    public void testVoid() {
        assertTrue(true);
    }

    @SuppressWarnings("unchecked")
    private Stack<GenericDAO> cleanupStack = new Stack<GenericDAO>();

    /**
     * Create a new user for testing.
     * 
     * @param loginName
     *                The login name for the user
     * @param passwd
     *                The password of the user
     * @return The id of the generated user
     */
    public Integer createUser(String loginName, String passwd) {
        Integer id;
        User user = new User();
        user.setLoginName(loginName);
        user.setPassword(SecurityUtil.MD5(passwd));
        id = (Integer) userDAO.create(user);
        pushToCleanupstack(userDAO);
        return id;
    }

    /**
     * Create a new product for testing.
     * 
     * @param productName
     *                The name for the test product
     * @return The id of the generated product
     */
    public Integer createProduct(String name) {
        Integer id;
        Product product = new Product();
        product.setName(name);
        id = (Integer) productDAO.create(product);
        pushToCleanupstack(productDAO);
        return id;
    }

    /**
     * Create a new project for testing.
     * 
     * @param name
     *                The name for the test project
     * @param startDate
     *                The start date of the test project
     * @param endDate
     *                The end date of the test project
     * @param product
     *                The product of the test project
     * @return The id of the generated project
     */
    public Integer createProject(String name, Date startDate, Date endDate,
            Product product) {
        Integer id;
        Project project = new Project();
        project.setName(name);
        project.setProduct(product);
        project.setStartDate(startDate);
        project.setEndDate(endDate);
        id = (Integer) projectDAO.create(project);
        pushToCleanupstack(projectDAO);
        return id;
    }

    /**
     * Create a new project for testing. Start date is epoch, end date is now *
     * 2. Generic product is also created.
     * 
     * @param name
     *                The name for the test project
     * @return The id of the generated project
     */
    public Integer createProject(String name) {
        Product product = productDAO.get(createProduct(name));
        return createProject(name, new Date(0), new Date(System
                .currentTimeMillis() * 2), product);
    }

    /**
     * Crate a new iteration for testing.
     * 
     * @param name
     *                the name for the test iteration
     * @param startDate
     *                the start date for the iteration
     * @param endDate
     *                the end date for the iteration
     * @param project
     *                the project the iteration belongs to
     * @return the id of the generated iteration
     */
    public Integer createIteration(String name, Date startDate, Date endDate,
            Project project) {
        Integer id;
        Iteration iteration = new Iteration();
        iteration.setName(name);
        iteration.setProject(project);
        iteration.setStartDate(startDate);
        iteration.setEndDate(endDate);
        id = (Integer) iterationDAO.create(iteration);
        pushToCleanupstack(iterationDAO);
        return id;
    }

    /**
     * Create a new iteration for testing. Start date is epoch, end date is 2 *
     * now. Generic project is also created.
     * 
     * @param name
     *                the name for the test iteration
     * @return the id of the generated iteration
     */
    public Integer createIteration(String name) {
        Project project = projectDAO.get(createProject(name));
        return createIteration(name, new Date(0), new Date(System
                .currentTimeMillis() * 2), project);
    }

    /**
     * Create a backlog item.
     * 
     * @param name
     *                the name for the backlog item
     * @param backlog
     *                the backlog for the backlog item
     * @return the id of the generated backlog item
     */
    public Integer createBacklogItem(String name, Backlog backlog) {
        Integer id;
        BacklogItem backlogItem = new BacklogItem();
        backlogItem.setName(name);
        backlogItem.setBacklog(backlog);
        id = (Integer) backlogItemDAO.create(backlogItem);
        pushToCleanupstack(backlogItemDAO);
        return id;
    }

    /**
     * Create a backlog item. Creates also a generic backlog for the item.
     * 
     * @param name
     *                the name for the backlog item
     * @param backlog
     *                the backlog for the backlog item
     * @return the id of the generated backlog item
     */
    public Integer createBacklogItem(String name) {
        Product product = productDAO.get(createProduct(name));
        return createBacklogItem(name, product);
    }

    /**
     * Create a todo. Doens't create todo event history items.
     * 
     * @param name
     *                the name for the todo
     * @param backlogItem
     *                the backlog item the todo belongs to
     * @return the id of the generated todo
     */
    public Integer createTodo(User creator, String name, BacklogItem backlogItem) {
        Integer id;
        Todo todo = new Todo();
        todo.setName(name);
        todo.setBacklogItem(backlogItem);
        todo.setCreator(creator);
        id = (Integer) todoDAO.create(todo);
        pushToCleanupstack(todoDAO);
        return id;
    }

    /**
     * Create a todo. Create a generic backlog item and backlog for the todo.
     * 
     * @param name
     *                the name for the todo
     * @return the id of the generated todo
     */
    public Integer createTodo(User creator, String name) {
        BacklogItem backlogItem = backlogItemDAO.get(createBacklogItem(name));
        return createTodo(creator, name, backlogItem);
    }


    /**
     * Clean the DB from all content of DAOs in cleanupSet.
     */
    public void clearDBStack() {
        if (cleanup) {
            while (!cleanupStack.empty()) {
                clearDB(cleanupStack.pop());
            }
        }
    }

    /**
     * Createss a product for testing.
     * 
     * @param number
     *                number for identifying product from name
     * @param productDAO
     *                data access object for product
     */
    public static void createTestProduct(int number, ProductDAO productDAO) {
        Product product = new Product();

        product.setDescription("Product backlog for testing");
        product.setName("Product test backlog " + number);
        productDAO.store(product);
    }

    /**
     * Createss a iteration for testing.
     * 
     * @param number
     *                number for identifying iteration from name
     * @param iterationDAO
     *                data access object for iteration
     */
    public static int createTestIteration(int number, IterationDAO iterationDAO) {
        Iteration iteration = new Iteration();
        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(GregorianCalendar.MONTH, 1);

        iteration.setDescription("Iteration backlog for testing");
        iteration.setName("Iteration test backlog " + number);
        iteration.setStartDate(new GregorianCalendar().getTime());
        iteration.setEndDate(endDate.getTime());
        return (Integer) iterationDAO.create(iteration);
    }

    /**
     * Create test backlog item without using action.
     * 
     * @param number
     *                identifier for backlog item name
     * @param backlog
     *                backlog used
     * @param backlogItemDAO
     *                data access object for backlogItem
     */
    public static void createBareTestItem(int number, Backlog backlog,
            BacklogItemDAO backlogItemDAO) {
        BacklogItem backlogItem = new BacklogItem();

        backlogItem.setBacklog(backlog);
        backlogItem.setDescription("Backlog item for testing");
        backlogItem.setName("Test backlog " + number);
        backlogItemDAO.store(backlogItem);
    }

    /**
     * Creates a test backlog item using action
     * 
     * @param number
     *                identifier for backlog item name
     * @param backlog
     *                backlog used
     * @param backlogItemDAO
     *                data access object for backlogItem
     */
    public static String createTestItem(Backlog backlog,
            BacklogItemAction backlogItemAction) {
        return createTestItem(backlog, backlogItemAction, 0);
    }

    /**
     * Creates a test backlog item using action
     * 
     * @param number
     *                identifier for backlog item name
     * @param backlog
     *                backlog used
     * @param backlogItemAction
     *                action used in creation
     * @return result of the storing action
     */
    public static String createTestItem(Backlog backlog,
            BacklogItemAction backlogItemAction, long originalEstimate) {
        backlogItemAction.create();
        backlogItemAction.setBacklog(backlog);
        backlogItemAction.setBacklogId(backlog.getId());
        backlogItemAction.setBacklogItemName("Test item");
        backlogItemAction.getBacklogItem().setOriginalEstimate(
                new AFTime(originalEstimate));
        return backlogItemAction.store();
    }

    /**
     * Creates a test todo using action
     * 
     * @param backlogItem
     *                the backlog item this todo belongs to
     * @param backlogItemAction
     *                the action used in creation
     * @param estimate
     *                the estimate for the todo
     * @return the ID of the todo created
     */
    public static int createTestTodo(BacklogItem backlogItem,
            TodoAction todoAction, long estimate) {
        todoAction.create();
        todoAction.setBacklogItemId(backlogItem.getId());
        todoAction.getTodo().setName("Test todo");
        return todoAction.storeNew();
    }


    
    /**
     * Clears the database from test data. We must clear database by hand
     * because of we need transactions to complete.
     * 
     * @param userDAO
     *                data access object for user
     * @param backlogItem
     *                DAO data access object for backlog item
     * @param productDAO
     *                data access object for product
     */
    public static void clearData(UserDAO userDAO,
            BacklogItemDAO backlogItemDAO, ProductDAO productDAO)
            throws Exception {

        for (User i : userDAO.getAll()) {
            userDAO.remove(i.getId());
        }

        for (BacklogItem i : backlogItemDAO.getAll()) {
            backlogItemDAO.remove(i.getId());
        }

        clearData(productDAO);
    }

//    public static void clearData(ProductDAO productDAO,
//            EffortHistoryDAO effortHistoryDAO) {
//        clearData(effortHistoryDAO);
//        clearData(productDAO);
//    }

    public static void clearData(ProductDAO productDAO) {
        for (Product i : productDAO.getAll()) {
            productDAO.remove(i.getId());
        }
    }

//    public static void clearData(EffortHistoryDAO effortHistoryDAO) {
//        for (EffortHistory i : effortHistoryDAO.getAll()) {
//            effortHistoryDAO.remove(i.getId());
//        }
//    }

    public static void clearData(IterationDAO iterationDAO) {
        for (Iteration i : iterationDAO.getAll()) {
            iterationDAO.remove(i.getId());
        }
    }

    public static void clearData(TodoDAO todoDAO) {
        for (Todo i : todoDAO.getAll()) {
            todoDAO.remove(i.getId());
        }
    }

    @SuppressWarnings("unchecked")
    public static void clearDB(GenericDAO genericDAO) {
        for (Object i : genericDAO.getAll()) {
            genericDAO.remove(i);
        }
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * @return the cleanup
     */
    public boolean isCleanup() {
        return cleanup;
    }

    /**
     * @param cleanup
     *                the cleanup to set
     */
    public void setCleanup(boolean cleanup) {
        this.cleanup = cleanup;
    }

    /**
     * @return the productDAO
     */
    public ProductDAO getProductDAO() {
        return productDAO;
    }

    /**
     * @param productDAO
     *                the productDAO to set
     */
    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    /**
     * @return the backlogItemDAO
     */
    public BacklogItemDAO getBacklogItemDAO() {
        return backlogItemDAO;
    }

    /**
     * @param backlogItemDAO
     *                the backlogItemDAO to set
     */
    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    /**
     * @return the todoDAO
     */
    public TodoDAO getTodoDAO() {
        return todoDAO;
    }

    /**
     * @param todoDAO
     *                the todoDAO to set
     */
    public void setTodoDAO(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    /**
     * @return the projectDAO
     */
    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    /**
     * @param projectDAO
     *                the projectDAO to set
     */
    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    /**
     * @return the iterationDAO
     */
    public IterationDAO getIterationDAO() {
        return iterationDAO;
    }

    /**
     * @param iterationDAO
     *                the iterationDAO to set
     */
    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    /**
     * Push the DAO to cleanup stack if cleanup is enabled and it is not yet in
     * the stack.
     * 
     * @param genericDAO
     *                the DAO to push to the stack
     */
    @SuppressWarnings("unchecked")
    private void pushToCleanupstack(GenericDAO genericDAO) {
        if (cleanup && !cleanupStack.contains(genericDAO)) {
            cleanupStack.push(genericDAO);
        }
    }
}
