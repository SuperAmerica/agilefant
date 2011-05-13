package fi.hut.soberit.agilefant.web;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.StoryAccessBusiness;
import fi.hut.soberit.agilefant.transfer.StoryAccessCloudTO;

@Component("storyAccessAction")
@Scope("prototype")
public class StoryAccessAction extends ActionSupport {

    private static final long serialVersionUID = 5288474092729204648L;

    private int storyId = -1;

    private int userId;

    private List<StoryAccessCloudTO> entries;

    @Autowired
    private StoryAccessBusiness storyAccessBusiness;

    public String addAccessEntry() {
        this.storyAccessBusiness.addAccessEntry(storyId);
        return Action.SUCCESS;
    }

    public String calculateAccesses() {
        entries = this.storyAccessBusiness.calculateOccurences(
                new DateTime().minusMonths(4), new DateTime(), userId);
        return Action.SUCCESS;
    }
    
    public String calculateEditAccesses() {
        entries = this.storyAccessBusiness.calculateEditOccurences(
                new DateTime().minusMonths(4), new DateTime(), userId);
        return Action.SUCCESS;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<StoryAccessCloudTO> getEntries() {
        return entries;
    }
}
