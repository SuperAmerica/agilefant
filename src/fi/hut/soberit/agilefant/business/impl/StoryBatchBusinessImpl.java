package fi.hut.soberit.agilefant.business.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.LabelBusiness;
import fi.hut.soberit.agilefant.business.StoryBatchBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;

@Service("storyBatchBusiness")
public class StoryBatchBusinessImpl implements StoryBatchBusiness {

    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private LabelBusiness labelBusiness;

    public void modifyMultiple(Set<Integer> storyIds, StoryState state,
            List<String> labelNames) {
        Set<Story> stories = new HashSet<Story>();

        // retrieve stories
        for (int storyId : storyIds) {
            stories.add(storyBusiness.retrieve(storyId));
        }

        updateStates(state, stories);

        addLabels(labelNames, stories);

    }

    private void addLabels(List<String> labelNames, Set<Story> stories) {
        if (labelNames != null && !labelNames.isEmpty()) {
            for (Story story : stories) {
                labelBusiness.createStoryLabels(labelNames, story.getId());

            }
        }
    }

    private void updateStates(StoryState state, Set<Story> stories) {
        if (state != null) {
            for (Story story : stories) {
                story.setState(state);
            }
        }
    }

}
