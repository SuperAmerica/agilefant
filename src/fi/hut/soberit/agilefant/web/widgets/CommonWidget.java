package fi.hut.soberit.agilefant.web.widgets;

import com.opensymphony.xwork2.ActionSupport;

public abstract class CommonWidget extends ActionSupport {
    private static final long serialVersionUID = 4029492283643549647L;

    private int objectId;

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }
}
