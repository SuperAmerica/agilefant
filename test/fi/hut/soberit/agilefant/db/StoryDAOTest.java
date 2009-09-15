package fi.hut.soberit.agilefant.db;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
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
        Set<Integer> storyIds = new HashSet<Integer>(Arrays.asList(1, 2, 3));
        Map<Integer, Integer> actual = this.storyDAO
                .getNumOfResponsiblesByStory(storyIds);
        assertEquals(1, actual.size());
        assertEquals(1, (int) actual.get(1));
    }
   
   /*
    * RANKING
    */
   Backlog rankingParent;
   @Before
   public void setUp() {
       rankingParent = new Iteration();
       rankingParent.setId(4);
   }
   
   @Test
   public void testGetLastStoryInRank() {
       executeClassSql();
       Story actual = storyDAO.getLastStoryInRank(rankingParent);
       
       assertEquals(3412, actual.getRank());
       assertEquals(24, actual.getId());
   }
   
   @Test
   public void testGetLastStoryInRank_noStories() {
       executeClassSql();
       rankingParent.setId(5);
       Story actual = storyDAO.getLastStoryInRank(rankingParent);
       assertNull(actual);
   }
   
   @Test
   public void testGetStoriesWithRankBetween_noStories() {
       executeClassSql();
       rankingParent.setId(5);
       Collection<Story> actual = storyDAO.getStoriesWithRankBetween(rankingParent, 0, 100);
       assertNotNull(actual);
       assertEquals(0, actual.size());
   }
   
   @Test
   public void testGetStoriesWithRankBetween_inverseBorders() {
       executeClassSql();
       Collection<Story> actual = storyDAO.getStoriesWithRankBetween(rankingParent, 1000, 0);
       assertNotNull(actual);
       assertEquals(0, actual.size());
   }
   
   @Test
   public void testGetStoriesWithRankBetween_onlyFirst() {
       executeClassSql();
       Collection<Story> actual = storyDAO.getStoriesWithRankBetween(rankingParent, 0, 0);
       assertEquals(1, actual.size());
       assertEquals(21, actual.iterator().next().getId());
   }
   
   @Test
   public void testGetStoriesWithRankBetween_all() {
       executeClassSql();
       Collection<Story> actual = storyDAO.getStoriesWithRankBetween(rankingParent, 0, 10000);
       assertEquals(4, actual.size());
       Iterator<Story> iter = actual.iterator();
       assertEquals(21, iter.next().getId());
       assertEquals(22, iter.next().getId());
       assertEquals(23, iter.next().getId());
       assertEquals(24, iter.next().getId());
   }
}
