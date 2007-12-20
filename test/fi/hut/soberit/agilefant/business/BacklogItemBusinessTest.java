package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.BacklogItemBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;

public class BacklogItemBusinessTest extends TestCase {

    private BacklogItemBusinessImpl bliBusiness = new BacklogItemBusinessImpl();
    private BacklogItemDAO bliDAO;
    private HistoryBusiness historyBusiness = createMock(HistoryBusiness.class);

    public void testRemoveBacklogItem_found() {
        bliDAO = createMock(BacklogItemDAO.class);
        bliBusiness.setBacklogItemDAO(bliDAO);
        bliBusiness.setHistoryBusiness(historyBusiness);
        Backlog backlog = new Iteration();
        backlog.setId(100);
        BacklogItem bli = new BacklogItem();
        bli.setBacklog(backlog);

        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(bli);
        bliDAO.remove(bli);
        replay(bliDAO);
        historyBusiness.updateBacklogHistory(100);
        replay(historyBusiness);
        // run method under test

        try {
            bliBusiness.removeBacklogItem(68);
        } catch (ObjectNotFoundException onfe) {
            fail();
        }
        // verify behavior
        verify(bliDAO);
        verify(historyBusiness);
    }

    public void testRemoveBacklogItem_notfound() {
        bliDAO = createMock(BacklogItemDAO.class);
        bliBusiness.setBacklogItemDAO(bliDAO);

        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(null);
        replay(bliDAO);
        // run method under test
        try {
            bliBusiness.removeBacklogItem(68);
            fail();
        } catch (ObjectNotFoundException onfe) {

        }

        // verify behavior
        verify(bliDAO);
    }

}
