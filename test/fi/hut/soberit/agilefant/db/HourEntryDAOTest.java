package fi.hut.soberit.agilefant.db;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class HourEntryDAOTest extends AbstractHibernateTests {
    
    @Autowired
    private HourEntryDAO hourEntryDAO;
    
    @Test
    public void testCalculateSumByStory() {
        executeClassSql();
        assertEquals(140, hourEntryDAO.calculateSumByStory(1));
    }

    @Test
    public void testCalculateSumFromTasksWithoutStory() {
        executeClassSql();
        assertEquals(60, hourEntryDAO.calculateSumFromTasksWithoutStory(1));
    }

}
