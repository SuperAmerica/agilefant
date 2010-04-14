package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;

@Component("loginContextAction")
@Scope("prototype")
public class LoginContextAction extends ActionSupport {

    private static final long serialVersionUID = -477483113446767662L;

    @Autowired
    private SettingBusiness settingBusiness;
    
    @Autowired
    private BacklogBusiness backlogBusiness;
    

    @Override
    public String execute() {
        if (backlogBusiness.countAll() == 0) {
            return "help";
        }
        else if (settingBusiness.isDailyWork()) {
            return "dailyWork";
        }
        else {
            return "selectBacklog";
        }
        
    }

}
