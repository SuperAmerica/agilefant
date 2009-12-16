package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.BacklogHistoryEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogHistoryEntryDAO;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;

public class BacklogHistoryEntryBusinessTest {

    private BacklogHistoryEntryDAO backlogHistoryEntryDAO;
    private StoryHierarchyDAO storyHierarchyDAO;
    private BacklogHistoryEntryBusinessImpl backlogHistoryEntryBusiness;
    private BacklogDAO backlogDAO;

    private BacklogHistoryEntry oldEntry;

    private BacklogHistoryEntry newEntry;

    @Before
    public void setUp_dependecies() {
        storyHierarchyDAO = createStrictMock(StoryHierarchyDAO.class);
        backlogHistoryEntryDAO = createStrictMock(BacklogHistoryEntryDAO.class);
        backlogDAO = createStrictMock(BacklogDAO.class);

        backlogHistoryEntryBusiness = new BacklogHistoryEntryBusinessImpl();
        backlogHistoryEntryBusiness
                .setBacklogHistoryEntryDAO(backlogHistoryEntryDAO);
        backlogHistoryEntryBusiness.setStoryHierarchyDAO(storyHierarchyDAO);
        backlogHistoryEntryBusiness.setBacklogDAO(backlogDAO);
    }

    @Before
    public void setUp_data() {
        oldEntry = new BacklogHistoryEntry();
        oldEntry.setTimestamp(new DateTime().minusDays(400));

        newEntry = new BacklogHistoryEntry();
        newEntry.setTimestamp(new DateTime());
    }

    private void replayAll() {
        replay(backlogHistoryEntryDAO, storyHierarchyDAO, backlogDAO);
    }

    private void verifyAll() {
        verify(backlogHistoryEntryDAO, storyHierarchyDAO, backlogDAO);
    }

    @Test
    public void testUpdateHistory_project() {
        Project project = new Project();
        project.setId(1);

        expect(backlogDAO.get(1)).andReturn(project);
        expect(
                backlogHistoryEntryDAO.retrieveLatest(EasyMock
                        .isA(DateTime.class), EasyMock.eq(1))).andReturn(
                oldEntry);
        expect(storyHierarchyDAO.totalLeafDoneStoryPoints(project)).andReturn(
                10l);
        expect(storyHierarchyDAO.totalLeafStoryPoints(project)).andReturn(20l);
        expect(storyHierarchyDAO.totalRootStoryPoints(project)).andReturn(30l);
        Capture<BacklogHistoryEntry> entry = new Capture<BacklogHistoryEntry>();
        backlogHistoryEntryDAO.store(EasyMock.capture(entry));

        replayAll();
        backlogHistoryEntryBusiness.updateHistory(1);
        verifyAll();

        assertEquals(10l, entry.getValue().getDoneSum());
        assertEquals(20l, entry.getValue().getEstimateSum());
        assertEquals(30l, entry.getValue().getRootSum());
        assertEquals(project, entry.getValue().getBacklog());
        assertNotNull(entry.getValue().getTimestamp());
    }

    @Test
    public void testUpdateHistory_iteration() {
        Project project = new Project();
        project.setId(2);
        Iteration iteration = new Iteration();
        iteration.setParent(project);
        expect(backlogDAO.get(1)).andReturn(iteration);
        expect(
                backlogHistoryEntryDAO.retrieveLatest(EasyMock
                        .isA(DateTime.class), EasyMock.eq(2))).andReturn(
                oldEntry);
        expect(storyHierarchyDAO.totalLeafDoneStoryPoints(project)).andReturn(
                10l);
        expect(storyHierarchyDAO.totalLeafStoryPoints(project)).andReturn(20l);
        expect(storyHierarchyDAO.totalRootStoryPoints(project)).andReturn(30l);
        Capture<BacklogHistoryEntry> entry = new Capture<BacklogHistoryEntry>();
        backlogHistoryEntryDAO.store(EasyMock.capture(entry));

        replayAll();
        backlogHistoryEntryBusiness.updateHistory(1);
        verifyAll();

        assertEquals(10l, entry.getValue().getDoneSum());
        assertEquals(20l, entry.getValue().getEstimateSum());
        assertEquals(30l, entry.getValue().getRootSum());
        assertEquals(project, entry.getValue().getBacklog());
        assertNotNull(entry.getValue().getTimestamp());
    }

    @Test
    public void testUpdateHistory_updateLatestEntry() {
        Project project = new Project();
        project.setId(2);
        Iteration iteration = new Iteration();
        iteration.setParent(project);
        expect(backlogDAO.get(1)).andReturn(iteration);
        expect(
                backlogHistoryEntryDAO.retrieveLatest(EasyMock
                        .isA(DateTime.class), EasyMock.eq(2))).andReturn(
                newEntry);
        expect(storyHierarchyDAO.totalLeafDoneStoryPoints(project)).andReturn(
                10l);
        expect(storyHierarchyDAO.totalLeafStoryPoints(project)).andReturn(20l);
        expect(storyHierarchyDAO.totalRootStoryPoints(project)).andReturn(30l);
        backlogHistoryEntryDAO.store(newEntry);

        replayAll();
        backlogHistoryEntryBusiness.updateHistory(1);
        verifyAll();
        assertEquals(20l, newEntry.getEstimateSum());
        assertEquals(30l, newEntry.getRootSum());
        assertEquals(10l, newEntry.getDoneSum());
    }

    @Test
    public void testUpdateHistory_product() {
        Product product = new Product();
        expect(backlogDAO.get(1)).andReturn(product);
        replayAll();
        backlogHistoryEntryBusiness.updateHistory(1);
        verifyAll();
    }

}
