package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.BacklogBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogDAO;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Priority;

/**
 * A spring test case for testing the Backlog business layer.
 * 
 * @author hhaataja, rstrom
 * 
 */
public class BacklogBusinessTest extends TestCase {

    private BacklogBusinessImpl backlogBusiness = new BacklogBusinessImpl();
    private HistoryBusiness historyBusiness;
    private BacklogItemDAO bliDAO;
    private BacklogDAO backlogDAO;

    public void testChangePriorityOfMultipleItems() throws Exception{
        bliDAO = createMock(BacklogItemDAO.class);
        backlogBusiness.setBacklogItemDAO(bliDAO);
        BacklogItem bli = new BacklogItem();

        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(bli);
        replay(bliDAO);
        // run method under test
        int ids[] = { 68 };
        backlogBusiness.changePriorityOfMultipleItems(ids, Priority.BLOCKER);
        assertEquals(Priority.BLOCKER, bli.getPriority());

        // verify behavior
        verify(bliDAO);
    }

    public void testCreateBacklogItemToBacklog() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogBusiness.setBacklogDAO(backlogDAO);
        Backlog backlog = new Iteration();

        // Record expected behavior
        expect(backlogDAO.get(68)).andReturn(backlog);
        replay(backlogDAO);
        // run method under test
        BacklogItem bli = backlogBusiness.createBacklogItemToBacklog(68);
        assertEquals(backlog, bli.getBacklog());
        assertTrue(backlog.getBacklogItems().contains(bli));

        // verify behavior
        verify(backlogDAO);
    }

    public void testCreateBakclogItemToBacklog_notFound() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogBusiness.setBacklogDAO(backlogDAO);
        Backlog backlog = new Iteration();

        // Record expected behavior
        expect(backlogDAO.get(-100)).andReturn(null);
        replay(backlogDAO);
        // run method under test
        BacklogItem bli = backlogBusiness.createBacklogItemToBacklog(-100);
        assertEquals(null, bli);
        assertEquals(0, backlog.getBacklogItems().size());

        // verify behavior
        verify(backlogDAO);
    }

    public void testDeleteMultipleItems() {
        backlogDAO = createMock(BacklogDAO.class);
        backlogBusiness.setBacklogDAO(backlogDAO);
        bliDAO = createMock(BacklogItemDAO.class);
        backlogBusiness.setBacklogItemDAO(bliDAO);
        historyBusiness = createMock(HistoryBusiness.class);
        backlogBusiness.setHistoryBusiness(historyBusiness);
        
        Backlog backlog = new Iteration();
        backlog.setId(100);
        BacklogItem bli = new BacklogItem();
        bli.setId(68);
        bli.setBacklog(backlog);
        ArrayList<BacklogItem> blis = new ArrayList<BacklogItem>();
        blis.add(bli);
        backlog.setBacklogItems(blis);

        // Record expected behavior
        expect(backlogDAO.get(backlog.getId())).andReturn(backlog);
        bliDAO.remove(bli.getId());
        historyBusiness.updateBacklogHistory(backlog.getId());
        replay(backlogDAO);
        replay(bliDAO);
        replay(historyBusiness);
        
        // run method under test
        int[] bliIds = { bli.getId() };
        backlogBusiness.deleteMultipleItems(backlog.getId(), bliIds);
        assertFalse(backlog.getBacklogItems().contains(bli));
        
        // verify behavior
        verify(backlogDAO);
        verify(bliDAO);
        verify(historyBusiness);
    }

}
