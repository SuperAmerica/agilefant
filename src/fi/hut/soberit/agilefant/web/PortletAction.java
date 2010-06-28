package fi.hut.soberit.agilefant.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

@Component("portletAction")
@Scope("prototype")
public class PortletAction implements ContextAware {
    
    public String retrieve() {
        return Action.SUCCESS;
    }
    
    public String getContextName() {
        return "portlets";
    }
    public int getContextObjectId() {
        return 0;
    }
}
