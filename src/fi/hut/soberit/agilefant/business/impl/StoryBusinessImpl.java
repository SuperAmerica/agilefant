package fi.hut.soberit.agilefant.business.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Story;

@Service("storyBusiness")
@Transactional
public class StoryBusinessImpl extends GenericBusinessImpl<Story> implements
        StoryBusiness {

    private StoryDAO storyDAO;

    @Autowired
    public void setStoryDAO(StoryDAO storyDAO) {
        this.genericDAO = storyDAO;
        this.storyDAO = storyDAO;
    }

}
