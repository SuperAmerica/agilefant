package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.StoryHierarchyBusiness;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.util.StoryFilters;

@Component("storyHierarchyAction")
@Scope("prototype")
public class StoryHierarchyAction extends ActionSupport {
    private static final long serialVersionUID = 2599224272717369900L;

    @Autowired
    private StoryBusiness storyBusiness;
    
    @Autowired
    private StoryHierarchyBusiness storyHierarchyBusiness;
    
    private String name;
    private List<Story> stories;
    private Set<StoryState> statesToKeep;
    private Set<String> labels;
    private Integer storyId;
    private Integer projectId;
    private Integer productId;
    private Story story;
    private Integer referenceStoryId;

    private List<Story> hierarchy = new ArrayList<Story>();

    public String recurseHierarchyAsList() {
        story = storyBusiness.retrieve(storyId);

        Story iterator = story;
        while (iterator != null) {
            hierarchy.add(0, iterator);
            iterator = iterator.getParent();
        }
        return Action.SUCCESS;
    }

    public String moveStoryUnder() {
        Story target = this.storyBusiness.retrieve(storyId);
        Story reference = this.storyBusiness.retrieve(referenceStoryId);
        this.storyHierarchyBusiness.moveUnder(target, reference);
        return Action.SUCCESS;
    }

    public String moveStoryAfter() {
        Story target = this.storyBusiness.retrieve(storyId);
        Story reference = this.storyBusiness.retrieve(referenceStoryId);
        this.storyHierarchyBusiness.moveAfter(target, reference);
        return Action.SUCCESS;
    }
    
    public String moveStoryBefore() {
        Story target = this.storyBusiness.retrieve(storyId);
        Story reference = this.storyBusiness.retrieve(referenceStoryId);
        this.storyHierarchyBusiness.moveBefore(target, reference);
        return Action.SUCCESS;
    }
        
    public String retrieveProductRootStories() {
        stories = storyHierarchyBusiness.retrieveProductRootStories(productId);
        return Action.SUCCESS;
    }
    public String retrieveProjectRootStories() {
        stories = storyHierarchyBusiness.retrieveProjectRootStories(projectId);
        return Action.SUCCESS;
    }
    
    public String retrieveFilteredRootStories() {
        StoryFilters storyFilters = new StoryFilters(name, labels, statesToKeep);
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

    public Story getStory() {
        return story;
    }

    public void setReferenceStoryId(Integer parentId) {
        this.referenceStoryId = parentId;
    }

    public void setStoryHierarchyBusiness(
            StoryHierarchyBusiness storyHierarchyBusiness) {
        this.storyHierarchyBusiness = storyHierarchyBusiness;
    }

    public Set<StoryState> getStatesToKeep() {
        return statesToKeep;
    }

    public void setStatesToKeep(Set<StoryState> statesToKeep) {
        this.statesToKeep = statesToKeep;
    }
    
    public List<Story> getStories() {
        return stories;
    }
    
    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

}