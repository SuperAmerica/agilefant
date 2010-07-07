package fi.hut.soberit.agilefant.business;

import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.model.StoryState;

public interface StoryBatchBusiness {
    public void modifyMultiple(Set<Integer> storyIds, StoryState state, List<String> labelNames);
}
