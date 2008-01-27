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
        for(Integer taskId : newStatesMap.keySet()) {
            Task task = taskDAO.get(taskId.intValue());
            if(task == null) {
                throw new ObjectNotFoundException("Task with id: " + taskId + " not found.");
            }
            task.setState(newStatesMap.get(taskId));
        }
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }
}
