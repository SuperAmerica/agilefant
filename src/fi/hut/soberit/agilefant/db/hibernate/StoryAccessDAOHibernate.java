package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.StoryAccessDAO;
import fi.hut.soberit.agilefant.model.StoryAccess;

@Repository("storyAccessDAO")
public class StoryAccessDAOHibernate extends GenericDAOHibernate<StoryAccess> implements
        StoryAccessDAO {

    protected StoryAccessDAOHibernate() {
        super(StoryAccess.class);
    }

    
}
