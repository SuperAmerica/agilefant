package fi.hut.soberit.agilefant.business;

import java.util.List;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.util.StoryFilters;

public interface StoryFilterBusiness {

    List<Story> filterStories(List<Story> stories, StoryFilters storyFilters);

}
