package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.BacklogItemBusiness;
import fi.hut.soberit.agilefant.business.BusinessThemeBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BusinessTheme;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

public class QuickReferenceAction extends ActionSupport {

    private static final long serialVersionUID = -1633753787414264535L;

    private String uri = "";

    private String id;

    private BacklogBusiness backlogBusiness;

    private BacklogItemBusiness backlogItemBusiness;

    private BusinessThemeBusiness businessThemeBusiness;

    public String resolveLinkTarget() {
        String context = "";
        int objectId = -1;

        if (id == null || id.trim().length() < 4 || id.indexOf(":") < 0) {
            return Action.ERROR;
        }
        
        id = id.trim(); 
        context = id.substring(0, id.indexOf(":")).toUpperCase();
        try {
            objectId = Integer.parseInt(id.substring(id.indexOf(":") + 1));
            Backlog bl = null;
            if (context.equals("BL")) {
                bl = backlogBusiness.getBacklog(objectId);
            } else if (context.equals("BLI")) {
                BacklogItem bli = backlogItemBusiness.getBacklogItem(objectId);
                bl = bli.getBacklog();
            }
            if (bl != null && bl instanceof Product) {
                uri = "editProduct.action?productId=" + bl.getId();
            } else if (bl != null && bl instanceof Project) {
                uri = "editProject.action?projectId=" + bl.getId();
            } else if (bl != null && bl instanceof Iteration) {
                uri = "editIteration.action?iterationId=" + bl.getId();
            }
            if (context.equals("BLI")) {
                SessionAction ses = new SessionAction();
                ses.setContextType("bli");
                ses.setObjectId(objectId);
                ses.ajaxOpenDialog();
                uri += "#backlogItemTabContainer-" + objectId + "-backlogList";

            } else if (context.equals("TH")) {
                BusinessTheme theme = businessThemeBusiness.getBusinessTheme(objectId);
                Product prod = theme.getProduct();
                
                SessionAction ses = new SessionAction();
                ses.setContextType("businessTheme");
                ses.setObjectId(objectId);
                ses.ajaxOpenDialog();
                
                if(prod == null) {
                    uri = "globalThemes.action#businessThemeTabContainer-"+objectId;
                } else {
                    uri = "editProduct.action?productId="+prod.getId()+"#businessThemeTabContainer-"+objectId;
               }
            }
            if(uri.length() == 0) {
                return Action.ERROR;
            }
        } catch (Exception e) {
            return Action.ERROR;
        }
        return Action.SUCCESS;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BacklogBusiness getBacklogBusiness() {
        return backlogBusiness;
    }

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public BacklogItemBusiness getBacklogItemBusiness() {
        return backlogItemBusiness;
    }

    public void setBacklogItemBusiness(BacklogItemBusiness bliBusiness) {
        this.backlogItemBusiness = bliBusiness;
    }

    public BusinessThemeBusiness getBusinessThemeBusiness() {
        return businessThemeBusiness;
    }

    public void setBusinessThemeBusiness(BusinessThemeBusiness themeBusiness) {
        this.businessThemeBusiness = themeBusiness;
    }
}
