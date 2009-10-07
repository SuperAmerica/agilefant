package fi.hut.soberit.agilefant.business.impl;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TaskSplitBusiness;
import fi.hut.soberit.agilefant.model.Task;

@Service("taskSplitBusiness")
@Transactional
public class TaskSplitBusinessImpl implements TaskSplitBusiness {
    @Autowired
    private TaskBusiness taskBusiness;

    @Transactional
    public Task splitTask(Task original, Collection<Task> newTasks) {
        if (original == null || newTasks == null) {
            throw new IllegalArgumentException(
                    "Original task and new task list should be given");
        }
        if (original.getId() == 0) {
            throw new RuntimeException("Original story not persisted.");
        }

        persistNewTasks(original, newTasks);
        return original;
    }

    private void persistNewTasks(Task original, Collection<Task> newTasks) {
        Integer parentStoryId     = null;
        if (original.getStory() != null) {
            parentStoryId = original.getStory().getId();
        }
        
        Integer parentIterationId = null;
        if (original.getIteration() != null) {
            parentIterationId = original.getIteration().getId();
        }
        
        for (Task task : newTasks) {
            task.setIteration(original.getIteration());
            task.setStory(original.getStory());
            
            task = taskBusiness.storeTask(task, parentIterationId, parentStoryId, null);
            taskBusiness.rankUnderTask(task, original);
        }
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }
}
