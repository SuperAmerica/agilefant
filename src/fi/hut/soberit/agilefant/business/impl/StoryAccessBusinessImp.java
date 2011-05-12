package fi.hut.soberit.agilefant.business.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryAccessBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.db.StoryAccessDAO;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryAccess;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;

@Service("storyAccessBusiness")
@Transactional
public class StoryAccessBusinessImp extends GenericBusinessImpl<StoryAccess> implements
        StoryAccessBusiness {

    @Autowired
    private StoryAccessDAO storyAccessDAO;
    @Autowired
    private StoryBusiness storyBusiness;
    
    public StoryAccessBusinessImp() {
        super(StoryAccess.class);
    }

    @Transactional
    public void addAccessEntry(Story story) {
        DateTime now = new DateTime();
        User user = SecurityUtil.getLoggedUser();

        StoryAccess entry = new StoryAccess();
        
        entry.setDate(now);     
        entry.setStory(story);
        entry.setUser(user);
        this.storyAccessDAO.create(entry);
    }

    public void addAccessEntry(int storyId) {
        Story story = this.storyBusiness.retrieve(storyId);
        this.addAccessEntry(story);
    }


}
