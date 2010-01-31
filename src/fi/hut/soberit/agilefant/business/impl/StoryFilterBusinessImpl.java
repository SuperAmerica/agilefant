package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryFilterBusiness;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.util.StoryFilters;

@Service("storyFilterBusiness")
@Transactional
public class StoryFilterBusinessImpl implements StoryFilterBusiness {
    
    @Transactional(readOnly = true)
    public List<Story> filterStories(List<Story> stories, StoryFilters storyFilters) {
        List<Story> filteredStories;
        filteredStories = filterByStates(stories, storyFilters.states);
        filteredStories = filterByLabels(filteredStories, storyFilters.labels);
        filteredStories = filterByName(filteredStories, storyFilters.name);
        return filteredStories;
    }
    
    private List<Story> filterByStates(List<Story> stories, Set<StoryState> statesToKeep) {
        List<Story> filteredList = new ArrayList<Story>();
        for (Story story : stories) {
            for (StoryState storyState : statesToKeep) {
                if (story.getState() == storyState) {
                    filteredList.add(story);
                }
            }
        }    
        return filteredList;
    }
    
    private List<Story> filterByName(List<Story> stories, String name) {
        if (name == null || name.isEmpty()){
            return stories;
        }
        List<Story> filteredStories = new ArrayList<Story>();
        for (Story story : stories) {
            if (story.getName().contains(name)){
                filteredStories.add(story);
            }
        }
        return filteredStories;
    }
    
    private List<Story> filterByLabels(List<Story> stories, Set<String> labels) {    
        if (labels.isEmpty()){
            return stories;
        }
        List<Story> filteredStories = new ArrayList<Story>();
        for (Story story : stories) {
            for (Label label : story.getLabels()) {
                if (labels.contains(label.getName())) {
                    filteredStories.add(story);
                }
            }
        }
        return filteredStories;
    }
}
