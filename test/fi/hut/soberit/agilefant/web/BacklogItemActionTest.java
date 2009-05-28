package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.TodoDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;
import fi.hut.soberit.agilefant.model.Priority;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Todo;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.util.SpringTestCase;

/**
 * JUnit integration test for BacklogItemAction.
 * 
 * @author rstrom
 */
public class BacklogItemActionTest extends SpringTestCase {
    // class under test
    private BacklogItemAction action = null;

    private BacklogDAO backlogDAO = null;
    private BacklogItemDAO backlogItemDAO = null;
    private TodoDAO todoDAO = null;
    private IterationGoalDAO iterationGoalDAO = null;
    private UserDAO userDAO = null;
    private ProjectDAO projectDAO = null;
    private ProductDAO productDAO = null;
    private BusinessThemeBusiness businessThemeBusiness = null;
//    private UserBusiness userBusiness = null;

    private int backlogId;
    private int bliId;
    private int todoId;
    private int goalId;
    private int userId;
    private int projectId;
    private int productId;

    /**
     * Create test data.
     */
    public void onSetUpInTransaction() throws Exception {

        //create product and project
        Product product = new Product();
        product.setName("Test Product");
        productId = (Integer) productDAO.create(product);
        product = productDAO.get(productId);

        BusinessTheme theme = new BusinessTheme();
        theme.setActive(false);
        theme.setName("ttest");
        theme = businessThemeBusiness.store(0, productId, theme);
        HashSet<BusinessTheme> themes = new HashSet<BusinessTheme>();
        themes.add(theme);
        product.setBusinessThemes(themes);
        
        Project project = new Project();
        project.setName("Test Project");
        projectId = (Integer) projectDAO.create(project);
        project = projectDAO.get(projectId);
        project.setProduct(product);
        
        Iteration iteration = new Iteration();
        iteration.setProject(project);
        
        // create iteration and bli
        //Backlog backlog = new Iteration();
        Backlog backlog = iteration;
        backlogId = (Integer) backlogDAO.create(backlog);
        backlog = backlogDAO.get(backlogId);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);
        bliId = (Integer) backlogItemDAO.create(bli);
        bli = backlogItemDAO.get(bliId);
        backlog.getBacklogItems().add(bli);

        // set bli fields
        bli.setName("Test Name");
        bli.setDescription("Test Description");
        bli.setPriority(Priority.BLOCKER);
        bli.setOriginalEstimate(new AFTime("2h 15min"));
        bli.setState(State.BLOCKED);
        bli.setEffortLeft(new AFTime("2h 15min"));
        bli.setBusinessThemes(themes);
        backlogItemDAO.store(bli);
        

        // set bli todos
        ArrayList<Todo> todos = new ArrayList<Todo>();
        Todo todo = new Todo();
        todo.setName("Test todo");
        todo.setBacklogItem(bli);
        todoId = (Integer) todoDAO.create(todo);
        todo = todoDAO.get(todoId);
        todos.add(todo);
        bli.setTodos(todos);
        backlogItemDAO.store(bli);

        // set bli goal
        IterationGoal goal = new IterationGoal();
        goal.setName("Test Goal");
        goal.setIteration((Iteration) backlog);
        goalId = (Integer) iterationGoalDAO.create(goal);
        goal = iterationGoalDAO.get(goalId);
        bli.setIterationGoal(goal);
        backlogDAO.store(backlog);
        backlogItemDAO.store(bli);

