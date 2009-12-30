package fi.hut.soberit.agilefant.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.model.User;
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
        assertEquals(Pair.create(2, 4), iterationDAO
                .getCountOfDoneAndAllTasks(iteration));
    }

    @Test
    public void testGetCountOfDoneAndAllStories() {
        executeClassSql();
        Iteration iteration = iterationDAO.get(1);
        assertEquals(Pair.create(1, 2), iterationDAO
                .getCountOfDoneAndAllStories(iteration));
    }

    @Test
    public void testRetrieveEmptyIterationsWithPlannedSize() {
        executeClassSql();
        DateTime startDate = new DateTime(2009, 6, 4, 0, 0, 0, 0);
        DateTime endDate = new DateTime(2009, 6, 15, 0, 0, 0, 0);
        User user = new User();
        user.setId(1);
        List<Iteration> actual = iterationDAO
                .retrieveEmptyIterationsWithPlannedSize(startDate,
                        endDate, user);
        assertEquals(1, actual.size());
        assertEquals(4, actual.get(0).getId());
    }

    @Test
    public void testRetrieveCurrentAndFutureIterationsAt() {
        executeClassSql();
        DateTime startDate = new DateTime(2009, 8, 1, 0, 0, 0, 0);
        List<Iteration> actual = iterationDAO
                .retrieveCurrentAndFutureIterationsAt(startDate);

        assertEquals(3, actual.get(0).getId());
        assertEquals(1, actual.size());
    }
    
    @Test
    public void testRetrieveDeep_emptyIteration() {
        executeClassSql();
        Iteration actual = iterationDAO.retrieveDeep(3);
        assertNotNull(actual);
    }
    
    @Test
    public void testRetrieveDeep() {
        executeClassSql();
        Iteration actual = iterationDAO.retrieveDeep(1);
        assertNotNull(actual);    
        
    }

    @Test
    public void testRetrieveActiveWithUserAssigned() {
        executeSql("classpath:fi/hut/soberit/agilefant/db/IterationDAOTest-assignments-data.sql");
        List<Iteration> iterations = iterationDAO.retrieveActiveWithUserAssigned(1);
        assertEquals(2, iterations.size());
    }
    
    @Test
    public void testCountIterationStoriesByState() {
        executeClassSql();
        Map<StoryState, Integer> data = iterationDAO.countIterationStoriesByState(3);
        assertEquals(new Integer(0), data.get(StoryState.NOT_STARTED));
        assertEquals(new Integer(1), data.get(StoryState.STARTED));
        assertEquals(new Integer(2), data.get(StoryState.PENDING));
        assertEquals(new Integer(3), data.get(StoryState.BLOCKED));
        assertEquals(new Integer(4), data.get(StoryState.IMPLEMENTED));
        assertEquals(new Integer(5), data.get(StoryState.DONE));
    }

}
