package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Story;

@Component("storyHierarchyAction")
@Scope("prototype")
public class StoryHierarchyAction extends ActionSupport {
    private static final long serialVersionUID = 2599224272717369900L;

    @Autowired
    private StoryBusiness storyBusiness;
    
    private Integer storyId;
    private List<Story> hierarchy = new ArrayList<Story>();
    
    public String recurseHierarchyAsList() {
        Story story = storyBusiness.retrieve(storyId);
        
        story = story.getParent();
        while (story != null) {
            hierarchy.add(0, story);
            story = story.getParent();
        }
        return Action.SUCCESS;
    }
    
    /*
     * SETTERS AND GETTERS 
     */
    
    public void setStoryId(Integer storyId) {
        this.storyId = storyId;
    }
    
    public List<Story> getHierarchy() {
        return hierarchy;
    }

    public void setStoryBusiness(StoryBusiness storyBusiness) {
        this.storyBusiness = storyBusiness;
    }
    
}