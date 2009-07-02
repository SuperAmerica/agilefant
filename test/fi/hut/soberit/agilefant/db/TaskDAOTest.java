package fi.hut.soberit.agilefant.db;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class TaskDAOTest extends AbstractHibernateTests {

    @Autowired  
    private TaskDAO taskDAO;
    
    @Test
    public void testGetIterationTasksByUserAndTimeframe() {
        executeClassSql();
        DateTime start = new DateTime(2009,6,10,1,0,0,0);
        Interval interval = new Interval(start, start.plusDays(5));
        User user = new User();
        user.setId(1);
        List<Task> actual = this.taskDAO.getIterationTasksByUserAndTimeframe(user, interval);
        assertEquals(2, actual.size());
    }
    
    @Test
    public void testGetIterationTasksByUserAndTimeframe_notInTimeframe() {
        executeClassSql();
        DateTime start = new DateTime(2008,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<Task> actual = this.taskDAO.getIterationTasksByUserAndTimeframe(user, interval);
        assertEquals(0, actual.size());
    }
    
    @Test
    public void testGetStoryTasksByUserAndTimeframe() {
        executeClassSql();
        DateTime start = new DateTime(2009,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<Task> actual = this.taskDAO.getStoryTasksByUserAndTimeframe(user, interval);
        assertEquals(3, actual.size());
    }
    
    @Test
    public void testGetStoryTasksByUserAndTimeframe_notInTimeframe() {
        executeClassSql();
        DateTime start = new DateTime(2008,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<Task> actual = this.taskDAO.getStoryTasksByUserAndTimeframe(user, interval);
        assertEquals(0, actual.size());
    }
    
    @Test
    public void testGetNumOfResponsiblesByTask() {
        executeClassSql();
        Set<Integer> taskIds = new HashSet<Integer>(Arrays.asList(1,2,3,4,5));
        Map<Integer, Integer> actual = this.taskDAO.getNumOfResponsiblesByTask(taskIds);
        assertEquals(1, (int)actual.get(1));
        assertEquals(2, (int)actual.get(2));
        assertEquals(1, (int)actual.get(3));
        assertEquals(2, (int)actual.get(4));
        assertEquals(1, (int)actual.get(5));
    }
    @Test
    public void testGetUnassignedTasksByStoryResponsibles() {
        executeClassSql();
        DateTime start = new DateTime(2009,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<Task> actual = this.taskDAO.getUnassignedTasksByStoryResponsibles(user, interval);
        assertEquals(1, actual.size());
    }
}
