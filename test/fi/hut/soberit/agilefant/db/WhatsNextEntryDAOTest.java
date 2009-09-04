package fi.hut.soberit.agilefant.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.test.AbstractHibernateTests;

@ContextConfiguration
@Transactional
public class WhatsNextEntryDAOTest extends AbstractHibernateTests {

    @Autowired  
    private WhatsNextEntryDAO testable;
    private User user1;
    private User user2;
    private User user3;
    
    @Before
    public void setUp() {
        user1 = new User();
        user1.setId(1);

        user2 = new User();
        user2.setId(2);

        user3 = new User();
        user3.setId(3);

        executeClassSql();
    }
    
    @Test
    public void testGetLastTaskInRank() {
        WhatsNextEntry e = testable.getLastTaskInRank(user1);
        Task t = e.getTask();
        assertEquals(5, t.getId());
    }
    
    @Test
    public void testGetLastTaskInRank_notFound() {
        WhatsNextEntry e = testable.getLastTaskInRank(user3);
        assertNull(e);
    }
    
    @Test
    public void testGetTasksWithRankBetween() {
        // Ranks: 2,3,5
        Collection<WhatsNextEntry> entries= testable.getTasksWithRankBetween(2, 5, user1);
        assertEquals(3, entries.size());
        
        ArrayList<Integer> ids = new ArrayList<Integer>(Arrays.asList(new Integer[]{ 4, 7, 8 }));
        for (WhatsNextEntry entry: entries) {
            ids.remove((Integer)entry.getId());
        }
        
        // all ids found.
        assertEquals(0, ids.size());
    }
    
    @Test
    public void testGetTasksWithRankBetween_notFound() {
        Collection<WhatsNextEntry> entries= testable.getTasksWithRankBetween(2, 5, user3);
        assertEquals(0, entries.size());
    }
    
    @Test
    public void testGetWhatsNextEntryFor() {
        Task task = new Task();
        task.setId(3);
        WhatsNextEntry entry = testable.getWhatsNextEntryFor(user1, task);
        assertEquals(4, entry.getId());
    }
    
    @Test
    public void testGetWhatsNextEntryFor_notFound() {
        Task task = new Task();
        task.setId(3);
        WhatsNextEntry entry = testable.getWhatsNextEntryFor(user3, task);
        assertNull(entry);
    }
    
    @Test
    public void testGetWhatsNextEntriesFor() {
        Collection<WhatsNextEntry> entries = testable.getWhatsNextEntriesFor(user1);
        assertEquals(5, entries.size());
    }
    
    @Test
    public void testGetWhatsNextEntriesFor_notFound() {
        Collection<WhatsNextEntry> entries = testable.getWhatsNextEntriesFor(user3);
        assertEquals(0, entries.size());
    }
}
