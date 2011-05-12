package fi.hut.soberit.agilefant.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.StoryAccessBusiness;

@Component("storyAccessAction")
@Scope("prototype")
public class StoryAccessAction extends ActionSupport {

    private static final long serialVersionUID = 5288474092729204648L;
    
    private int storyId = -1;
    
    @Autowired
    private StoryAccessBusiness storyAccessBusiness;

    public String addAccessEntry() {
        this.storyAccessBusiness.addAccessEntry(storyId);
        return Action.SUCCESS;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }
}
