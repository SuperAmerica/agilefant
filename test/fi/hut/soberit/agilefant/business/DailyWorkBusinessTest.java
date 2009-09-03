package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.DailyWorkBusinessImpl;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;

public class DailyWorkBusinessTest {
    private DailyWorkBusiness testable;
    
    private TaskDAO taskDAO;
    
    @Before
    public void setUp() {
        testable = new DailyWorkBusinessImpl();
        
        taskDAO = createMock(TaskDAO.class);
        testable.setTaskDAO(taskDAO);
    }

    private void replayAll() {
        replay(taskDAO);
    }
    
    private void verifyAll() {
        verify(taskDAO);
    }
    
    private void assertTOListEquals(Collection<Task> tasks, Collection<DailyWorkTaskTO> returned, DailyWorkTaskTO.TaskClass type) {
        assertEquals(tasks.size(), returned.size());
        Iterator<DailyWorkTaskTO> retIt = returned.iterator();
        Iterator<Task> origIt = tasks.iterator();
        
        while (origIt.hasNext() || retIt.hasNext()) {
            DailyWorkTaskTO nextRet =  retIt.next();
            assertEquals(origIt.next().getId(), nextRet.getId());
            assertEquals(nextRet.getTaskClass(), type);
        }
    }
    
    @Test
    public void test_getDailyTasksForUser() {
        Backlog backlog = new Product();
        backlog.setId(5);
        
        User user = new User();
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.addAll(Arrays.asList(new Task(), new Task()));

        Capture<Interval> interval = new Capture<Interval>();
        expect(taskDAO.getAllIterationAndStoryTasks(EasyMock.eq(user), 
                EasyMock.and(EasyMock.capture(interval), EasyMock.isA(Interval.class)))).andReturn(tasks);

//        expect(taskDAO.getAllIterationAndStoryTasks(EasyMock.eq(user), 
//                EasyMock.and(EasyMock.capture(interval), EasyMock.isA(Interval.class)))).andReturn(tasks);

        replayAll();
        Collection<DailyWorkTaskTO> returned = testable.getAllCurrentTasksForUser(user);
        verifyAll();

        // assertTOListEquals(tasks, returned, DailyWorkTaskTO.TaskClass.CURRENT);
        
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
