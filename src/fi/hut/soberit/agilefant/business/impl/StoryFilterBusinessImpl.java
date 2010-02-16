package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.StoryFilterBusiness;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.transfer.StoryTO;
import fi.hut.soberit.agilefant.util.StoryFilters;

@Service("storyFilterBusiness")
public class StoryFilterBusinessImpl implements StoryFilterBusiness {

    public List<Story> filterStories(List<Story> stories,
            StoryFilters storyFilters) {
        List<Story> filteredStories = new ArrayList<Story>();
        for (Story story : stories) {
            if (story == null) {
                continue;
            }
            List<Story> filteredChildren = filterStories(story.getChildren(),
                    storyFilters);
            if (!filteredChildren.isEmpty() || filterStory(story, storyFilters)) {
                StoryTO storyTO = new StoryTO(story);
                storyTO.setChildren(filteredChildren);
                filteredStories.add(storyTO);
            }
        }
        return filteredStories;
    }

    public boolean filterStory(Story story, StoryFilters storyFilters) {
        if (!filterByState(story, storyFilters)) {
            return false;
        }
        if (!filterByName(story, storyFilters)) {
            return false;
        }
        if (!filterByLabels(story, storyFilters)) {
            return false;
        }
        return true;
    }

    public boolean filterByState(Story story, StoryFilters storyFilters) {
        for (StoryState storyState : storyFilters.states) {
            if (story.getState() == storyState) {
                return true;
            }
        }
        return false;
    }

    public boolean filterByName(Story story, StoryFilters storyFilters) {
        String name = storyFilters.name;
        if (name == null || name.length() == 0) {
            return true;
        }
        return story.getName().toLowerCase(Locale.ENGLISH).contains(
                name.toLowerCase(Locale.ENGLISH));
    }

    public boolean filterByLabels(Story story, StoryFilters storyFilters) {
        if (storyFilters.labels.isEmpty()) {
            return true;
        }
        for (Label label : story.getLabels()) {
            if (storyFilters.labels.contains(label.getName())) {
                return true;
            }
        }
        return false;
    }

}
