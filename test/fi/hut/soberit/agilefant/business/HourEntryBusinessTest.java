package fi.hut.soberit.agilefant.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;



public class HourEntryBusinessTest extends TestCase {

    private HourEntryBusinessImpl bhe;  
    private BacklogItemHourEntryDAO bheDAO;
    
    public void testGetEntriesByBacklogItem() {
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        bhe = new HourEntryBusinessImpl();
        bhe.setBacklogItemHourEntryDAO(bheDAO);
        List<BacklogItemHourEntry> data = new ArrayList<BacklogItemHourEntry>();
        

        
        //set up backlog
        Backlog bl = new Iteration();
        
        //set up backlog items
        BacklogItem bli1 = new BacklogItem();
        bli1.setId(1);
        BacklogItem bli2 = new BacklogItem();
        bli2.setId(2);
        
        //set up hour report data
        BacklogItemHourEntry bhe1 = new BacklogItemHourEntry();
        bhe1.setTimeSpent(new AFTime(40));
        bhe1.setBacklogItem(bli1);
        BacklogItemHourEntry bhe2 = new BacklogItemHourEntry();
        bhe2.setTimeSpent(new AFTime(30));
        bhe2.setBacklogItem(bli1);
        BacklogItemHourEntry bhe3 = new BacklogItemHourEntry();
        bhe3.setTimeSpent(null);
        bhe3.setBacklogItem(bli2);
        
        data.add(bhe1);
        data.add(bhe2);
        data.add(bhe3);
        
        expect(bheDAO.getSumsByBacklog(bl)).andReturn(data);
        
        replay(bheDAO);
        
        try {
            Map<Integer, AFTime> sums = bhe.getSumsByBacklog(bl);
            //check correct sum
            assertEquals(sums.get(new Integer(1)).getTime(),70); 
            //check that null in effort spent is handled correctly
            assertEquals(sums.get(new Integer(2)).getTime(),0);
            verify(bheDAO);
        } catch(Exception e) {
            fail("HourEntryBusiness getSumpsByBacklogTest failed "+e.getMessage());
        }
    }

    
    
}
