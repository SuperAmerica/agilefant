package fi.hut.soberit.agilefant.web;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.model.WidgetCollection;
import fi.hut.soberit.agilefant.util.AgilefantWidgetUtils;

@Component("portletAction")
@Scope("prototype")
public class PortletAction implements ContextAware {
    
    private WidgetCollection contents;

    public String retrieve() {
        contents = AgilefantWidgetUtils.getMockWidgetCollection();
        return Action.SUCCESS;
    }
    
    
    public String getContextName() {
        return "portlets";
    }
    public int getContextObjectId() {
        return 0;
    }
    
    public WidgetCollection getContents() {
        return contents;
    }

    public void setContents(WidgetCollection contents) {
        this.contents = contents;
    }
}
