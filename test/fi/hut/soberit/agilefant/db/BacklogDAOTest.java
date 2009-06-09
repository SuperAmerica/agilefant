package fi.hut.soberit.agilefant.db;

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

}
