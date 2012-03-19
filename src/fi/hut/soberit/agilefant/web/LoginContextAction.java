package fi.hut.soberit.agilefant.web;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.SettingBusiness;
import fi.hut.soberit.agilefant.db.export.Atablesmodifier;

@Component("loginContextAction")
@Scope("prototype")
public class LoginContextAction extends ActionSupport {

    private static final long serialVersionUID = -477483113446767662L;

    @Autowired
    private SettingBusiness settingBusiness;
    
    @Autowired
    private BacklogBusiness backlogBusiness;
    

    @Override
    public String execute() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
        // Clean up anonymous tables if any upon login
        Atablesmodifier modifier = new Atablesmodifier();
        modifier.deletetables();
        
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
