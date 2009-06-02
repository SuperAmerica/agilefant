package fi.hut.soberit.agilefant.db;

import java.util.List;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;

public interface StoryDAO extends GenericDAO<Story> {

    int countByCreator(User user);

    public List<Story> getStoriesByBacklog(Backlog backlog);
}
