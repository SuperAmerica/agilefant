package fi.hut.soberit.agilefant.business.impl;

import java.util.HashMap;
import java.util.Map;

import fi.hut.soberit.agilefant.business.TodoBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.TodoDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.TaskState;
import fi.hut.soberit.agilefant.model.Todo;

public class TodoBusinessImpl extends GenericBusinessImpl<Todo> implements TodoBusiness {
    private TodoDAO todoDAO;
    private TaskDAO taskDAO;

    /** {@inheritDoc} */
    public Todo store(int todoId, int taskId, String name,
            TaskState state) throws ObjectNotFoundException {
        Todo todo = null;
        if (todoId > 0) {
            todo = todoDAO.get(todoId);
            if (todo == null) {
                throw new ObjectNotFoundException("todo.notFound");
            }
        }
        Task task = null;
        if (taskId > 0) {
            task = taskDAO.get(taskId);
            if (task == null) {
                throw new ObjectNotFoundException("task.notFound");
            }
        }
        return this.store(todo, task, name, state);
    }
    
    /** {@inheritDoc} */
    public Todo store(Todo storable, Task task, String name, TaskState state) {
        if (task == null) {
            throw new IllegalArgumentException("Task must be not null");
        }
        if (state == null) {
            throw new IllegalArgumentException("State must be not null");
        }
        if (storable == null) {
            storable = new Todo();
        }
        
        storable.setName(name);
        storable.setState(state);
        storable.setTask(task);
        
        if (storable.getId() == 0) {
            int persistedId = (Integer)todoDAO.create(storable);
            return todoDAO.get(persistedId);
        }
        else {
            todoDAO.store(storable);
            return storable;
        }
    }
      
    public void updateMultipleTodos(Task task,
            Map<Integer, TaskState> newStatesMap, Map<Integer, String> newNamesMap)
            throws ObjectNotFoundException {
        // Map of new todos.
        Map<Integer, Todo> newTodos = new HashMap<Integer, Todo>();
        
        for (Integer todoId : newStatesMap.keySet()) {            
            if (todoId < 0) {
                Todo todo = new Todo();
                todo.setState(newStatesMap.get(todoId));
                newTodos.put(todoId, todo);
            } else {
                Todo todo = todoDAO.get(todoId.intValue());
                if (todo == null) {
                    throw new ObjectNotFoundException("Todo with id: " + todoId
                        + " not found.");
                }
                todo.setState(newStatesMap.get(todoId));
            }
            
        }
        for (Integer todoId : newNamesMap.keySet()) {
            // new todo should already be in the map.
            if (todoId < 0) {
                Todo todo = newTodos.get(todoId);
                if (todo != null) {
                    todo.setName(newNamesMap.get(todoId));                    
                }
            } else {
                Todo todo = todoDAO.get(todoId.intValue());
                if (todo == null) {
                    throw new ObjectNotFoundException("Todo with id: " + todoId
                        + " not found.");
                }
                todo.setName(newNamesMap.get(todoId));
            }
            
        }
        // Save new todos, ranks in reverse order.
        if (newTodos.size() > 0) {
        int lowestRank = -1;
        if (todoDAO.getLowestRankedTodo(task) != null) {
            lowestRank = todoDAO.getLowestRankedTodo(task).getRank();
        }
        int size = newTodos.size();
        int rankOrder = 1;
        
        for (Integer i: newTodos.keySet()) {           
            Todo todo = newTodos.get(i);
            todo.setTask(task);
            task.getTodos().add(todo);
            if(lowestRank  < 0) {
                todo.setRank(size - rankOrder);
            }
            else {
                todo.setRank(lowestRank + 1 + size - rankOrder);
            }
            todoDAO.create(todo);           
            rankOrder++;            
        }
        }
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TodoDao#rankTodoUp(int)
     */
    public void rankTodoUp(int todoId) throws ObjectNotFoundException {        
        Todo todo = getTodoById(todoId);
        Todo upperRankedTodo = this.todoDAO.findUpperRankedTodo(todo);
        if (upperRankedTodo == null) {
            return;
        }
        // Swap ranks with upper ranked todo
        Integer tmpRank = upperRankedTodo.getRank();
        upperRankedTodo.setRank(todo.getRank());
        todo.setRank(tmpRank);
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TodoDao#rankTodoDown(int)
     */
    public void rankTodoDown(int todoId) throws ObjectNotFoundException {        
        Todo todo = getTodoById(todoId);
        Todo lowerRankedTodo = this.todoDAO.findLowerRankedTodo(todo);
        if (lowerRankedTodo == null) {
            return;
        }
        // Swap ranks with lower ranked todo
        Integer tmpRank = lowerRankedTodo.getRank();
        lowerRankedTodo.setRank(todo.getRank());
        todo.setRank(tmpRank);
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TodoDao#rankTodoTop(int)
     */
    public void rankTodoTop(int todoId) throws ObjectNotFoundException {
        Todo todo = getTodoById(todoId);
        // Raise rank for all todos which have lower rank than the todo we are
        // moving.
        this.todoDAO.raiseRankBetween(0, todo.getRank(), todo.getTask());
        // Set todo's rank to zero to send it to top
        todo.setRank(0);
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TodoDao#rankTodoBottom(int)
     */
    public void rankTodoBottom(int todoId) throws ObjectNotFoundException {
        Todo todo = getTodoById(todoId);
        Todo lowestRankedTodo = this.todoDAO.getLowestRankedTodo(todo
                .getTask());
        // If this is the first todo, this gets first rank in any case.               
        if (lowestRankedTodo.getId() == todo.getId()) {
            return;
        }
        else {
            // Set todo's rank to be one unit bigger thank the currently lowest
            // ranked todo.
            todo.setRank(lowestRankedTodo.getRank() + 1);
        }
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TodoDao#getTodoById(int)
     */
    public Todo getTodoById(int todoId) throws ObjectNotFoundException {
        Todo todo = todoDAO.get(todoId);
        if (todo == null) {
            throw new ObjectNotFoundException("Could not find todo with id: "
                    + todoId);
        }
        return todo;
    }

    public void delete(int todoId) {
        Todo cur = todoDAO.get(todoId);
        if(cur != null) {
            cur.getTask().getTodos().remove(cur);
            todoDAO.remove(todoId);
        }
        
    }
    
    public Todo retrieve(int todoId) {
        return todoDAO.get(todoId);
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public void setTodoDAO(TodoDAO todoDAO) {
        this.genericDAO = this.todoDAO = todoDAO;
    }


}
