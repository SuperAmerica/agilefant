package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TaskSplitBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;

@Service("taskSplitBusiness")
@Transactional
public class TaskSplitBusinessImpl implements TaskSplitBusiness {
    @Autowired
    private TaskDAO taskDAO;

    @Autowired
    private TaskBusiness taskBusiness;

    @Transactional
    public Task splitTask(Task original, Collection<Task> newTasks) {
        if (original == null || newTasks.size() == 0) {
            throw new IllegalArgumentException(
                    "Original task and new tasks should be given");
        }
        if (original.getId() == 0) {
            throw new RuntimeException("Original story not persisted.");
        }

        persistNewTasks(original, newTasks);
        return original;
    }

    private void persistNewTasks(Task original, Collection<Task> newTasks) {
        ArrayList<Task> reversed = new ArrayList<Task>(newTasks);
        Collections.reverse(reversed);

        for (Task task : reversed) {
            task.setIteration(original.getIteration());
            task.setStory(original.getStory());

            //copy responsible from the parent story
            task.getResponsibles().addAll(original.getResponsibles());
            
            int newId = (Integer)taskDAO.create(task);
            task = taskDAO.get(newId);
            taskBusiness.rankUnderTask(task, original);
            taskDAO.store(task);
        }
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }
}
