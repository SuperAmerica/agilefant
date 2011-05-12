package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.StoryAccessBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.business.UserBusiness;
import fi.hut.soberit.agilefant.db.StoryAccessDAO;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryAccess;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.security.SecurityUtil;
import fi.hut.soberit.agilefant.transfer.StoryAccessCloudTO;

@Service("storyAccessBusiness")
@Transactional
public class StoryAccessBusinessImp extends GenericBusinessImpl<StoryAccess>
        implements StoryAccessBusiness {

    @Autowired
    private StoryAccessDAO storyAccessDAO;
    @Autowired
    private StoryBusiness storyBusiness;
    @Autowired
    private UserBusiness userBusiness;

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

    public List<StoryAccessCloudTO> calculateOccurences(DateTime start,
            DateTime end, int userId) {
        User user = this.userBusiness.retrieve(userId);
        return this.calculateOccurences(start, end, user);

    }

    public List<StoryAccessCloudTO> calculateOccurences(DateTime start,
            DateTime end, User user) {
        Map<Story, Long> data = this.storyAccessDAO.calculateAccessCounts(
                start, end, user);
        List<StoryAccessCloudTO> res = new ArrayList<StoryAccessCloudTO>();
        for (Story story : data.keySet()) {
            res.add(new StoryAccessCloudTO(story, data.get(story)));
        }
        return res;
    }

}
