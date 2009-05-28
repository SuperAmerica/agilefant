package fi.hut.soberit.agilefant.web;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TodoBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.TodoDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Todo;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import flexjson.JSONSerializer;

/**
 * Todo Action
 * 
 * @author khel
 */
public class TodoAction extends ActionSupport implements CRUDAction {

    private static final long serialVersionUID = -8560828440589313663L;

    private int todoId;

    private int backlogItemId;

    private Todo todo;

    private TodoDAO todoDAO;

    private BacklogItemDAO backlogItemDAO;

    private TodoBusiness todoBusiness;
    
    private String jsonData = "";

    /**
     * Creates a new todo.
     * 
     * @return Action.SUCCESS
     */
    public String create() {
        todoId = 0;
        todo = new Todo();
        return Action.SUCCESS;
    }

    /**
     * Fetches a todo for editing (based on todoId set)
     * 
     * @return Action.SUCCESS, if todo was found or Action.ERROR if todo wasn't
     *         found
     */
    public String edit() {
        todo = todoDAO.get(todoId);
        if (todo == null) {
            super.addActionError(super.getText("todo.notFound"));
            return Action.ERROR;
        }
        backlogItemId = todo.getBacklogItem().getId();
        return Action.SUCCESS;
    }
    
    
    /**
     * Stores the todo for an ajax request.
     */
    public String ajaxStore() {
        Todo ret = null;
        try {
            ret = todoBusiness.storeTodo(todoId, backlogItemId, todo.getName(), todo.getState());
        }
        catch (Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        JSONSerializer ser = new JSONSerializer();
        ser.include("name").include("id").include("state").exclude("*");
        jsonData = ser.serialize(ret);
        return CRUDAction.AJAX_SUCCESS;
    }
    
    /**
     * Deletes the todo for an ajax request.
     */
    public String ajaxDelete() {
        try {
            todoBusiness.removeTodo(todoId);
        }
        catch (Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
    }

    /**
     * Stores the todo (a new todo created with create() or an old one fetched
     * with edit()
     * 
     * @return Action.SUCCESS if todo is saved ok or Action.ERROR if there's
     *         something wrong. (more information with getActionErrors())
     */
    @Deprecated
    public String store() {
        Todo storable = new Todo();
        //Backlog backlog;

        if (todo.getName().equals("")) {
            super.addActionError(super.getText("todo.missingName"));
            return Action.ERROR;
        }

        if (todoId > 0) {
            storable = todoDAO.get(todoId);
            if (storable == null) {
                super.addActionError(super.getText("todo.notFound"));
                return Action.ERROR;
            }
        }

        this.fillStorable(storable);

        if (super.hasActionErrors()) {
            return Action.ERROR;
        }

        if (todoId == 0) {
            // Set rank for todo temporarily to -1 to indicate new item 
            storable.setRank(-1);
            todoId = (Integer) todoDAO.create(storable);
            // Get rank for new todo.
            Todo lowestRankedTodo = todoDAO.getLowestRankedTodo(storable.getBacklogItem());
            if(lowestRankedTodo == null || lowestRankedTodo.getRank() < 0) {
                storable.setRank(0);
            }
            else {
                storable.setRank(lowestRankedTodo.getRank() + 1);
            }
            todoDAO.store(storable);
        }
        else
            todoDAO.store(storable);
                
        /* Update effort history */
        backlogItemDAO.get(backlogItemId).getBacklog();

        return Action.SUCCESS;
    }

    /**
     * Stores a new todo.
     * 
     * @return The ID of the newly stored todo
     */
    public Integer storeNew() {

        if (todo.getName().equals("")) {
            super.addActionError(super.getText("todo.missingName"));
            return null;
        }

        Todo storable = new Todo();
        this.fillStorable(storable);

        if (super.hasActionErrors()) {
            return null;
        }

        return (Integer) todoDAO.create(storable);
    }

    /**
     * Deletes a todo (based on todoId set)
     * 
     * @return Action.SUCCESS if todo was deleted or Action.ERROR if todo wasn't
     *         found
     */
    public String delete() {
        todo = todoDAO.get(todoId);
        
        if (todo == null) {
            super.addActionError(super.getText("todo.notFound"));
            return Action.ERROR;
        }
        
        BacklogItem backlogItem = todo.getBacklogItem();
        backlogItemId = backlogItem.getId();
        backlogItem.getTodos().remove(todo);
        todo.setBacklogItem(null);
        todoDAO.remove(todo);

        return Action.SUCCESS;
    }

    /**
     * Transforms a todo to BacklogItem
     * 
     * @author hhaataja
     */
    public String transformToBacklogItem() {
        // First store the todo if any changes were made
        this.store();

        Todo storedTodo = new Todo();
        BacklogItem backlogItem = new BacklogItem();

        if (todo.getName().equals("")) {
            super.addActionError(super.getText("todo.missingName"));
            return Action.ERROR;
        }

        // Get todo from database
        storedTodo = todoDAO.get(todoId);
        if (storedTodo == null) {
            super.addActionError(super.getText("todo.notFound"));
            return Action.ERROR;
        }
        // Inherit from todo's backlogItem
        backlogItem.setBacklog(storedTodo.getBacklogItem().getBacklog());
        //TODO: Deprecated method
        backlogItem.setAssignee(storedTodo.getBacklogItem().getAssignee());
        backlogItem.setIterationGoal(storedTodo.getBacklogItem()
                .getIterationGoal());
        backlogItem.setPriority(storedTodo.getBacklogItem().getPriority());

        // Inherit from todo
        backlogItem.setName(storedTodo.getName());
        backlogItem.setDescription(storedTodo.getDescription());
        backlogItem.setState(storedTodo.getState());
        // These are null because they are not defined for todo
        backlogItem.setEffortLeft(null);
        backlogItem.setOriginalEstimate(null);

        // Remove the persistent todo because it has been transformed to backlog
        // item
        todoDAO.remove(storedTodo);
        backlogItemDAO.store(backlogItem);

        this.setBacklogItemId(backlogItem.getId());

        return Action.SUCCESS;
    }

    protected void fillStorable(Todo storable) {
        if (storable.getBacklogItem() == null) {
            BacklogItem backlogItem = backlogItemDAO.get(backlogItemId);
            if (backlogItem == null) {
                super.addActionError(super.getText("backlogItem.notFound"));
                return;
            }
            storable.setBacklogItem(backlogItem);
            backlogItem.getTodos().add(storable);
            storable.setCreator(SecurityUtil.getLoggedUser());
        }

        if (todo.getName().equals("")|| 
                this.todo.getName().trim().equals("")) {
            super.addActionError(super.getText("todo.missingName"));
            return;
        }
        //storable.setPriority(todo.getPriority());
        storable.setState(todo.getState());
        storable.setName(todo.getName());
        storable.setDescription(todo.getDescription());
    }

    public String moveTodoUp() {
        try {
            this.todoBusiness.rankTodoUp(todoId);
            this.backlogItemId = this.todoBusiness.getTodoById(todoId)
                    .getBacklogItem().getId();
            return CRUDAction.AJAX_SUCCESS;
        } catch (ObjectNotFoundException onfe) {
            addActionError(onfe.getMessage());
            return CRUDAction.AJAX_ERROR;
        }
    }

    public String moveTodoDown() {
        try {
            this.todoBusiness.rankTodoDown(todoId);
            this.backlogItemId = this.todoBusiness.getTodoById(todoId)
                    .getBacklogItem().getId();
            return CRUDAction.AJAX_SUCCESS;
        } catch (ObjectNotFoundException onfe) {
            addActionError(onfe.getMessage());
            return CRUDAction.AJAX_ERROR;
        }
    }

    public String moveTodoBottom() {
        try {
            this.todoBusiness.rankTodoBottom(todoId);
            this.backlogItemId = this.todoBusiness.getTodoById(todoId)
                    .getBacklogItem().getId();
            return CRUDAction.AJAX_SUCCESS;
        } catch (ObjectNotFoundException onfe) {
            addActionError(onfe.getMessage());
            return CRUDAction.AJAX_ERROR;
        }
    }

    public String moveTodoTop() {
        try {
            this.todoBusiness.rankTodoTop(todoId);
            this.backlogItemId = this.todoBusiness.getTodoById(todoId)
                    .getBacklogItem().getId();
            return CRUDAction.AJAX_SUCCESS;
        } catch (ObjectNotFoundException onfe) {
            addActionError(onfe.getMessage());
            return CRUDAction.AJAX_ERROR;
        }
    }
    
    /* AUTOGENERATED LIST OF GETTERS AND SETTERS */

    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
        this.todoId = todoId;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public Todo getTodo() {
        return todo;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setTodoDAO(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setTodoBusiness(TodoBusiness todoBusiness) {
        this.todoBusiness = todoBusiness;
    }
}