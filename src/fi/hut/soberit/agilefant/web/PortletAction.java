package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.AgilefantWidgetBusiness;
import fi.hut.soberit.agilefant.business.WidgetCollectionBusiness;
import fi.hut.soberit.agilefant.model.AgilefantWidget;
import fi.hut.soberit.agilefant.model.WidgetCollection;

@Component("portletAction")
@Scope("prototype")
public class PortletAction implements ContextAware {

    private int viewId = 1;
    
    private WidgetCollection contents;
    private List<List<AgilefantWidget>> widgetGrid = new ArrayList<List<AgilefantWidget>>();

    @Autowired
    private AgilefantWidgetBusiness agilefantWidgetBusiness;
    
    @Autowired
    private WidgetCollectionBusiness widgetCollectionBusiness;
    
    public String retrieve() {
        contents = widgetCollectionBusiness.retrieve(viewId); 
        widgetGrid = agilefantWidgetBusiness.generateWidgetGrid(contents);
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

    public List<List<AgilefantWidget>> getWidgetGrid() {
        return widgetGrid;
    }
    
    public int getViewId() {
        return viewId;
    }
    
    public void setViewId(int viewId) {
        this.viewId = viewId;
    }
}
