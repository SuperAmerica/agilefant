package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.StoryBatchBusiness;
import fi.hut.soberit.agilefant.model.StoryState;

@Scope("prototype")
@Component("multipleStoryAction")
public class MultipleStoryAction {
    
    private List<String> labelNames = new ArrayList<String>();
    private StoryState state = null;
    private Set<Integer> storyIds = new HashSet<Integer>();
    
    @Autowired
    private StoryBatchBusiness storyBatchBusiness;
    
    
    public String updateMultipleStories() {
        storyBatchBusiness.modifyMultiple(storyIds, state, labelNames);
        return Action.SUCCESS;
    }

    
    public StoryState getState() {
        return state;
    }

    public void setState(StoryState state) {
        this.state = state;
    }

    public Set<Integer> getStoryIds() {
        return storyIds;
    }

    public void setStoryIds(Set<Integer> storyIds) {
        this.storyIds = storyIds;
    }

    public List<String> getLabelNames() {
        return labelNames;
    }

    public void setLabelNames(List<String> labelNames) {
        this.labelNames = labelNames;
    }
}
