package fi.hut.soberit.agilefant.db;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Interval;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.StoryMetrics;

public interface StoryDAO extends GenericDAO<Story> {

    StoryMetrics calculateMetrics(int storyId);

    public int getStoryPointSumByBacklog(int backlogId);

    public Map<Integer, Integer> getNumOfResponsiblesByStory(Set<Integer> storyIds);
    
    public Collection<Story> getAllIterationStoriesByResponsibleAndInterval(User user, Interval interval);

    List<Story> retrieveActiveIterationStoriesWithUserResponsible(int userId);
    
    public List<Story> searchByName(String name);

}
