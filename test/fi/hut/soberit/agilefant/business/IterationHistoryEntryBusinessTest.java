package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.easymock.Capture;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

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
    
    Iteration iteration;
    IterationHistoryEntry latestEntry;
    
    @Before
    public void setUp() {
        this.iterationHistoryEntryBusiness = new IterationHistoryEntryBusinessImpl();
        this.iterationHistoryEntryDAO = createMock(IterationHistoryEntryDAO.class);
        this.iterationDAO = createMock(IterationDAO.class);
        iterationHistoryEntryBusiness.setIterationDAO(iterationDAO);
        iterationHistoryEntryBusiness.setIterationHistoryEntryDAO(iterationHistoryEntryDAO);
        
        iteration = new Iteration();
        iteration.setId(1);
        
        latestEntry = new IterationHistoryEntry();
        latestEntry.setIteration(iteration);
        latestEntry.setId(666);
        latestEntry.setEffortLeftSum(60);
        latestEntry.setOriginalEstimateSum(90);
        latestEntry.setTimestamp(new DateTime());
    }
    
    @Test
    public void testGetLatestOriginalEstimateSum() {
        expect(iterationHistoryEntryDAO.retrieveLatest(iteration.getId()))
                .andReturn(latestEntry);
        replay(iterationHistoryEntryDAO);
        ExactEstimate expectedEstimate = new ExactEstimate(latestEntry.getOriginalEstimateSum());
        assertEquals(expectedEstimate.getMinorUnits(), iterationHistoryEntryBusiness
                .getLatestOriginalEstimateSum(iteration).getMinorUnits());

        verify(iterationHistoryEntryDAO);
    }
    
    
    @Test
    public void testGetLatestOriginalEstimateSum_nullValue() {
        expect(iterationHistoryEntryDAO.retrieveLatest(iteration.getId()))
                .andReturn(null);
        replay(iterationHistoryEntryDAO);

        assertEquals(new Long(0), iterationHistoryEntryBusiness
                .getLatestOriginalEstimateSum(iteration).getMinorUnits());

        verify(iterationHistoryEntryDAO);        
    }
    
    @Test
    public void testUpdateIterationHistory_latestEntryYesterday() {
        latestEntry.setTimestamp(new DateTime().minusDays(1));
        Pair<ExactEstimate, ExactEstimate> sums = Pair.create(new ExactEstimate(10), new ExactEstimate(20));
        
        expect(iterationDAO.get(1)).andReturn(iteration);
        expect(iterationHistoryEntryDAO.retrieveLatest(1)).andReturn(latestEntry);        
        expect(iterationHistoryEntryDAO.calculateCurrentHistoryData(1)).andReturn(sums);
        Capture<IterationHistoryEntry> capturedEntry = new Capture<IterationHistoryEntry>();
        expect(iterationHistoryEntryDAO.create(capture(capturedEntry))).andReturn(123);
        replay(iterationDAO, iterationHistoryEntryDAO);
        
        iterationHistoryEntryBusiness.updateIterationHistory(1);
        IterationHistoryEntry entry = capturedEntry.getValue();
        assertEquals(-50, entry.getDeltaEffortLeft());
        assertEquals(-70, entry.getDeltaOriginalEstimate());
        assertEquals(10, entry.getEffortLeftSum());
        assertEquals(20, entry.getOriginalEstimateSum());
        assertEquals(0, entry.getId());
        assertNotNull(entry.getTimestamp());
        assertEquals(iteration, entry.getIteration());
       
        verify(iterationDAO, iterationHistoryEntryDAO);
    }
    
    @Test
    public void testUpdateIterationHistory_latestEntryToday() {
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
        assertEquals(latestEntry.getId(), entry.getId());
        assertEquals(iteration, entry.getIteration());
        
       
        verify(iterationDAO, iterationHistoryEntryDAO);
    }
    
    @Test
    public void testGetHistoryEntriesForIteration() {
        expect(iterationHistoryEntryDAO.getHistoryEntriesForIteration(iteration.getId()))
            .andReturn(null);
        replay(iterationHistoryEntryDAO);
        
        iterationHistoryEntryBusiness.getHistoryEntriesForIteration(iteration);
        
        verify(iterationHistoryEntryDAO);
    }

}
