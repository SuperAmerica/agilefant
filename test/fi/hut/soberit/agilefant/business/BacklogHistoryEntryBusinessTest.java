package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.easymock.Capture;
import org.easymock.EasyMock;
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

    private void replayAll() {
        replay(backlogHistoryEntryDAO, storyHierarchyDAO, backlogDAO);
    }

    private void verifyAll() {
        verify(backlogHistoryEntryDAO, storyHierarchyDAO, backlogDAO);
    }

    @Test
    public void testUpdateHistory_project() {
        Project project = new Project();

        expect(backlogDAO.get(1)).andReturn(project);
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
        Iteration iteration = new Iteration();
        iteration.setParent(project);
        expect(backlogDAO.get(1)).andReturn(iteration);
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
    public void testUpdateHistory_product() {
        Product product = new Product();
        expect(backlogDAO.get(1)).andReturn(product);
        replayAll();
        backlogHistoryEntryBusiness.updateHistory(1);
        verifyAll();
    }

}
