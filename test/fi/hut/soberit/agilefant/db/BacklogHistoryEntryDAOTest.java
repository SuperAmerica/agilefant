package fi.hut.soberit.agilefant.db;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.BacklogHistoryEntry;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class BacklogHistoryEntryDAOTest extends AbstractHibernateTests {
    
    @Autowired
    private BacklogHistoryEntryDAO backlogHistoryEntryDAO;
    
    @Test
    public void testCalculateForBacklog() {
        executeClassSql();
        BacklogHistoryEntry entry = backlogHistoryEntryDAO.calculateForBacklog(1);
        assertEquals(25, entry.getEstimateSum());
        assertEquals(5, entry.getDoneSum());
        assertNull(entry.getBacklog());
    }

}
