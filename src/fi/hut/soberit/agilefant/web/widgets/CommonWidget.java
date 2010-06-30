package fi.hut.soberit.agilefant.web.widgets;

import com.opensymphony.xwork2.ActionSupport;

public class CommonWidget extends ActionSupport {
    private static final long serialVersionUID = 4029492283643549647L;

    private int objectId;
    
    private int widgetId;

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }
}
