package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.DailyWorkBusiness;
import fi.hut.soberit.agilefant.business.TaskBusiness;
import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.transfer.AssignedWorkTO;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;
import fi.hut.soberit.agilefant.util.UserComparator;

@Component("dailyWorkAction")
@Scope("prototype")
public class DailyWorkAction extends ActionSupport {
    private static final long serialVersionUID = -1891256847796843738L;
    
    @Autowired
    private DailyWorkBusiness dailyWorkBusiness;
    
    @Autowired
    private UserBusiness userBusiness;

    @Autowired
    private TaskBusiness taskBusiness;

    @Autowired
    private TransferObjectBusiness transferObjectBusiness;

    private int  userId;
    private User user; 

    private List<User> enabledUsers                 = new ArrayList<User>();
    private Collection<DailyWorkTaskTO> queuedTasks = new ArrayList<DailyWorkTaskTO>();
    private AssignedWorkTO assignedWork;

    private int  taskId;
    private int  rankUnderId;
    private Task task;

    /**
     * Retrieve for JSON data.
     * @return
     */
    public String retrieve() {
        /*
         * Get the user id from session variables. This enables the Daily Work
         * page to remember the selected user.
         */
        
        if (userId == 0) {
            userId = getStoredDailyWorkUserId();
        }

        user = getDefaultUser();

        enabledUsers.addAll(userBusiness.getEnabledUsers());
        Collections.sort(enabledUsers, new UserComparator());

        queuedTasks = dailyWorkBusiness.getQueuedTasksForUser(user);
        assignedWork = dailyWorkBusiness.getAssignedWorkFor(user);
        
        return Action.SUCCESS;
    }

    public String deleteFromWorkQueue() {
        User thisUser = getDefaultUser();
        Task task = taskBusiness.retrieve(taskId);
        
        dailyWorkBusiness.removeFromWhatsNext(thisUser, task);
        
        this.setTask(transferObjectBusiness.constructTaskTO(task));
        return Action.SUCCESS;
    }
    
    public String addToWorkQueue() {
        User thisUser = getDefaultUser();
        Task task = taskBusiness.retrieve(taskId);

        dailyWorkBusiness.addToWhatsNext(thisUser, task);
        
        this.setTask(transferObjectBusiness.constructTaskTO(task));
        return Action.SUCCESS;
    }
    
    public String rankQueueTaskAndMoveUnder() {
        User user = getDefaultUser();
        Task task = taskBusiness.retrieve(taskId);
        
        dailyWorkBusiness.rankUnderTaskOnWhatsNext(user, task, taskBusiness.retrieveIfExists(rankUnderId));
        
        return Action.SUCCESS;
    }
    
    protected User getDefaultUser() {
        if (userId == 0) {
            userId = getLoggedInUserId();
        }
        
        return userBusiness.retrieve(userId);
    }
    
    protected int getStoredDailyWorkUserId() {
        /*
         * Get the user id from session variables. This enables the Daily Work
         * page to remember the selected user.
         */
        int dailyWorkUserId = 0;
        if (ActionContext.getContext() != null
                && ActionContext.getContext().getSession() != null) {
            Object sessionUser = ActionContext.getContext().getSession().get(
                    "dailyWorkUserId");

            if (sessionUser != null) {
                dailyWorkUserId = (Integer) sessionUser;
            }
        }
        
        return dailyWorkUserId;
    }
    
    protected int getLoggedInUserId() {
        return SecurityUtil.getLoggedUserId();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Collection<DailyWorkTaskTO> getAssignedTasks() {
        return queuedTasks;
    }
    
    public User getUser() {
        return user;
    }
    
    public Collection<User> getEnabledUsers() {
        return enabledUsers;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId; 
    }
    
    public void setRankUnderId(int rankUnderId) {
        this.rankUnderId = rankUnderId;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public void setDailyWorkBusiness(DailyWorkBusiness dailyWorkBusiness) {
        this.dailyWorkBusiness = dailyWorkBusiness;
    }

    public void setTaskBusiness(TaskBusiness taskBusiness) {
        this.taskBusiness = taskBusiness;
    }

    public void setTransferObjectBusiness(TransferObjectBusiness transferObjectBusiness) {
        this.transferObjectBusiness = transferObjectBusiness;
    }

    public AssignedWorkTO getAssignedWork() {
        return assignedWork;
    }
}