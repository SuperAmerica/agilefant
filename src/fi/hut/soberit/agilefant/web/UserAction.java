package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.annotations.PrefetchId;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;

/**
 * UserAction
 * 
 * @author khel
 */
@Component("userAction")
@Scope("prototype")
public class UserAction extends ActionSupport implements CRUDAction, Prefetching {

    private static final long serialVersionUID = 284890678155663442L;

    @PrefetchId
    private int userId;

    private User user;
    
    private String password1;

    private Collection<User> users = new ArrayList<User>();
    
    @Autowired
    private UserBusiness userBusiness;

    public String create() {
        user = new User();
        user.setEnabled(true);
        return Action.SUCCESS;
    }

    public String retrieve() {
        user = userBusiness.retrieve(userId);
        return Action.SUCCESS;
    }
    
    public String retrieveAll() {
        users = userBusiness.retrieveAll();
        return Action.SUCCESS;
    }
    
    public String store() {
        user = userBusiness.storeUser(user, password1);
        return Action.SUCCESS;
    }
    
    public String delete() {
        userBusiness.retrieve(userId);
        return Action.SUCCESS;
    }
    
    public String disable() {
        user = userBusiness.retrieve(userId);
        userBusiness.disableUser(user.getId());
        
        return Action.SUCCESS;
    }
    
    public String enable() {
        user = userBusiness.retrieve(userId);
        userBusiness.enableUser(user.getId());
        
        return Action.SUCCESS;
    }
    
    
    public void initializePrefetchedData(int objectId) {
        user = userBusiness.retrieve(objectId);
    }

    /*
     * GETTERS AND SETTERS
     */
    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public Collection<User> getUsers() {
        return users;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }
}
