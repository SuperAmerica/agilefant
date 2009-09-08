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
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
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

    private int  userId;
    private User user; 

    private List<User> enabledUsers        = new ArrayList<User>();
    private Collection<DailyWorkTaskTO> assignedTasks = new ArrayList<DailyWorkTaskTO>();
    private Collection<DailyWorkTaskTO> nextTasks     = new ArrayList<DailyWorkTaskTO>();

    // TODO: refactor these outside...
    private int taskId;

    private int rankUnderId;
    
    /**
     * Retrieve for JSON data.
     * @return
     */
    public String retrieve() {
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

        if (userId == 0) {
            if (dailyWorkUserId == 0) {
                userId = SecurityUtil.getLoggedUserId();
            } else {
                userId = dailyWorkUserId;
            }
        }

        enabledUsers.addAll(userBusiness.getEnabledUsers());
        Collections.sort(enabledUsers, new UserComparator());

        user = userBusiness.retrieve(userId);
        assignedTasks = dailyWorkBusiness.getAllCurrentTasksForUser(user);

        return Action.SUCCESS;
    }

    public String deleteFromWhatsNext() {
        User thisUser = userBusiness.retrieve(userId);
        Task thisTask = taskBusiness.retrieve(taskId);
        
        dailyWorkBusiness.removeFromWhatsNext(thisUser, thisTask);

        return Action.SUCCESS;
    }
    
    public String addToWhatsNext() {
        if (userId == 0) {
            userId = SecurityUtil.getLoggedUserId();
        }
        
        user = userBusiness.retrieve(userId);
        Task task = taskBusiness.retrieve(taskId);

        dailyWorkBusiness.addToWhatsNext(user, task);
        
        return Action.SUCCESS;
    }
    
    public String rankWhatsNextTaskAndMoveUnder() {
        if (userId == 0) {
            userId = SecurityUtil.getLoggedUserId();
        }
        
        user      = userBusiness.retrieve(userId);
        Task task = taskBusiness.retrieve(taskId);
        
        dailyWorkBusiness.rankUnderTaskOnWhatsNext(user, task, taskBusiness.retrieveIfExists(rankUnderId));
        
        return Action.SUCCESS;
    }
    
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public void setDailyWorkBusiness(DailyWorkBusiness dailyWorkBusiness) {
        this.dailyWorkBusiness = dailyWorkBusiness;
    }
    
    public Collection<DailyWorkTaskTO> getAssignedTasks() {
        return assignedTasks;
    }
    
    public Collection<DailyWorkTaskTO> getNextTasks() {
        return nextTasks;
    }
    
    public User getUser() {
        return user;
    }
    
    public Collection<User> getEnabledUsers() {
        return enabledUsers;
    }

    public void setEnabledUsers(List<User> enabledUsers) {
        this.enabledUsers = enabledUsers;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId; 
    }
    
    public void setRankUnderId(int rankUnderId) {
        this.rankUnderId = rankUnderId;
    }
}