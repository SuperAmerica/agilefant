package fi.hut.soberit.agilefant.db;

import static org.junit.Assert.*;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class BacklogDAOTest extends AbstractHibernateTests {
    
    @Autowired    
    private BacklogDAO backlogDAO;
    
    @Test
    public void testCalculateStoryPointSum() {
        executeClassSql();
        assertEquals(20, backlogDAO.calculateStoryPointSum(1));
    }

    @Test
    public void testCalculateStoryPointSumIncludeChildBacklogs_iteration() {
        executeClassSql();
        // Sums: 12 + 17
        assertEquals(12+17, backlogDAO.calculateStoryPointSumIncludeChildBacklogs(4));
    }
    
    @Test
    public void testCalculateStoryPointSumIncludeChildBacklogs_project() {
        executeClassSql();
        // Sums: Proj (30), I1 (12+17), I2 (9)
        assertEquals(30+12+17+9, backlogDAO.calculateStoryPointSumIncludeChildBacklogs(3));
    }
}
