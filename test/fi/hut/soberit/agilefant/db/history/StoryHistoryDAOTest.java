package fi.hut.soberit.agilefant.db.history;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class StoryHistoryDAOTest extends AbstractHibernateTests {
    @Autowired
    private StoryHistoryDAO storyHistoryDAO;
    
    @Test
    public void testRetrieveClosestRevision_inial() {
        executeClassSql();
       Story actual = this.storyHistoryDAO.retrieveClosestRevision(1, 1);
       assertEquals(StoryState.NOT_STARTED, actual.getState());
    }
    
    @Test
    public void testRetrieveClosestRevision_second() {
        executeClassSql();
       Story actual = this.storyHistoryDAO.retrieveClosestRevision(1, 2);
       assertEquals(StoryState.PENDING, actual.getState());
    }
    @Test
    public void testRetrieveClosestRevision_last() {
        executeClassSql();
       Story actual = this.storyHistoryDAO.retrieveClosestRevision(1, 3);
       assertEquals(StoryState.IMPLEMENTED, actual.getState());
    }
    
    @Test
    public void testRetrieveClosestRevision_bigger() {
        executeClassSql();
       Story actual = this.storyHistoryDAO.retrieveClosestRevision(1, 8);
       assertEquals(StoryState.IMPLEMENTED, actual.getState());
    }
}
