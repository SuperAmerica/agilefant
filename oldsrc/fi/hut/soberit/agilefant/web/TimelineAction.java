package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.TimelineBusiness;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;

public class TimelineAction extends ActionSupport {

    private static final long serialVersionUID = 8198544031152412030L;
    
    private int productId;
    
    private TimelineBusiness timelineBusiness;
    
    private String json;

    public String productData() {
        try {
            json = timelineBusiness.productContentsToJSON(productId);
        }
        catch(ObjectNotFoundException onfe) {
            json = "{}";
        }
        return Action.SUCCESS;
    }

    public String themeData() {
        json = timelineBusiness.getThemeJSON(productId);
        return Action.SUCCESS;
    }
    
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }


    public TimelineBusiness getTimelineBusiness() {
        return timelineBusiness;
    }


    public void setTimelineBusiness(TimelineBusiness timelineBusiness) {
        this.timelineBusiness = timelineBusiness;
    }


    public String getJson() {
        return json;
    }


    public void setJson(String json) {
        this.json = json;
    }
    
}
