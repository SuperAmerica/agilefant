package fi.hut.soberit.agilefant.web;

import java.util.Collection;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.DeliverableDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.PerformedWorkDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.PerformedWork;

public class ShowPerformedWorkAction extends ActionSupport {

    private int taskId;

    private int backlogItemId;

    private int iterationId;

    private int deliverableId;

    private TaskDAO taskDAO;

    private BacklogItemDAO backlogItemDAO;

    private IterationDAO iterationDAO;

    private DeliverableDAO deliverableDAO;

    private PerformedWorkDAO performedWorkDAO;

    private Collection<PerformedWork> works;

    public String execute() {
        if (taskId > 0) {
            works = performedWorkDAO.getPerformedWork(taskDAO.get(taskId));
        } else if (backlogItemId > 0) {
            works = performedWorkDAO.getPerformedWork(backlogItemDAO
                    .get(backlogItemId));
        } else if (iterationId > 0) {
            works = performedWorkDAO.getPerformedWork(iterationDAO
                    .get(iterationId));
        } else if (deliverableId > 0) {
            works = performedWorkDAO.getPerformedWork(deliverableDAO
                    .get(deliverableId));
        }
        return Action.SUCCESS;
    }

    public int getBacklogItemId() {
        return backlogItemId;
    }

    public void setBacklogItemId(int backlogItemId) {
        this.backlogItemId = backlogItemId;
    }

    public int getDeliverableId() {
        return deliverableId;
    }

    public void setDeliverableId(int deliverableId) {
        this.deliverableId = deliverableId;
    }

    public int getIterationId() {
        return iterationId;
    }

    public void setIterationId(int iterationId) {
        this.iterationId = iterationId;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public Collection<PerformedWork> getWorks() {
        return works;
    }

    public void setWorks(Collection<PerformedWork> work) {
        this.works = work;
    }

    public void setBacklogItemDAO(BacklogItemDAO backlogItemDAO) {
        this.backlogItemDAO = backlogItemDAO;
    }

    public void setDeliverableDAO(DeliverableDAO deliverableDAO) {
        this.deliverableDAO = deliverableDAO;
    }

    public void setIterationDAO(IterationDAO iterationDAO) {
        this.iterationDAO = iterationDAO;
    }

    public void setTaskDAO(TaskDAO taskDAO) {
        this.taskDAO = taskDAO;
    }

    public void setPerformedWorkDAO(PerformedWorkDAO performedWorkDAO) {
        this.performedWorkDAO = performedWorkDAO;
    }
}
