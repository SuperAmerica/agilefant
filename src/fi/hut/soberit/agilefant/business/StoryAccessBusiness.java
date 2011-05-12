package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryAccess;

public interface StoryAccessBusiness extends GenericBusiness<StoryAccess> {
    public void addAccessEntry(Story story);
    public void addAccessEntry(int storyId);
}
