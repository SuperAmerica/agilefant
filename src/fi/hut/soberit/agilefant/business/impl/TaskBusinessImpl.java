package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Task;

@Service("taskBusiness")
@Transactional
public class TaskBusinessImpl extends GenericBusinessImpl<Task> implements
        TaskBusiness {

    private TaskDAO taskDAO;

    @Autowired
    public void setTaskDAO(TaskDAO taskDAO) {
        this.genericDAO = taskDAO;
        this.taskDAO = taskDAO;
    }

}