package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.StoryFilterBusiness;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;
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

    public List<Story> filterStoryList(List<Story> stories, StoryFilters filters) {
        List<Story> result = new ArrayList<Story>();
        for(Story story : stories) {
            if(filterStory(story, filters)) {
                result.add(story);
            }
        }
        return result;
    }
    public boolean filterStory(Story story, StoryFilters storyFilters) {
        if (!filterByState(story, storyFilters)) {
            return false;
        }
        if(storyFilters.name == null) {
            return true;
        }
        if (filterByName(story, storyFilters) || filterByLabels(story, storyFilters.name) || filterByBacklogName(story, storyFilters.name)) {
            return true;
        }
        return false;
    }

    public boolean filterByState(Story story, StoryFilters storyFilters) {
        if(storyFilters.states == null || storyFilters.states.isEmpty()) {
            return true;
        }
        return storyFilters.states.contains(story.getState());
    }

    public boolean filterByName(Story story, StoryFilters storyFilters) {
        String name = storyFilters.name;
        if(story.getName() == null) {
            return false;
        }
        return story.getName().toLowerCase(Locale.ENGLISH).contains(
                name.toLowerCase(Locale.ENGLISH));
    }
    
    public boolean filterByBacklogName(Story story, String backlogName) {
        if(story.getBacklog() == null) {
            return false;
        }
        String lowerCaseName = backlogName.toLowerCase();
        return story.getBacklog().getName().toLowerCase().contains(lowerCaseName);    
    }

    public boolean filterByLabels(Story story, String labelName) {
        String lowerCaseName = labelName.toLowerCase();
        for (Label label : story.getLabels()) {
            if(label.getName().contains(lowerCaseName)) {
                return true;
            }
        }
        return false;
    }

}
