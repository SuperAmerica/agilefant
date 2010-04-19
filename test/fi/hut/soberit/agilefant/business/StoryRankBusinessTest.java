package fi.hut.soberit.agilefant.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.easymock.Capture;
import org.easymock.EasyMock;
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

    List<StoryRank> storyRanks;

    Backlog context;

    Story story;

    StoryRank rankable;

    @Before
    public void setUp() {
        storyRankBusiness = new StoryRankBusinessImpl();

        storyRankDAO = createStrictMock(StoryRankDAO.class);
        storyRankBusiness.setStoryRankDAO(storyRankDAO);
    }

    @Before
    public void setUp_data() {
        storyRanks = new ArrayList<StoryRank>();

        storyRanks.add(new StoryRank());
        storyRanks.add(new StoryRank());
        storyRanks.add(new StoryRank());

        storyRanks.get(0).setRank(0);
        storyRanks.get(1).setRank(1);
        storyRanks.get(2).setRank(2);

        context = new Project();

        storyRanks.get(0).setBacklog(context);
        storyRanks.get(1).setBacklog(context);
        storyRanks.get(2).setBacklog(context);

        story = new Story();

        rankable = new StoryRank();
        rankable.setBacklog(context);
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
        Story story2 = new Story();
        Story story3 = new Story();
        storyRanks.get(0).setStory(story1);
        storyRanks.get(1).setStory(story2);
        storyRanks.get(2).setStory(story3);

        expect(storyRankDAO.retrieveRanksByBacklog(backlog)).andReturn(
                storyRanks);

        replayAll();
        List<Story> actual = storyRankBusiness
                .retrieveByRankingContext(backlog);
        verifyAll();
        assertSame(story1, actual.get(0));
        assertSame(story2, actual.get(1));
        assertSame(story3, actual.get(2));

    }

    @Test
    public void testGetRankByBacklog() {
        Backlog backlog = new Project();
        StoryRank rank = new StoryRank();
        rank.setRank(556);
        expect(storyRankDAO.retrieveByBacklogAndStory(backlog, story)).andReturn(rank);
        
        replayAll();
        assertEquals(556, storyRankBusiness.getRankByBacklog(story, backlog));
        verifyAll();
    }
    
    
    @Test
    public void testRankAbove() {
        Story ref = new Story();

        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(1));
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        replayAll();
        storyRankBusiness.rankAbove(story, context, ref);
        verifyAll();
        assertSame(0, storyRanks.get(0).getRank());
        assertSame(1, rankable.getRank());
        assertSame(2, storyRanks.get(1).getRank());
        assertSame(3, storyRanks.get(2).getRank());
    }

    @Test
    public void testRankAbove_toTop() {
        Story ref = new Story();
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(0));
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        replayAll();
        storyRankBusiness.rankAbove(story, context, ref);
        verifyAll();
        assertSame(0, rankable.getRank());
        assertSame(1, storyRanks.get(0).getRank());
        assertSame(2, storyRanks.get(1).getRank());
        assertSame(3, storyRanks.get(2).getRank());
    }

    @Test
    public void testRankAbove_emptyContext() {
        Story ref = null;
        Serializable id = new Integer(1);
        Capture<StoryRank> capt = new Capture<StoryRank>();
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(null);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                null);
        expect(storyRankDAO.create(EasyMock.capture(capt))).andReturn(id);
        expect(storyRankDAO.get(1)).andReturn(null);
        replayAll();
        storyRankBusiness.rankAbove(story, context, ref);
        verifyAll();
        StoryRank rank = capt.getValue();
        assertSame(context, rank.getBacklog());
        assertSame(story, rank.getStory());
        assertSame(0, rankable.getRank());

    }

    @Test
    public void testRankBelow() {
        Story ref = new Story();

        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(1));
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        replayAll();
        storyRankBusiness.rankBelow(story, context, ref);
        verifyAll();

        assertSame(0, storyRanks.get(0).getRank());
        assertSame(1, storyRanks.get(1).getRank());
        assertSame(2, rankable.getRank());
        assertSame(3, storyRanks.get(2).getRank());
    }

    @Test
    public void testRankBelow_topBottom() {
        Story ref = new Story();

        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(2));
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        replayAll();
        storyRankBusiness.rankBelow(story, context, ref);
        verifyAll();
        assertSame(0, storyRanks.get(0).getRank());
        assertSame(1, storyRanks.get(1).getRank());
        assertSame(2, storyRanks.get(2).getRank());
        assertSame(3, rankable.getRank());
    }

    @Test
    public void testRankBelow_emptyContext() {
        Story ref = null;
        Serializable id = new Integer(1);
        Capture<StoryRank> capt = new Capture<StoryRank>();
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(null);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                null);
        expect(storyRankDAO.create(EasyMock.capture(capt))).andReturn(id);
        expect(storyRankDAO.get(1)).andReturn(null);
        replayAll();
        storyRankBusiness.rankBelow(story, context, ref);
        verifyAll();
        StoryRank rank = capt.getValue();
        assertSame(context, rank.getBacklog());
        assertSame(story, rank.getStory());
        assertSame(0, rankable.getRank());
    }

    @Test
    public void testRemoveRank() {
        expect(this.storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(this.storyRanks.get(1));
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        this.storyRankDAO.remove(this.storyRanks.get(1));
        replayAll();
        this.storyRankBusiness.removeRank(story, context);
        verifyAll();
        assertSame(0, storyRanks.get(0).getRank());
        assertSame(1, storyRanks.get(2).getRank());
    }

    @Test
    public void testRemoveRank_top() {
        expect(this.storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(this.storyRanks.get(0));
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        this.storyRankDAO.remove(this.storyRanks.get(0));
        replayAll();
        this.storyRankBusiness.removeRank(story, context);
        verifyAll();
        assertSame(0, storyRanks.get(1).getRank());
        assertSame(1, storyRanks.get(2).getRank());
    }

    @Test
    public void testRemoveRank_bottom() {
        expect(this.storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(this.storyRanks.get(2));
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        this.storyRankDAO.remove(this.storyRanks.get(2));
        replayAll();
        this.storyRankBusiness.removeRank(story, context);
        verifyAll();
        assertSame(0, storyRanks.get(0).getRank());
        assertSame(1, storyRanks.get(1).getRank());
    }

    @Test
    public void testRankToBottom() {
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                storyRanks);
        replayAll();
        storyRankBusiness.rankToBottom(story, context);
        verifyAll();
        assertSame(0, storyRanks.get(0).getRank());
        assertSame(1, storyRanks.get(1).getRank());
        assertSame(2, storyRanks.get(2).getRank());
        assertSame(3, rankable.getRank());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testRankToBottom_emptyContext() {
        Serializable id = new Integer(1);
        Capture<StoryRank> capt = new Capture<StoryRank>();
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(null);
        expect(storyRankDAO.retrieveRanksByBacklog(context)).andReturn(
                Collections.EMPTY_LIST);
        expect(storyRankDAO.create(EasyMock.capture(capt))).andReturn(id);
        expect(storyRankDAO.get(1)).andReturn(rankable);
        
        replayAll();
        storyRankBusiness.rankToBottom(story, context);
        verifyAll();
        StoryRank rank = capt.getValue();
        assertSame(context, rank.getBacklog());
        assertSame(story, rank.getStory());
        assertSame(0, rankable.getRank());
    }

}
