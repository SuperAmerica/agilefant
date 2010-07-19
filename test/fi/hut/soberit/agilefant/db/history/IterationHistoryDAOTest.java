package fi.hut.soberit.agilefant.db.history;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class IterationHistoryDAOTest extends AbstractHibernateTests {

    @Autowired
    private IterationHistoryDAO iterationHistoryDAO;
    
    @Test
    public void testRetrieveInitialTasks() {
        executeClassSql();
        Iteration iteration = new Iteration();
        iteration.setId(1);
        iteration.setStartDate(new DateTime(2010,1,6,0,0,0,0));
        Set<Integer> actual = this.iterationHistoryDAO.retrieveInitialTasks(iteration);
        assertTrue(actual.contains(1));
        assertTrue(actual.contains(2));
        assertFalse(actual.contains(3));
        assertFalse(actual.contains(4));
    }
}
