package fi.hut.soberit.agilefant.business;

import org.easymock.Capture;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import fi.hut.soberit.agilefant.business.impl.IterationHistoryEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationHistoryEntryDAO;
import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.util.Pair;


public class IterationHistoryEntryBusinessTest {
    
    private IterationHistoryEntryBusinessImpl iterationHistoryEntryBusiness;
    
    private IterationHistoryEntryDAO iterationHistoryEntryDAO;
    
    private IterationDAO iterationDAO;
    
    @Before
    public void setUp() {
        this.iterationHistoryEntryBusiness = new IterationHistoryEntryBusinessImpl();
        this.iterationHistoryEntryDAO = createMock(IterationHistoryEntryDAO.class);
        this.iterationDAO = createMock(IterationDAO.class);
        iterationHistoryEntryBusiness.setIterationDAO(iterationDAO);
        iterationHistoryEntryBusiness.setIterationHistoryEntryDAO(iterationHistoryEntryDAO);
    }
    
    @Test
    public void testUpdateIterationHistory() {
        Iteration iteration = new Iteration();
        iteration.setId(1);
        
        IterationHistoryEntry latestEntry = new IterationHistoryEntry();
        latestEntry.setEffortLeftSum(60);
        latestEntry.setOriginalEstimateSum(90);
        
        Pair<ExactEstimate, ExactEstimate> sums = Pair.create(new ExactEstimate(10), new ExactEstimate(20));
        
        expect(iterationDAO.get(1)).andReturn(iteration);
        expect(iterationHistoryEntryDAO.retrieveLatest(1)).andReturn(latestEntry);        
        expect(iterationHistoryEntryDAO.calculateCurrentHistoryData(1)).andReturn(sums);
        Capture<IterationHistoryEntry> capturedEntry = new Capture<IterationHistoryEntry>();
        iterationHistoryEntryDAO.store(capture(capturedEntry));
        replay(iterationDAO, iterationHistoryEntryDAO);
        
        iterationHistoryEntryBusiness.updateIterationHistory(1);
        IterationHistoryEntry entry = capturedEntry.getValue();
        assertEquals(-50, entry.getDeltaEffortLeft());
        assertEquals(-70, entry.getDeltaOriginalEstimate());
        assertEquals(10, entry.getEffortLeftSum());
        assertEquals(20, entry.getOriginalEstimateSum());
        assertNotNull(entry.getTimestamp());
        assertEquals(iteration, entry.getIteration());
       
        verify(iterationDAO, iterationHistoryEntryDAO);
    }

}
