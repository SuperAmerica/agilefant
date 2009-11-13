package fi.hut.soberit.agilefant.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

@Component("projectPortfolioAction")
@Scope("prototype")
public class ProjectPortfolioAction {
    
    public String retrieve() {
        return Action.SUCCESS;
    }

}
