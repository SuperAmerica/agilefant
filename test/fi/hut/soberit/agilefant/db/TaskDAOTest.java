package fi.hut.soberit.agilefant.db;

import java.util.Arrays;
import java.util.Collection;
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

import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;
import fi.hut.soberit.agilefant.transfer.UnassignedLoadTO;

@ContextConfiguration
@Transactional
public class TaskDAOTest extends AbstractHibernateTests {

    @Autowired  
    private TaskDAO taskDAO;
    
    
    @Test
    public void testGetIterationTasksWithEffortLeft() {
        executeClassSql();
        DateTime start = new DateTime(2009,6,10,1,0,0,0);
        Interval interval = new Interval(start, start.plusDays(5));
        User user = new User();
        user.setId(1);
        List<Task> actual = this.taskDAO.getIterationTasksWithEffortLeft(user, interval);
        assertEquals(2, actual.size());
    }
    
    @Test
    public void testGetIterationTasksWithEffortLeft_notInTimeframe() {
        executeClassSql();
        DateTime start = new DateTime(2008,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<Task> actual = this.taskDAO.getIterationTasksWithEffortLeft(user, interval);
        assertEquals(0, actual.size());
    }
    
    @Test
    public void testGetStoryTasksWithEffortLeft() {
        executeClassSql();
        DateTime start = new DateTime(2009,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<Task> actual = this.taskDAO.getStoryTasksWithEffortLeft(user, interval);
        assertEquals(3, actual.size());
    }
    
    @Test
    public void testGetStoryTasksWithEffortLeft_beginsWithin() {
        executeClassSql();
        DateTime start = new DateTime(2009,5,15,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(6));
        List<Task> actual = this.taskDAO.getStoryTasksWithEffortLeft(user, interval);
        assertEquals(3, actual.size());
    }
    
    @Test
    public void testGetStoryTasksWithEffortLeft_EndsWithin() {
        executeClassSql();
        DateTime start = new DateTime(2009,6,29,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<Task> actual = this.taskDAO.getStoryTasksWithEffortLeft(user, interval);
        assertEquals(3, actual.size());
    }
    
    @Test
    public void testGetStoryTasksWithEffortLeft_notInTimeframe() {
        executeClassSql();
        DateTime start = new DateTime(2008,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<Task> actual = this.taskDAO.getStoryTasksWithEffortLeft(user, interval);
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
    public void testGetUnassignedStoryTasksWithEffortLeft() {
        executeClassSql();
        DateTime start = new DateTime(2009,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<UnassignedLoadTO> actual = this.taskDAO.getUnassignedStoryTasksWithEffortLeft(user, interval);
        assertEquals(1, actual.size());
        assertEquals(60000, actual.get(0).effortLeft.intValue());
        assertEquals(100, actual.get(0).availability);
        assertEquals(1, actual.get(0).iterationId);
    }
    
    @Test
    public void testGetUnassignedIterationTasksWithEffortLeft() {
        executeClassSql();
        DateTime start = new DateTime(2009,6,10,1,0,0,0);
        User user = new User();
        user.setId(1);
        Interval interval = new Interval(start, start.plusDays(5));
        List<UnassignedLoadTO> actual = this.taskDAO.getUnassignedIterationTasksWithEffortLeft(user, interval);
        assertEquals(1, actual.size());
        assertEquals(970, actual.get(0).effortLeft.intValue());
        assertEquals(100, actual.get(0).availability);
        assertEquals(1, actual.get(0).iterationId);
    }
    
    
    @Test
    public void testGetTasksWithRankBetween_iterationTop() {
        executeClassSql();
        Collection<Task> actual = taskDAO.getTasksWithRankBetween(0, 2, 1, null);
        assertEquals(3, actual.size());
    }
    
    @Test
    public void testGetTasksWithRankBetween_iterationBottom() {
        executeClassSql();
        Collection<Task> actual = taskDAO.getTasksWithRankBetween(3, 6, 1, null);
        assertEquals(1, actual.size());
    }
    
    @Test
    public void testGetTasksWithRankBetween_iterationEmptyCollection() {
        executeClassSql();
        Collection<Task> actual = taskDAO.getTasksWithRankBetween(2, 0, 1, null);
        assertEquals(0, actual.size());
    }
    
    @Test
    public void testGetTasksWithRankBetween_storyTop() {
        executeClassSql();
        Collection<Task> actual = taskDAO.getTasksWithRankBetween(0, 0, null, 1);
        assertEquals(1, actual.size());
    }
    
    @Test
    public void testGetTasksWithRankBetween_storyBottom() {
        executeClassSql();
        Collection<Task> actual = taskDAO.getTasksWithRankBetween(1, 5, null, 3);
        assertEquals(1, actual.size());
    }
    
    @Test
    public void testGetTasksWithRankBetween_storyEmptyCollection() {
        executeClassSql();
        Collection<Task> actual = taskDAO.getTasksWithRankBetween(2, 1, null, 1);
        assertEquals(0, actual.size());
    }
    
    
    @Test
    public void testGetNextTaskInRank_iteration() {
        executeClassSql();
        Task actual = taskDAO.getNextTaskInRank(0, 1, null);       
        assertEquals(1, actual.getRank());
    }
    
    @Test
    public void testGetNextTaskInRank_iteration_largeCap() {
        executeClassSql();
        Task actual = taskDAO.getNextTaskInRank(25, 3, null);       
        assertEquals(17, actual.getId());
        assertEquals(1500, actual.getRank());
    }
    
    @Test
    public void testGetNextTaskInRank_iteration_notFound() {
        executeClassSql();
        assertNull(taskDAO.getNextTaskInRank(999, 1, null));
    }
    
    @Test
    public void testGetNextTaskInRank_story() {
        executeClassSql();
        Task actual = taskDAO.getNextTaskInRank(0, null, 55);
        assertEquals(1, actual.getRank());
        assertEquals(21, actual.getId());
    }
    
    @Test
    public void testGetNextTaskInRank_story_largeCap() {
        executeClassSql();
        Task actual = taskDAO.getNextTaskInRank(25, null, 55);
        
        assertEquals(22, actual.getId());
        assertEquals(666, actual.getRank());
    }
    
    @Test
    public void testGetNextTaskInRank_story_notFound() {
        executeClassSql();
        assertNull(taskDAO.getNextTaskInRank(999, null, 55));
    }
    
    @Test
    public void testGetLastTaskInRank_iteration() {
        executeClassSql();
        Task actual = taskDAO.getLastTaskInRank(null, 3);
        assertEquals(17, actual.getId());
        assertEquals(1500, actual.getRank());
    }
    
    @Test
    public void testGetLastTaskInRank_story() {
        executeClassSql();
        Task actual = taskDAO.getLastTaskInRank(55, null);
        assertEquals(22, actual.getId());
        assertEquals(666, actual.getRank());
    }
}
