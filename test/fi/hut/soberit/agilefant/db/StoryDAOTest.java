package fi.hut.soberit.agilefant.db;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class StoryDAOTest extends AbstractHibernateTests {

    @Autowired
    private StoryDAO storyDAO;
    
    @Test
    public void testGetStoryPointSumByBacklog_firstBacklog() {
        executeClassSql();
        assertEquals(29, storyDAO.getStoryPointSumByBacklog(1));
    }
    
    @Test
    public void testGetStoryPointSumByBacklog_secondBacklog() {
        executeClassSql();
        assertEquals(33, storyDAO.getStoryPointSumByBacklog(3));
    }
    
    @Test
    public void testRemoveStoryWithStoryHourEntries() {
        executeClassSql();
        try {
            storyDAO.remove(2);
            forceFlush();
            fail("Exception not thrown.");
        } catch (ConstraintViolationException cve) {}
    }
    
    @Test
    public void testRemoveStoryWithTaskHourEntries() {
        executeClassSql();
        try {
            storyDAO.remove(3);
            forceFlush();
            fail("Exception not thrown.");
        } catch (ConstraintViolationException cve) {}
    }
    
   @Test
   public void testGetNumberOfResponsiblesByTask() {
       executeClassSql();
       Set<Integer> storyIds = new HashSet<Integer>(Arrays.asList(1,2,3));
       Map<Integer, Integer> actual = this.storyDAO.getNumOfResponsiblesByStory(storyIds);
       assertEquals(1, actual.size());
       assertEquals(1, (int)actual.get(1));      
   }
}
