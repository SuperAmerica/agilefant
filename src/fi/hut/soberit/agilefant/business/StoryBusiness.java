package fi.hut.soberit.agilefant.business;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.ResponsibleContainer;

public interface StoryBusiness extends GenericBusiness<Story> {

    Story store(int storyId, int backlogId, Story dataItem,
            Set<Integer> responsibles) throws ObjectNotFoundException;

    
    public List<Story> getStoriesByBacklog(Backlog backlog);
    
    /**
     * Get the story's tasks as <code>StoryData</code>
     */
    public Collection<Task> getStoryContents(Story story);
    
    public Collection<ResponsibleContainer> getStoryResponsibles(Story story);
}
