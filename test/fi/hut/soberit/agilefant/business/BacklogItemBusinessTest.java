package fi.hut.soberit.agilefant.business;

import fi.hut.soberit.agilefant.business.impl.BacklogItemBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import junit.framework.TestCase;
import static org.easymock.EasyMock.*;

public class BacklogItemBusinessTest extends TestCase {

    private BacklogItemBusinessImpl bliBusiness = new BacklogItemBusinessImpl();
    private BacklogItemDAO bliDAO;
    
    public void testRemoveBacklogItem_found(){
        bliDAO = createMock(BacklogItemDAO.class);
        bliBusiness.setBacklogItemDAO(bliDAO);
        BacklogItem bli = new BacklogItem();
        
        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(bli);
        bliDAO.remove(bli);
        replay(bliDAO);
        // run method under test
        bliBusiness.removeBacklogItem(68);
        
        // verify behavior
        verify(bliDAO);
    }

    public void testRemoveBacklogItem_notfound(){
        bliDAO = createMock(BacklogItemDAO.class);
        bliBusiness.setBacklogItemDAO(bliDAO);
        
        // Record expected behavior
        expect(bliDAO.get(68)).andReturn(null);
        replay(bliDAO);
        // run method under test
        bliBusiness.removeBacklogItem(68);
        
        // verify behavior
        verify(bliDAO);
    }

}
