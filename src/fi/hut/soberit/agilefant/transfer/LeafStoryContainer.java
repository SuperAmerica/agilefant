package fi.hut.soberit.agilefant.transfer;

import java.util.List;

public interface LeafStoryContainer {
    public List<StoryTO> getLeafStories();
    public void setLeafStories(List<StoryTO> leafStories);
}
