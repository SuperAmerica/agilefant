package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;

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
    
    @Test
    public void testGetNumberOfChildren() {
        Backlog backlog = new Product();
        backlog.setId(5);
        
        User user = new User();
        ArrayList<Task> tasks = new ArrayList<Task>();
        tasks.addAll(Arrays.asList(new Task(), new Task()));

        Capture<Interval> interval = new Capture<Interval>();
        expect(taskDAO.getAllIterationAndStoryTasks(EasyMock.eq(user), 
                EasyMock.and(EasyMock.capture(interval), EasyMock.isA(Interval.class)))).andReturn(tasks);

        replayAll();
        Collection<Task> returned = testable.getDailyTasksForUser(user);               
        verifyAll();
        
        assertEquals(tasks, returned);
        assertTrue(interval.hasCaptured());
        Interval intervalValue = interval.getValue();
        assertNotNull(intervalValue);

        // race condition during midnight... ;)
        // must contain today!
        assertTrue(intervalValue.containsNow());
        
        // duration of interval is exactly 24 hours
        assertEquals(new Duration(24*60*60*1000), intervalValue.toDuration());

        // and the milliseconds within the day must be 0 
        assertEquals(0, intervalValue.getStart().getMillisOfDay());
    }
}
