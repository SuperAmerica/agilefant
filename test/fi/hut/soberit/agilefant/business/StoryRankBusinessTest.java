package fi.hut.soberit.agilefant.business;

import java.io.Serializable;
import java.util.ArrayList;
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

        storyRanks.get(0).setNext(storyRanks.get(1));
        storyRanks.get(1).setNext(storyRanks.get(2));

        storyRanks.get(1).setPrevious(storyRanks.get(0));
        storyRanks.get(2).setPrevious(storyRanks.get(1));

        context = new Project();

        story = new Story();

        rankable = new StoryRank();
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

        expect(storyRankDAO.retrieveHeadByBacklog(backlog)).andReturn(
                storyRanks.get(0));
        replayAll();
        List<Story> actual = storyRankBusiness
                .retrieveByRankingContext(backlog);
        verifyAll();
        assertSame(story1, actual.get(0));
        assertSame(story2, actual.get(1));
        assertSame(story3, actual.get(2));

    }

    @Test
    public void testRankAbove() {
        Story ref = new Story();

        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(1));
        replayAll();
        storyRankBusiness.rankAbove(story, context, ref);
        verifyAll();
        assertSame(storyRanks.get(0), rankable.getPrevious());
        assertSame(rankable, storyRanks.get(0).getNext());
        assertSame(storyRanks.get(1), rankable.getNext());
        assertSame(rankable, storyRanks.get(1).getPrevious());
    }

    @Test
    public void testRankAbove_toTop() {
        Story ref = new Story();
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(0));
        replayAll();
        storyRankBusiness.rankAbove(story, context, ref);
        verifyAll();
        assertNull(rankable.getPrevious());
        assertSame(rankable, storyRanks.get(0).getPrevious());
        assertSame(storyRanks.get(0), rankable.getNext());
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
        assertNull(rank.getNext());
        assertNull(rank.getPrevious());
    }

    @Test
    public void testRankAbove_contextMove() {
        Story ref = new Story();
        Backlog oldBacklog = new Project();
        expect(storyRankDAO.retrieveByBacklogAndStory(oldBacklog, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(null);
        storyRankDAO.store(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(1));
        replayAll();
        storyRankBusiness.rankAbove(story, context, oldBacklog, ref);
        verifyAll();
    }

    @Test
    public void testRankAbove_contextMove_emptyOld() {
        Story ref = new Story();
        Backlog oldBacklog = new Project();
        expect(storyRankDAO.retrieveByBacklogAndStory(oldBacklog, story))
                .andReturn(null);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(null);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(null);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(1));
        expect(storyRankDAO.create(EasyMock.isA(StoryRank.class))).andReturn(
                new Integer(1));
        expect(storyRankDAO.get(1)).andReturn(rankable);
        replayAll();
        storyRankBusiness.rankAbove(story, context, oldBacklog, ref);
        verifyAll();
    }

    @Test
    public void testRankAbove_contextMove_double() {
        Story ref = new Story();
        StoryRank oldRank = new StoryRank();
        Backlog oldBacklog = new Project();
        expect(storyRankDAO.retrieveByBacklogAndStory(oldBacklog, story))
                .andReturn(oldRank);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        storyRankDAO.remove(oldRank);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(1));
        replayAll();
        storyRankBusiness.rankAbove(story, context, oldBacklog, ref);
        verifyAll();
    }

    @Test
    public void testRankBelow() {
        Story ref = new Story();

        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(1));
        replayAll();
        storyRankBusiness.rankBelow(story, context, ref);
        verifyAll();
        assertSame(storyRanks.get(1), rankable.getPrevious());
        assertSame(rankable, storyRanks.get(1).getNext());
        assertSame(storyRanks.get(2), rankable.getNext());
        assertSame(rankable, storyRanks.get(2).getPrevious());
    }

    @Test
    public void testRankBelow_topBottom() {
        Story ref = new Story();

        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(2));
        replayAll();
        storyRankBusiness.rankBelow(story, context, ref);
        verifyAll();
        assertSame(storyRanks.get(2), rankable.getPrevious());
        assertSame(rankable, storyRanks.get(2).getNext());
        assertNull(rankable.getNext());
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
        assertNull(rank.getNext());
        assertNull(rank.getPrevious());
    }

    @Test
    public void testRankBelow_contextMove() {
        Story ref = new Story();

        Backlog oldBacklog = new Project();
        expect(storyRankDAO.retrieveByBacklogAndStory(oldBacklog, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(null);
        storyRankDAO.store(rankable);

        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveByBacklogAndStory(context, ref)).andReturn(
                storyRanks.get(1));
        replayAll();
        storyRankBusiness.rankBelow(story, context, oldBacklog, ref);
        verifyAll();
    }

    @Test
    public void testRemoveRank() {
        expect(this.storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(this.storyRanks.get(1));
        this.storyRankDAO.remove(this.storyRanks.get(1));
        replayAll();
        this.storyRankBusiness.removeRank(story, context);
        verifyAll();
        assertSame(this.storyRanks.get(2), this.storyRanks.get(0).getNext());
        assertSame(this.storyRanks.get(0), this.storyRanks.get(2).getPrevious());
    }

    @Test
    public void testRemoveRank_top() {
        expect(this.storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(this.storyRanks.get(0));
        this.storyRankDAO.remove(this.storyRanks.get(0));
        replayAll();
        this.storyRankBusiness.removeRank(story, context);
        verifyAll();
        assertNull(this.storyRanks.get(1).getPrevious());
    }

    @Test
    public void testRemoveRank_bottom() {
        expect(this.storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(this.storyRanks.get(2));
        this.storyRankDAO.remove(this.storyRanks.get(2));
        replayAll();
        this.storyRankBusiness.removeRank(story, context);
        verifyAll();
        assertNull(this.storyRanks.get(1).getNext());
    }

    @Test
    public void testRankToBottom() {
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(rankable);
        expect(storyRankDAO.retrieveTailByBacklog(context)).andReturn(
                storyRanks.get(2));
        replayAll();
        storyRankBusiness.rankToBottom(story, context);
        verifyAll();
        assertSame(storyRanks.get(2), rankable.getPrevious());
        assertSame(rankable, storyRanks.get(2).getNext());
        assertNull(rankable.getNext());
    }

    @Test
    public void testRankToBottom_emptyContext() {
        Serializable id = new Integer(1);
        Capture<StoryRank> capt = new Capture<StoryRank>();
        expect(storyRankDAO.retrieveByBacklogAndStory(context, story))
                .andReturn(null);
        expect(storyRankDAO.retrieveTailByBacklog(context)).andReturn(null);
        expect(storyRankDAO.create(EasyMock.capture(capt))).andReturn(id);
        expect(storyRankDAO.get(1)).andReturn(null);
        replayAll();
        storyRankBusiness.rankToBottom(story, context);
        verifyAll();
        StoryRank rank = capt.getValue();
        assertSame(context, rank.getBacklog());
        assertSame(story, rank.getStory());
        assertNull(rank.getNext());
        assertNull(rank.getPrevious());
    }

}
