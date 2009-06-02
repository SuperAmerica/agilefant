package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TodoBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.Todo;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import flexjson.JSONSerializer;

/**
 * Todo Action
 * 
 * @author praty
 * @author khel
 */
@Component("todoAction")
@Scope("prototype")
public class TodoAction extends ActionSupport {

    private static final long serialVersionUID = -8560828440589313663L;

    private int todoId;

    private int taskId;

    private Todo todo;

    private TaskBusiness taskBusiness;

    private TodoBusiness todoBusiness;
    
    private String jsonData;
    
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
        todo = todoBusiness.retrieve(todoId);
        if (todo == null) {
            super.addActionError(super.getText("todo.notFound"));
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }
    
    
    /**
     * Stores the todo for an ajax request.
     */
    public String ajaxStore() {
        Todo ret = null;
        try {
            ret = todoBusiness.store(todoId, taskId, todo.getName(), todo.getState());
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
            todoBusiness.delete(todoId);
        }
        catch (Exception e) {
            return CRUDAction.AJAX_ERROR;
        }
        return CRUDAction.AJAX_SUCCESS;
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

        return (Integer) todoBusiness.create(storable);
    }

    /**
     * Deletes a todo (based on todoId set)
     * 
     * @return Action.SUCCESS if todo was deleted or Action.ERROR if todo wasn't
     *         found
     */
    public String delete() {
        todoBusiness.delete(todoId);
        return Action.SUCCESS;
    }

    /**
     * Transforms a todo to BacklogItem
     * 
     * @author hhaataja
     */
    /*
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
        storedTodo = todoBusiness.get(todoId);
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
        todoBusiness.remove(storedTodo);
        storyBusiness.store(backlogItem);

        this.setBacklogItemId(backlogItem.getId());

        return Action.SUCCESS;
    }
    */

    protected void fillStorable(Todo storable) {
        if (storable.getTask() == null) {
            Task task = taskBusiness.retrieve(taskId);
            if (task == null) {
                super.addActionError(super.getText("story.notFound"));
                return;
            }
            storable.setTask(task);
            task.getTodos().add(storable);
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
            this.taskId = this.todoBusiness.getTodoById(todoId)
                    .getTask().getId();
            return CRUDAction.AJAX_SUCCESS;
        } catch (ObjectNotFoundException onfe) {
            addActionError(onfe.getMessage());
            return CRUDAction.AJAX_ERROR;
        }
    }

    public String moveTodoDown() {
        try {
            this.todoBusiness.rankTodoDown(todoId);
            this.taskId = this.todoBusiness.getTodoById(todoId)
                    .getTask().getId();
            return CRUDAction.AJAX_SUCCESS;
        } catch (ObjectNotFoundException onfe) {
            addActionError(onfe.getMessage());
            return CRUDAction.AJAX_ERROR;
        }
    }

    public String moveTodoBottom() {
        try {
            this.todoBusiness.rankTodoBottom(todoId);
            this.taskId = this.todoBusiness.getTodoById(todoId)
                    .getTask().getId();
            return CRUDAction.AJAX_SUCCESS;
        } catch (ObjectNotFoundException onfe) {
            addActionError(onfe.getMessage());
            return CRUDAction.AJAX_ERROR;
        }
    }

    public String moveTodoTop() {
        try {
            this.todoBusiness.rankTodoTop(todoId);
            this.taskId = this.todoBusiness.getTodoById(todoId)
                    .getTask().getId();
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

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
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

    @Autowired
    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }

    @Autowired
    public void setTodoBusiness(TodoBusiness todoBusiness) {
        this.todoBusiness = todoBusiness;
    }
}