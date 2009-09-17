package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.DailyWorkBusinessImpl;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.WhatsNextEntryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;

public class DailyWorkBusinessTest {
    private DailyWorkBusiness testable;
    
    private TaskDAO taskDAO;

    private WhatsNextEntryDAO whatsNextEntryDAO;
    
    @Before
    public void setUp() {
        testable = new DailyWorkBusinessImpl();
        
        taskDAO = createMock(TaskDAO.class);
        testable.setTaskDAO(taskDAO);
        
        whatsNextEntryDAO = createMock(WhatsNextEntryDAO.class);
        testable.setWhatsNextEntryDAO(whatsNextEntryDAO);
    }

    private void replayAll() {
        replay(taskDAO, whatsNextEntryDAO);
    }
    
    private void verifyAll() {
        verify(taskDAO, whatsNextEntryDAO);
    }

    @Test
    public void testGetAllCurrentTasksForUser() {
        Backlog backlog = new Product();
        backlog.setId(5);
        
        User user = new User();
        ArrayList<Task> tasks = new ArrayList<Task>();
        
        Task task1 = new Task();
        task1.setId(1);
        Task task2 = new Task();
        task2.setId(2);
        Task task3 = new Task();
        task3.setId(3);
        
        tasks.addAll(Arrays.asList(task1, task2));

        WhatsNextEntry entry1 = new WhatsNextEntry();
        entry1.setTask(task2);
        entry1.setUser(user);
        entry1.setRank(2);
        
        WhatsNextEntry entry2 = new WhatsNextEntry();
        entry2.setTask(task3);
        entry2.setUser(user);
        entry2.setRank(1);
        
        
        ArrayList<WhatsNextEntry> entries = new ArrayList<WhatsNextEntry>();
        entries.add(entry1);
        entries.add(entry2);
        
        Capture<Interval> interval = new Capture<Interval>();
        expect(taskDAO.getAllIterationAndStoryTasks(EasyMock.eq(user), 
                EasyMock.and(EasyMock.capture(interval), EasyMock.isA(Interval.class)))).andReturn(tasks);

        expect(whatsNextEntryDAO.getWhatsNextEntriesFor(user)).andReturn(entries);
        
        replayAll();
        Collection<DailyWorkTaskTO> returned = testable.getAllCurrentTasksForUser(user);
        verifyAll();

        assertEquals(3, returned.size());
        
        int numberOfNext = 0;
        for (DailyWorkTaskTO to: returned) {
            if (to.getTaskClass() == DailyWorkTaskTO.TaskClass.NEXT) {
                numberOfNext ++;
            }
        }
        assertEquals(2, numberOfNext);
        
        assertTrue(interval.hasCaptured());
        Interval intervalValue = interval.getValue();
        assertNotNull(intervalValue);

        // race condition during midnight... ;) must contain today!
        assertTrue(intervalValue.containsNow());
        
        // duration of interval is exactly 24 hours
        assertEquals(new Duration(24*60*60*1000), intervalValue.toDuration());

        // and the milliseconds within the day must be 0 
        assertEquals(0, intervalValue.getStart().getMillisOfDay());
    }
}
