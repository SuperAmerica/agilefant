package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Story;

@Repository("storyDAO")
public class StoryDAOHibernate extends GenericDAOHibernate<Story> implements
        StoryDAO {

    public StoryDAOHibernate() {
        super(Story.class);
    }

}
