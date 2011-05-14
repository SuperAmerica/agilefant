package fi.hut.soberit.agilefant.business;

import java.util.List;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryAccess;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.StoryAccessCloudTO;

public interface StoryAccessBusiness extends GenericBusiness<StoryAccess> {
    public void addAccessEntry(Story story);

    public void addAccessEntry(int storyId);

    public List<StoryAccessCloudTO> calculateOccurences(DateTime start,
            DateTime end, int userId, int numberOfItems);

    public List<StoryAccessCloudTO> calculateOccurences(DateTime start,
            DateTime end, User user, int numberOfItems);

    public List<StoryAccessCloudTO> calculateEditOccurences(DateTime start,
            DateTime end, int userId, int numberOfItems);

    public List<StoryAccessCloudTO> calculateEditOccurences(DateTime start,
            DateTime end, User user, int numberOfItems);
}
