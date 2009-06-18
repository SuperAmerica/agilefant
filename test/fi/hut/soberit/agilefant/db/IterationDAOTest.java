package fi.hut.soberit.agilefant.db;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;
import fi.hut.soberit.agilefant.util.Pair;

@ContextConfiguration
@Transactional
public class IterationDAOTest extends AbstractHibernateTests {
    
    @Autowired    
    private IterationDAO iterationDAO;
    
    @Test
    public void testGetCountOfDoneAndAllTasks() {
        executeClassSql();
        Iteration iteration = iterationDAO.get(1);
        assertEquals(Pair.create(2,4), iterationDAO.getCountOfDoneAndAllTasks(iteration));
    }

}
