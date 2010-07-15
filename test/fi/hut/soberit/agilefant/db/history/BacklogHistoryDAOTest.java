package fi.hut.soberit.agilefant.db.history;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.hibernate.envers.RevisionType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;

@ContextConfiguration
@Transactional
public class BacklogHistoryDAOTest extends AbstractHibernateTests {

    @Autowired
    private BacklogHistoryDAO backlogHistoryDAO;
    
    @Test
    public void testRetrieveAddedStories() {
        executeClassSql();
        Backlog backlog = new Iteration();
        backlog.setId(1);
        List<AgilefantHistoryEntry> actual = this.backlogHistoryDAO.retrieveAddedStories(backlog);

        assertEquals(RevisionType.ADD, actual.get(0).getRevisionType());
        assertEquals(RevisionType.ADD, actual.get(1).getRevisionType());
        assertEquals(RevisionType.ADD, actual.get(2).getRevisionType());
        assertEquals(1, actual.get(2).getRevision().getId());
        
        assertEquals(RevisionType.ADD, actual.get(3).getRevisionType());
        assertEquals(2, actual.get(3).getRevision().getId());
    }
    
    @Test
    public void testRetrieveDeletedStories() {
        executeClassSql();
        Backlog backlog = new Iteration();
        backlog.setId(1);
        List<AgilefantHistoryEntry> actual = this.backlogHistoryDAO.retrieveDeletedStories(backlog);
        
        assertEquals(RevisionType.DEL, actual.get(0).getRevisionType());
        assertEquals(3, actual.get(0).getRevision().getId());
    }
}
