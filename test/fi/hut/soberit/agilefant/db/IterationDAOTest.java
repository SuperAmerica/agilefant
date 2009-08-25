package fi.hut.soberit.agilefant.db;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Iteration;
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
}
