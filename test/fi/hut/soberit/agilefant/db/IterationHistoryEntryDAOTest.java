package fi.hut.soberit.agilefant.db;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.ExactEstimate;
import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;
import fi.hut.soberit.agilefant.util.Pair;

@ContextConfiguration
@Transactional
public class IterationHistoryEntryDAOTest extends AbstractHibernateTests {
    
    @Autowired
    private IterationHistoryEntryDAO iterationHistoryEntryDAO;

    @Test
    public void testRetrieveLatest() {
        executeClassSql();
        IterationHistoryEntry entry = iterationHistoryEntryDAO.retrieveLatest(1);
        assertEquals(4, entry.getId());
    }

    @Test
    public void testRetrieveLatest_noHistory() {
        executeClassSql();
        IterationHistoryEntry entry = iterationHistoryEntryDAO.retrieveLatest(2);
        assertNull(entry);
    }
    
    @Test
    public void testCalculateCurrentHistoryData() {
        executeClassSql();
        Pair<ExactEstimate, ExactEstimate> sums = iterationHistoryEntryDAO.calculateCurrentHistoryData(1);
        assertEquals(140L, sums.first.getMinorUnits().longValue());
        assertEquals(240L, sums.second.getMinorUnits().longValue());
    }

    @Test
    public void testCalculateCurrentHistoryData_noTasks() {
        executeClassSql();
        Pair<ExactEstimate, ExactEstimate> sums = iterationHistoryEntryDAO.calculateCurrentHistoryData(2);
        assertEquals(0L, sums.getFirst().getMinorUnits().longValue());
        assertEquals(0L, sums.getSecond().getMinorUnits().longValue());
    }

    @Test
    public void testGetHistoryEntriesForIteration() {
        executeClassSql();
        List<IterationHistoryEntry> actualEntries
            = iterationHistoryEntryDAO.getHistoryEntriesForIteration(1);
        assertNotNull(actualEntries);
        assertEquals(60, actualEntries.get(0).getEffortLeftSum());
        assertEquals(60, actualEntries.get(0).getOriginalEstimateSum());
        assertEquals(4, actualEntries.size());
    }
}
