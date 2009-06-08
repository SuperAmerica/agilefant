package fi.hut.soberit.agilefant.db;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.IterationHistoryEntry;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class IterationHistoryEntryDAOTest extends AbstractHibernateTests {
    
    @Autowired
    private IterationHistoryEntryDAO iterationHistoryEntryDAO;

    @Test
    public void testRetrieveLatest() {
        executeClassSql();
        executeMethodSql();
        IterationHistoryEntry entry = iterationHistoryEntryDAO.retrieveLatest(1);
        assertEquals(4, entry.getId());
    }

    @Test
    public void testRetrieveLatest_noHistory() {
        executeClassSql();
        IterationHistoryEntry entry = iterationHistoryEntryDAO.retrieveLatest(1);
        assertNull(entry);
    }

}
