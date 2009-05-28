package fi.hut.soberit.agilefant.db.hibernate;

import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.StoryHourEntryDAO;
import fi.hut.soberit.agilefant.model.StoryHourEntry;

@Repository("storyHourEntryDAO")
public class StoryHourEntryDAOHibernate extends GenericDAOHibernate<StoryHourEntry> implements
        StoryHourEntryDAO {

    public StoryHourEntryDAOHibernate() {
        super(StoryHourEntry.class);
    }

}
