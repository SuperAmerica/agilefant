package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;


@Component("resolveSearchResultAction")
@Scope("prototype")
public class ResolveSearchResultAction extends ActionSupport {
    private static final long serialVersionUID = -3551952340761514545L;
    
    @Autowired
    private StoryBusiness storyBusiness;
    
    private String targetClassName = "";
    private int targetObjectId = 0;
    private int targetBacklogId;

    @Override
    public String execute() {
        if(Story.class.getCanonicalName().equals(this.targetClassName)) {
            return resolveStoryContainer();
        } else if(Iteration.class.getCanonicalName().equals(this.targetClassName)) {
            this.targetBacklogId = this.targetObjectId;
            return "iteration";
        } else if(Project.class.getCanonicalName().equals(this.targetClassName)) {
            this.targetBacklogId = this.targetObjectId;
            return "project";
        } else if(Product.class.getCanonicalName().equals(this.targetClassName)) {
            this.targetBacklogId = this.targetObjectId;
            return "product";
        } else {
            return ERROR;
        }
    }

    private String resolveStoryContainer() {
        Story story = this.storyBusiness.retrieve(targetObjectId);
        this.targetBacklogId = story.getBacklog().getId();
        if(story.getBacklog() instanceof Iteration) {
            return "iteration";
        } else if(story.getBacklog() instanceof Project) {
            return "project";
        } else {
            return "product";
        }
    }

    public void setTargetObjectId(int targetObjectId) {
        this.targetObjectId = targetObjectId;
    }

    public void setTargetClassName(String targetClassName) {
        this.targetClassName = targetClassName;
    }

    public int getTargetBacklogId() {
        return targetBacklogId;
    }
    
    public String getHash() {
        return targetClassName + "_" + targetObjectId;
    }

}
