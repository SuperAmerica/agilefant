package fi.hut.soberit.agilefant.business;

import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Story;

public interface StoryBusiness extends GenericBusiness<Story> {

    Story store(int storyId, int backlogId, Story dataItem,
            Set<Integer> responsibles) throws ObjectNotFoundException;

}
