package fi.hut.soberit.agilefant.web;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.PersonalLoadBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.ComputedLoadData;
import fi.hut.soberit.agilefant.transfer.UserLoadLimits;

@Component("userLoadAction")
@Scope("prototype")
public class UserLoadAction extends ActionSupport {
    private static final long serialVersionUID = -3387270060869450376L;

    public static final int DEFAULT_LOAD_INTERVAL_LENGTH = 5;
    
    @Autowired
    private PersonalLoadBusiness personalLoadBusiness;
    
    @Autowired
    private UserBusiness userBusiness;
    
    private int userId;
    
    private ComputedLoadData userLoadData;
    
    private UserLoadLimits loadLimits;
    
    public String retrieveUserLoad() {
        User user = userBusiness.retrieve(userId);
        userLoadData = personalLoadBusiness.retrieveUserLoad(user, DEFAULT_LOAD_INTERVAL_LENGTH);
        return Action.SUCCESS;
    }

    public String dailyLoadLimits() {
        User user = userBusiness.retrieve(userId);
        loadLimits = personalLoadBusiness.getDailyLoadLimitsByUser(user);
        return Action.SUCCESS;
    }
    
    public ComputedLoadData getUserLoadData() {
        return userLoadData;
    }

    public void setUserLoadData(ComputedLoadData userLoadData) {
        this.userLoadData = userLoadData;
    }

    public void setPersonalLoadBusiness(PersonalLoadBusiness personalLoadBusiness) {
        this.personalLoadBusiness = personalLoadBusiness;
    }

    public void setUserBusiness(UserBusiness userBusiness) {
        this.userBusiness = userBusiness;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UserLoadLimits getLoadLimits() {
        return loadLimits;
    }
}
