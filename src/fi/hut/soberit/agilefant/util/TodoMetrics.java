package fi.hut.soberit.agilefant.util;

import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;

public class TodoMetrics {
    private int doneTodos = 0;
    private int implementedTodos = 0;
    private int pendingTodos = 0;
    private int startedTodos = 0;
    private int notStartedTodos = 0;
    private int total = 0;
    private int blockedTodos = 0;
    
    public void addTodo(Task todo) {
        if(todo == null) {
            return;
        }
        State state = todo.getState();
        if(state == State.DONE) {
            doneTodos++;
        } else if(state == State.BLOCKED) {
            blockedTodos++;
        } else if(state == State.IMPLEMENTED) {
            implementedTodos++;
        } else if(state == State.NOT_STARTED) {
            notStartedTodos++;
        } else if(state == State.PENDING) {
            pendingTodos++;
        } else if(state == State.STARTED) {
            startedTodos++;
        }
        total++;
    }

    public int getDoneTodos() {
        return doneTodos;
    }

    public int getImplementedTodos() {
        return implementedTodos;
    }

    public int getPendingTodos() {
        return pendingTodos;
    }

    public int getStartedTodos() {
        return startedTodos;
    }

    public int getNotStartedTodos() {
        return notStartedTodos;
    }

    public int getTotal() {
        return total;
    }

    public int getBlockedTodos() {
        return blockedTodos;
    }
}
