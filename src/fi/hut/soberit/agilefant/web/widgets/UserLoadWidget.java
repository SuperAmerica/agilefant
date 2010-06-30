package fi.hut.soberit.agilefant.web.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.model.User;

@Component("userLoadWidget")
@Scope("prototype")
public class UserLoadWidget extends CommonWidget {

    private static final long serialVersionUID = 7810437122662724707L;

    private User user;
    
    @Autowired
    private UserBusiness userBusiness;
    
    @Override
    public String execute() {
        user = userBusiness.retrieve(getObjectId());
        return SUCCESS;
    }

    public User getUser() {
        return user;
    }
}
