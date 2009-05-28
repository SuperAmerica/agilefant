package fi.hut.soberit.agilefant.business.impl;

import java.util.HashMap;
import java.util.Map;

import fi.hut.soberit.agilefant.business.TodoBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.TodoDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Todo;

public class TodoBusinessImpl implements TodoBusiness {
    private TodoDAO todoDAO;
    private BacklogItemDAO backlogItemDAO;

    /** {@inheritDoc} */
    public Todo storeTodo(int todoId, int backlogItemId, String name,
            State state) throws ObjectNotFoundException {
        Todo todo = null;
        if (todoId > 0) {
            todo = todoDAO.get(todoId);
            if (todo == null) {
                throw new ObjectNotFoundException("todo.notFound");
            }
        }
        BacklogItem bli = null;
        if (backlogItemId > 0) {
            bli = backlogItemDAO.get(backlogItemId);
            if (bli == null) {
                throw new ObjectNotFoundException("backlogItem.notFound");
            }
        }
        return this.storeTodo(todo, bli, name, state);
    }
    
    /** {@inheritDoc} */
    public Todo storeTodo(Todo storable, BacklogItem bli, String name, State state) {
        if (bli == null) {
            throw new IllegalArgumentException("Backlog item must be not null");
        }
        if (state == null) {
            throw new IllegalArgumentException("State must be not null");
        }
        if (storable == null) {
            storable = new Todo();
        }
        
        storable.setName(name);
        storable.setState(state);
        storable.setBacklogItem(bli);
        
        if (storable.getId() == 0) {
            int persistedId = (Integer)todoDAO.create(storable);
            return todoDAO.get(persistedId);
        }
        else {
            todoDAO.store(storable);
            return storable;
        }
    }
    
    public void removeTodo(int todoId) throws ObjectNotFoundException {
        this.todoDAO.remove(todoId);    
    }
    
    public void removeTodo(Todo todo) throws ObjectNotFoundException{
        this.removeTodo(todo.getId());
    }
    
    public void updateMultipleTodos(BacklogItem bli, Map<Integer, State> newStatesMap, Map<Integer, String> newNamesMap)
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
        if (todoDAO.getLowestRankedTodo(bli) != null) {
            lowestRank = todoDAO.getLowestRankedTodo(bli).getRank();
        }
        int size = newTodos.size();
        int rankOrder = 1;
        
        for (Integer i: newTodos.keySet()) {           
            Todo todo = newTodos.get(i);
            todo.setBacklogItem(bli);
            bli.getTodos().add(todo);
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
        this.todoDAO.raiseRankBetween(0, todo.getRank(), todo.getBacklogItem());
        // Set todo's rank to zero to send it to top
        todo.setRank(0);
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TodoDao#rankTodoBottom(int)
     */
    public void rankTodoBottom(int todoId) throws ObjectNotFoundException {
        Todo todo = getTodoById(todoId);
        Todo lowestRankedTodo = this.todoDAO.getLowestRankedTodo(todo
                .getBacklogItem());
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

    //TODO: refactor!
    public Map<Integer, Integer> getTodoCountByState(int backlogItemId) {
        Map<Integer, Integer> res = new HashMap<Integer,Integer>();
        
        BacklogItem bli = backlogItemDAO.get(backlogItemId);
        if(bli != null) {
            for(Todo t : bli.getTodos()) {
                if(res.get(t.getState().getOrdinal()) == null) {
                    res.put(t.getState().getOrdinal(), 0);
                }
                res.put(t.getState().getOrdinal(), res.get(t.getState().getOrdinal()) + 1);
            }
        }
        return res;
    }
    public void delete(int todoId) {
        Todo cur = todoDAO.get(todoId);
        if(cur != null) {
            cur.getBacklogItem().getTodos().remove(cur);
            todoDAO.remove(todoId);
        }
        
    }
    
    public Todo getTodo(int todoId) {
        return todoDAO.get(todoId);
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setTodoDAO(TodoDAO todoDAO) {
        this.todoDAO = todoDAO;
    }



}
