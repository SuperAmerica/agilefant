package fi.hut.soberit.agilefant.db;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryAccess;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.StoryAccessCloudTO;

public interface StoryAccessDAO extends GenericDAO<StoryAccess> {
    public Map<Story, Long> calculateAccessCounts(DateTime start, DateTime end, User user);
}
