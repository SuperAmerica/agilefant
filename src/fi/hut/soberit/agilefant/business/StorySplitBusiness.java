package fi.hut.soberit.agilefant.business;

import java.util.Collection;

import fi.hut.soberit.agilefant.model.Story;

public interface StorySplitBusiness {

    /**
     * Split a story and create new stories accordingly.
     * 
     * @param original original story to split
     * @param newStories the new stories to create
     * @param moveOriginalStory TODO
     * @return the split story
     */
    public Story splitStory(Story original, Collection<Story> newStories, Collection<Story> oldChangedStories, boolean moveOriginalStory);
    
    public Story getStory(int id);
}
