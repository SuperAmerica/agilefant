package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;

@Component("quickReferenceAction")
@Scope("prototype")
public class QuickReferenceAction extends ActionSupport {

    private static final long serialVersionUID = -1633753787414264535L;

    private String uri = "";

    private String id;

    @Autowired
    private BacklogBusiness backlogBusiness;

    @Autowired
    private StoryBusiness storyBusiness;

    //private BusinessThemeBusiness businessThemeBusiness;
    
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
                bl = backlogBusiness.retrieve(objectId);
            } else if (context.equals("BLI")) {
                Story story = storyBusiness.retrieve(objectId);
                bl = story.getBacklog();
            }
            if (bl != null && bl instanceof Product) {
                uri = "editProduct.action?productId=" + bl.getId();
            } else if (bl != null && bl instanceof Project) {
                uri = "editProject.action?projectId=" + bl.getId();
            } else if (bl != null && bl instanceof Iteration) {
                uri = "editIteration.action?iterationId=" + bl.getId();
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

    public void setBacklogBusiness(BacklogBusiness backlogBusiness) {
        this.backlogBusiness = backlogBusiness;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }

    /*
    public BusinessThemeBusiness getBusinessThemeBusiness() {
        return businessThemeBusiness;
    }

    public void setBusinessThemeBusiness(BusinessThemeBusiness themeBusiness) {
        this.businessThemeBusiness = themeBusiness;
    }
    */

}
