package fi.hut.soberit.agilefant.db;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;

public interface StoryDAO extends GenericDAO<Story> {

    int countByCreator(User user);

}
