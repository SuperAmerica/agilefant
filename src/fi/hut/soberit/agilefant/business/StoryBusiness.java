package fi.hut.soberit.agilefant.business;

import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;

public interface StoryBusiness extends GenericBusiness<Story> {

    Story store(int storyId, int backlogId, Story dataItem,
            Set<Integer> responsibles) throws ObjectNotFoundException;

    
    public List<Story> getStoriesByBacklog(Backlog backlog);
}