        // set bli assignee
        User user = new User();
        user.setLoginName("Test User");
        userId = (Integer) userDAO.create(user);
        user = userDAO.get(userId);
        bli.setAssignee(user);
        backlogItemDAO.store(bli);
    }

    /**
     * Test edit operation.
     */
    @SuppressWarnings("unchecked")
    public void testEdit() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());

        assertEquals(bliId, action.getBacklogItem().getId());
        assertEquals("Test Name", action.getBacklogItem().getName());
        assertEquals("Test Description", action.getBacklogItem()
                .getDescription());
        assertEquals(Priority.BLOCKER, action.getBacklogItem().getPriority());
        assertEquals("2h 15min", action.getBacklogItem().getOriginalEstimate()
                .toString());
        assertEquals("Test todo", ((Todo) ((List) action.getBacklogItem()
                .getTodos()).get(0)).getName());
        assertEquals(State.BLOCKED, action.getBacklogItem().getState());
        assertEquals("2h 15min", action.getBacklogItem().getEffortLeft()
                .toString());
        assertEquals("Test Goal", action.getBacklogItem().getIterationGoal()
                .getName());
        assertEquals("Test User", action.getBacklogItem().getAssignee()
                .getLoginName());
        assertEquals(1, action.getBliActiveOrSelectedThemes().size());
    }

    /**
     * Test edit operation with invalid bli id.
     */
    public void testEdit_invalidId() {
        action.setBacklogItemId(-500);
        assertEquals("error", action.edit());
    }

    /**
     * Test store operation.
     * 
     * TODO: store operation with invalid id. operation needs to be refactored
     * first to get rid of fillStorable cludge.
     */
    public void testStore() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // update bli and execute store
        action.getBacklogItem().setName("Updated");
        action.getBacklogItem().setDescription("Updated");
        action.getBacklogItem().setPriority(Priority.CRITICAL);
        action.getBacklogItem().setOriginalEstimate(new AFTime("1h 15min"));
        action.getBacklogItem().setState(State.IMPLEMENTED);
        action.getBacklogItem().setEffortLeft(new AFTime("1h 15min"));
        assertEquals("ajax_success", action.ajaxStoreBacklogItem());

        // check that fields are updated
        assertEquals("Updated", backlogItemDAO.get(bliId).getName());
        assertEquals("Updated", backlogItemDAO.get(bliId).getDescription());
        assertEquals(Priority.CRITICAL, backlogItemDAO.get(bliId).getPriority());
        assertEquals("1h 15min", backlogItemDAO.get(bliId)
                .getOriginalEstimate().toString());
        assertEquals(State.IMPLEMENTED, backlogItemDAO.get(bliId).getState());
        assertEquals("1h 15min", backlogItemDAO.get(bliId).getEffortLeft()
                .toString());
        assertNull(backlogItemDAO.get(bliId).getCreatedDate());
    }

    /**
     * Test store operation with updating todos.
     */
    @SuppressWarnings("unchecked")
    public void testStore_updateTodos() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // update bli todos and execute store
        List<Todo> todos = (List) action.getBacklogItem().getTodos();
        // edit existing todo
        Todo todo = todos.get(0);
        todo.setName("Updated");
        // create new todo
        Todo todo2 = new Todo();
        todo2.setName("Test todo2");
        todo2.setBacklogItem(action.getBacklogItem());
        int todo2Id = (Integer) todoDAO.create(todo2);
        todo2 = todoDAO.get(todo2Id);
        todos.add(todo2);
        // execute store operation
        assertEquals("ajax_success", action.ajaxStoreBacklogItem());

        // check that todos are updated
        assertEquals("Updated", ((Todo) ((List) backlogItemDAO.get(bliId)
                .getTodos()).get(0)).getName());
        assertEquals("Test todo2", ((Todo) ((List) backlogItemDAO.get(
                bliId).getTodos()).get(1)).getName());
    }

    /**
     * Test store operation with updating goal.
     */
    public void testStore_updateGoal() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // update bli goal
        IterationGoal goal2 = new IterationGoal();
        goal2.setName("Test Goal2");
        goal2.setIteration((Iteration) backlogDAO.get(backlogId));
        int goal2Id = (Integer) iterationGoalDAO.create(goal2);
        goal2 = iterationGoalDAO.get(goal2Id);
        action.setIterationGoalId(goal2Id);

        // execute store operation
        assertEquals("ajax_success", action.ajaxStoreBacklogItem());

        // check that goal is updated
        assertEquals("Test Goal2", backlogItemDAO.get(bliId).getIterationGoal()
                .getName());
    }
    /**
     * Test store operation with changing backlog and setting an iteration goal
     * from the new backlog. BUG: 0000025
     */
    public void testStore_updateGoalAndChangeBacklog() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // update bli goal
        Iteration iter2 = new Iteration();
        int iter2Id = (Integer)backlogDAO.create(iter2);
        IterationGoal goal2 = new IterationGoal();
        goal2.setName("Test Goal2");
        goal2.setIteration((Iteration) backlogDAO.get(iter2Id));
        int goal2Id = (Integer) iterationGoalDAO.create(goal2);
        goal2 = iterationGoalDAO.get(goal2Id);
        action.setIterationGoalId(goal2Id);
        action.setBacklogId(iter2Id);

        // execute store operation
        assertEquals("ajax_success", action.ajaxStoreBacklogItem());

        // check that goal is updated
        assertEquals(goal2Id, action.getBacklogItem().getIterationGoal().getId());
        assertEquals(iter2Id, action.getBacklogItem().getBacklog().getId());
        assertEquals(goal2Id, backlogItemDAO.get(bliId).getIterationGoal().getId());
        assertEquals(iter2Id, backlogItemDAO.get(bliId).getBacklog().getId());
    }

	/**
     * Test delete operation.
     */
    public void testDelete() {
        // execute delete operation
        assertNotNull(backlogItemDAO.get(bliId));
        action.setBacklogItemId(bliId);
        assertEquals("success", action.delete());
        // check that bli was deleted from database
        assertNull(backlogItemDAO.get(bliId));
    }

    /**
     * Test delete operation with invalid bli id.
     */
    public void testDelete_invalidId() {
        action.setBacklogItemId(-500);
        assertEquals("error", action.delete());
    }

    /**
     * Test create operation.
     */
    public void testCreate() {
        // execute create operation
        assertNull(action.getBacklogItem());
        assertEquals(1, backlogDAO.get(backlogId).getBacklogItems().size());
        action.setBacklogId(backlogId);
        assertEquals("success", action.create());
        assertNotNull(action.getBacklogItem());
        // check that new bli was created to backlog
        assertEquals(2, backlogDAO.get(backlogId).getBacklogItems().size());
    }

    /**
     * Test create operation with invalid backlog id.
     */
    public void testCreate_invalidId() {
        action.setBacklogId(-500);
        assertEquals("error", action.create());
    }

    /**
     * Test createdDate store
     */
    public void testCreatedDate() {
        assertNull(action.getBacklogItem());
        action.setBacklogId(backlogId);
        action.create();
        assertNotNull(action.getBacklogItem());
        action.getBacklogItem().setName("Updated");
        action.getBacklogItem().setDescription("Updated");
        action.getBacklogItem().setPriority(Priority.CRITICAL);
        action.getBacklogItem().setOriginalEstimate(new AFTime("1h 15min"));
        action.getBacklogItem().setState(State.IMPLEMENTED);
        action.getBacklogItem().setEffortLeft(new AFTime("1h 15min"));
        SecurityUtil.setLoggedUser(userDAO.get(this.userId));
        Calendar created = Calendar.getInstance();
        assertEquals("ajax_success", action.ajaxStoreBacklogItem());
        created.setTime(backlogItemDAO.get(action.getBacklogItemId()).getCreatedDate());
        assertEquals(Calendar.getInstance().get(Calendar.YEAR),created.get(Calendar.YEAR));
        assertEquals(Calendar.getInstance().get(Calendar.MONTH),created.get(Calendar.MONTH));
        assertEquals(Calendar.getInstance().get(Calendar.DAY_OF_MONTH),created.get(Calendar.DAY_OF_MONTH));
        assertEquals(this.userId, backlogItemDAO.get(action.getBacklogItemId()).getCreator().getId());
     }
    
    
    /**
     * Test transforming todo to bli
     */
    
    public void testCreateNewFromTodo() {
        action.setBacklogId(backlogId);
        action.create();
        action.getBacklogItem().setName("Updated");
        action.getBacklogItem().setDescription("Updated");
        action.getBacklogItem().setPriority(Priority.CRITICAL);
        action.getBacklogItem().setOriginalEstimate(new AFTime("1h 15min"));
        action.getBacklogItem().setState(State.IMPLEMENTED);
        action.getBacklogItem().setEffortLeft(new AFTime("1h 15min"));
        SecurityUtil.setLoggedUser(userDAO.get(this.userId));
        assertEquals(Action.SUCCESS,action.store());
        
        Todo foo = new Todo();
        foo.setBacklogItem(action.getBacklogItem());
        foo.setState(State.NOT_STARTED);
        foo.setCreator(SecurityUtil.getLoggedUser());
        foo.setRank(0);
        //foo.setPriority(Priority.BLOCKER);
        
        int todoId = (Integer)todoDAO.create(foo);
        action.setBacklogItemId(0);
        action.setFromTodoId(todoId);
        action.store();
        assertEquals(null, todoDAO.get(todoId));
    }
    
    public void testCreateFromTodo() {
        action.setBacklogId(backlogId);
        action.create();
        action.getBacklogItem().setName("Updated");
        action.getBacklogItem().setDescription("Updated");
        action.getBacklogItem().setPriority(Priority.CRITICAL);
        action.getBacklogItem().setOriginalEstimate(new AFTime("1h 15min"));
        action.getBacklogItem().setState(State.IMPLEMENTED);
        action.getBacklogItem().setEffortLeft(new AFTime("1h 15min"));
        SecurityUtil.setLoggedUser(userDAO.get(this.userId));
        action.setIterationGoalId(goalId);
        assertEquals(Action.SUCCESS,action.store());
        action.setIterationGoalId(0);
        
        Todo foo = new Todo();
        foo.setBacklogItem(action.getBacklogItem());
        foo.setState(State.NOT_STARTED);
        foo.setCreator(SecurityUtil.getLoggedUser());
        foo.setRank(0);
        //foo.setPriority(Priority.BLOCKER);
        
        int todoId = (Integer)todoDAO.create(foo);
        
        action.setBacklogItem(null);
        action.setFromTodoId(todoId);
        action.create();
        assertEquals(foo.getName(), action.getBacklogItem().getName());
        assertEquals(foo.getState(), action.getBacklogItem().getState());
        assertEquals(goalId, action.getIterationGoalId());
        assertEquals(backlogId, action.getBacklogId());
    }

    public void testQuickStoreTodoList() {
        // execute edit operation
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());
        // update status and effortLeft
        action.setState(State.PENDING);
        action.setEffortLeft(new AFTime("3h 15min"));

        // Get first todo from backlog item
        Todo todo = backlogItemDAO.get(bliId).getTodos().iterator().next();
        // Create Map of new todo states
        Map<Integer, State> newTodoStates = new HashMap<Integer, State>();
        newTodoStates.put(todo.getId(), State.PENDING);
        action.setTodoStates(newTodoStates);

        // execute quickStoreBacklogItem operation
        assertEquals("ajax_success", action.quickStoreTodoList());
        // check that bli was updated both to action and database
        assertEquals(State.PENDING, action.getBacklogItem().getState());
        assertEquals("3h 15min", action.getBacklogItem().getEffortLeft()
                .toString());
        assertEquals(State.PENDING, action.getTodoStates().get(todo.getId()));

        assertEquals(State.PENDING, backlogItemDAO.get(bliId).getState());
        assertEquals("3h 15min", backlogItemDAO.get(bliId).getEffortLeft()
                .toString());
        // Check that the new status is updated for the todo
        assertEquals(State.PENDING, backlogItemDAO.get(bliId).getTodos()
                .iterator().next().getState());

    }

    /**
     * Test create operation with invalid backlog id.
     */
    public void testQuickStoreTodoList_invalidId() {
        action.setBacklogItemId(-500);
        assertEquals("ajax_error", action.quickStoreTodoList());
    }

    /**
     * Test ResetBliOrigEstAndEffortLeft operation.
     */
    public void testResetBliOrigEstAndEffortLeft() {
        // execute edit operation
        assertNull(action.getBacklogItem());
        action.setBacklogItemId(bliId);
        assertEquals("success", action.edit());
        assertNotNull(action.getBacklogItem());

        // execute reset operation
        action.resetBliOrigEstAndEffortLeft();

        // check that original estimate and effort left are null
        assertEquals(null, backlogItemDAO.get(bliId).getOriginalEstimate());
        assertEquals(null, backlogItemDAO.get(bliId).getEffortLeft());
    }

    /**
     * Test reset operation with invalid bli id.
     */
    public void testResetBliOrigEstAndEffortLeft_invalidId() {
        action.setBacklogItemId(-500);
        assertEquals("error", action.resetBliOrigEstAndEffortLeft());
    }
    
    public void setBacklogDAO(BacklogDAO backlogDAO) {
        this.backlogDAO = backlogDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setBacklogItemAction(BacklogItemAction action) {
        this.action = action;
    }

    public void setTodoDAO(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    public void setIterationGoalDAO(IterationGoalDAO iterationGoalDAO) {
        this.iterationGoalDAO = iterationGoalDAO;
    }

    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public void setAction(BacklogItemAction action) {
        this.action = action;
    }

    public void setBacklogId(int backlogId) {
        this.backlogId = backlogId;
    }

    public void setBliId(int bliId) {
        this.bliId = bliId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public void setGoalId(int goalId) {
        this.goalId = goalId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ProjectDAO getProjectDAO() {
        return projectDAO;
    }

    public void setProjectDAO(ProjectDAO projectDAO) {
        this.projectDAO = projectDAO;
    }

    public ProductDAO getProductDAO() {
        return productDAO;
    }

    public void setProductDAO(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public BusinessThemeBusiness getBusinessThemeBusiness() {
        return businessThemeBusiness;
    }

    public void setBusinessThemeBusiness(BusinessThemeBusiness businessThemeBusiness) {
        this.businessThemeBusiness = businessThemeBusiness;
    }
}
