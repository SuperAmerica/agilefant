package fi.hut.soberit.agilefant.business.impl;

import java.util.Map;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.State;
import fi.hut.soberit.agilefant.model.Task;

public class TaskBusinessImpl implements TaskBusiness {
    private TaskDAO taskDAO;

    public void updateMultipleTaskStates(Map<Integer, State> newStatesMap)
            throws ObjectNotFoundException {
        for (Integer taskId : newStatesMap.keySet()) {
            Task task = taskDAO.get(taskId.intValue());
            if (task == null) {
                throw new ObjectNotFoundException("Task with id: " + taskId
                        + " not found.");
            }
            task.setState(newStatesMap.get(taskId));
        }
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TaskDao#rankTaskUp(int)
     */
    public void rankTaskUp(int taskId) throws ObjectNotFoundException {
        Task task = getTaskById(taskId);
        Task lowerRankedTask = this.taskDAO.findLowerRankedTask(task);
        if (lowerRankedTask == null) {
            return;
        }
        // Swap ranks with lower ranked task
        Integer tmpRank = lowerRankedTask.getRank();
        lowerRankedTask.setRank(task.getRank());
        task.setRank(tmpRank);
        /*
        Task task = getTaskById(taskId);
        Task upperRankedTask = this.taskDAO.findUpperRankedTask(task);
        if (upperRankedTask == null) {
            return;
        }
        // Swap ranks with upper ranked task
        Integer tmpRank = upperRankedTask.getRank();
        upperRankedTask.setRank(task.getRank());
        task.setRank(tmpRank);
        */
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TaskDao#rankTaskDown(int)
     */
    public void rankTaskDown(int taskId) throws ObjectNotFoundException {
        Task task = getTaskById(taskId);
        Task upperRankedTask = this.taskDAO.findUpperRankedTask(task);
        if (upperRankedTask == null) {
            return;
        }
        // Swap ranks with upper ranked task
        Integer tmpRank = upperRankedTask.getRank();
        upperRankedTask.setRank(task.getRank());
        task.setRank(tmpRank);
        /*
        Task task = getTaskById(taskId);
        Task lowerRankedTask = this.taskDAO.findLowerRankedTask(task);
        if (lowerRankedTask == null) {
            return;
        }
        // Swap ranks with lower ranked task
        Integer tmpRank = lowerRankedTask.getRank();
        lowerRankedTask.setRank(task.getRank());
        task.setRank(tmpRank);
        */
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TaskDao#rankTaskTop(int)
     */
    public void rankTaskTop(int taskId) throws ObjectNotFoundException {
        Task task = getTaskById(taskId);
        // Raise rank for all tasks which have lower rank than the task we are
        // moving.
        this.taskDAO.raiseRankBetween(0, task.getRank(), task.getBacklogItem());
        // Set task's rank to zero to send it to top
        task.setRank(0);
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TaskDao#rankTaskBottom(int)
     */
    public void rankTaskBottom(int taskId) throws ObjectNotFoundException {
        Task task = getTaskById(taskId);
        Task lowestRankedTask = this.taskDAO.getLowestRankedTask(task
                .getBacklogItem());
        if (lowestRankedTask == null
                || lowestRankedTask.getId() == task.getId()) {
            return;
        }
        // Set task's rank to be one unit bigger thank the currently lowest
        // ranked
        // task.
        task.setRank(lowestRankedTask.getRank() + 1);
    }

    /* (non-Javadoc)
     * @see fi.hut.soberit.agilefant.business.impl.TaskDao#getTaskById(int)
     */
    public Task getTaskById(int taskId) throws ObjectNotFoundException {
        Task task = taskDAO.get(taskId);
        if (task == null) {
            throw new ObjectNotFoundException("Could not find task with id: "
                    + taskId);
        }
        return task;
    }

}
