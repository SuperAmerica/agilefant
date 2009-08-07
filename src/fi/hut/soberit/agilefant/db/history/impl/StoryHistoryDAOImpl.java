package fi.hut.soberit.agilefant.db.history.impl;


import org.springframework.stereotype.Repository;

import fi.hut.soberit.agilefant.db.history.StoryHistoryDAO;
import fi.hut.soberit.agilefant.model.Story;

@Repository("storyHistoryDAO")
public class StoryHistoryDAOImpl extends GenericHistoryDAOImpl<Story> implements StoryHistoryDAO {

    public StoryHistoryDAOImpl() {
        super(Story.class);
    }
    

}
