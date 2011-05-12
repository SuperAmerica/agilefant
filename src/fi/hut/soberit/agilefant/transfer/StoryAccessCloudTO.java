package fi.hut.soberit.agilefant.transfer;

import fi.hut.soberit.agilefant.model.Story;
import flexjson.JSON;

public class StoryAccessCloudTO {
    private Story story;
    private long count;
    
    public StoryAccessCloudTO(Story story, long count) {
        this.story = story;
        this.count = count;
    }
    
    @JSON
    public Story getStory() {
        return story;
    }
    public void setStory(Story story) {
        this.story = story;
    }
    @JSON
    public long getCount() {
        return count;
    }
    public void setCount(long count) {
        this.count = count;
    }
}
