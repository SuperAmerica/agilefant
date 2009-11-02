package fi.hut.soberit.agilefant.db;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.test.AbstractHibernateTests;
import fi.hut.soberit.agilefant.util.ProjectBurnupData;

@ContextConfiguration
@Transactional
public class BacklogHistoryEntryDAOTest extends AbstractHibernateTests {
    
    @Autowired
    private BacklogHistoryEntryDAO backlogHistoryEntryDAO;
    
    @Test
    public void testRetrieveBurnupData_duplicate() {
        executeClassSql();
        int count = 0;
        for (ProjectBurnupData.Entry entry : backlogHistoryEntryDAO.retrieveBurnupData(1)) {
            count++;
            assertEquals(20, entry.estimateSum);
            assertEquals(6, entry.doneSum);
        }
        assertEquals(1, count);
    }

    @Test
    public void testRetrieveBurnupData_noHistory() {
        executeClassSql();
        for (ProjectBurnupData.Entry entry : backlogHistoryEntryDAO.retrieveBurnupData(4)) {
            fail("Unexpected: " + entry);
        }
    }
    
    @Test
    public void testRetrieveBurnupData() {
        executeClassSql();
        int count = 0;        
        for (ProjectBurnupData.Entry entry : backlogHistoryEntryDAO.retrieveBurnupData(3)) {
            assertNotNull(entry);
            count++;
        }
        assertEquals(4, count);
    }

}
