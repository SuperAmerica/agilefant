package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fi.hut.soberit.agilefant.business.StoryRankBusiness;
import fi.hut.soberit.agilefant.db.StoryRankDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;

@Service("storyRankBusiness")
public class StoryRankBusinessImpl implements StoryRankBusiness {

    @Autowired
    private StoryRankDAO storyRankDAO;

    public void rankAbove(Story story, Backlog context, Story upper) {
        // TODO Auto-generated method stub

    }

    public void rankAbove(Story story, Backlog context, Backlog fromContext,
            Story upper) {
        // TODO Auto-generated method stub

    }

    public void rankBelow(Story story, Backlog context, Story upper) {
        // TODO Auto-generated method stub

    }

    public void rankBelow(Story story, Backlog context, Backlog fromContext,
            Story upper) {
        // TODO Auto-generated method stub

    }

    public List<Story> retrieveByRankingContext(Backlog backlog) {
        StoryRank currentRank = this.storyRankDAO.retrieveHeadByBacklog(backlog);
        List<Story> result = new ArrayList<Story>();

        while (currentRank != null) {
            result.add(currentRank.getStory());
            currentRank = currentRank.getNext();
        }
        return result;
    }

    public void removeRank(Story story, Backlog context) {
        // TODO Auto-generated method stub

    }

    public void setStoryRankDAO(StoryRankDAO storyRankDAO) {
        this.storyRankDAO = storyRankDAO;
    }
}
