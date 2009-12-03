package fi.hut.soberit.agilefant.business;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StoryRankBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryRankDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryRank;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

public class StoryRankBusinessTest {

    StoryRankBusinessImpl storyRankBusiness;
    
    StoryRankDAO storyRankDAO;
    
    @Before
    public void setUp() {
        storyRankBusiness = new StoryRankBusinessImpl();
        
        storyRankDAO = createStrictMock(StoryRankDAO.class);
        storyRankBusiness.setStoryRankDAO(storyRankDAO);
    }
    
    public void replayAll() {
        replay(storyRankDAO);
    }
    public void verifyAll() {
        verify(storyRankDAO);
    }
    
    @Test
    public void testRetrieveByRankingContext() {
        Backlog backlog = new Project();
        Story story1 = new Story();
        StoryRank rank1 = new StoryRank();
        Story story2 = new Story();
        rank1.setStory(story1);
        StoryRank rank2 = new StoryRank();
        rank2.setStory(story2);
        StoryRank rank3 = new StoryRank();
        Story story3 = new Story();
        rank3.setStory(story3);
        rank1.setNext(rank2);
        rank2.setNext(rank3);
     
        expect(storyRankDAO.retrieveHeadByBacklog(backlog)).andReturn(rank1);
        replayAll();
        List<Story> actual = storyRankBusiness.retrieveByRankingContext(backlog);
        verifyAll();
        assertSame(story1, actual.get(0));
        assertSame(story2, actual.get(1));
        assertSame(story3, actual.get(2));
        
    }
}
