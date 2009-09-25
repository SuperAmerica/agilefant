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
import org.springframework.transaction.annotation.Transactional;

import fi.hut.soberit.agilefant.business.impl.DailyWorkBusinessImpl;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.db.WhatsNextEntryDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Rankable;
import fi.hut.soberit.agilefant.model.Task;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.model.WhatsNextEntry;
import fi.hut.soberit.agilefant.transfer.DailyWorkTaskTO;

public class DailyWorkBusinessTest {
    private DailyWorkBusiness testable;
    
    private TaskDAO taskDAO;
    private WhatsNextEntryDAO whatsNextEntryDAO;

    private RankingBusiness rankingBusiness;
    private TaskBusiness taskBusiness;

    private Task task1;

    private Task task3;

    private Task task2;


    private User user;

    private Product backlog;

    private WhatsNextEntry whatsNextEntry_forTask1AndUser;

    private WhatsNextEntry whatsNextEntry_forTask2AndUser;
    
    @Before
    public void setUp() {
        testable = new DailyWorkBusinessImpl();
        
        taskDAO = createMock(TaskDAO.class);
        testable.setTaskDAO(taskDAO);
        
        whatsNextEntryDAO = createMock(WhatsNextEntryDAO.class);
        testable.setWhatsNextEntryDAO(whatsNextEntryDAO);
        
        rankingBusiness = createMock(RankingBusiness.class);
        testable.setRankingBusiness(rankingBusiness);
        
        taskBusiness = createMock(TaskBusiness.class);
        testable.setTaskBusiness(taskBusiness);
        
        backlog = new Product();
        backlog.setId(5);
        
        user = new User();
        
        task1 = new Task();
        task1.setId(1);
        task2 = new Task();
        task2.setId(2);
        task3 = new Task();
        task3.setId(3);

        whatsNextEntry_forTask1AndUser = new WhatsNextEntry();
        whatsNextEntry_forTask1AndUser.setUser(user);
        whatsNextEntry_forTask1AndUser.setTask(task1);

        whatsNextEntry_forTask2AndUser = new WhatsNextEntry();
        whatsNextEntry_forTask2AndUser.setUser(user);
        whatsNextEntry_forTask2AndUser.setTask(task2);
    }

    private void replayAll() {
        replay(taskDAO, whatsNextEntryDAO, rankingBusiness, taskBusiness);
    }
    
    private void verifyAll() {
        verify(taskDAO, whatsNextEntryDAO, rankingBusiness, taskBusiness);
    }

    @Test
    public void testGetAllCurrentTasksForUser() {
        ArrayList<Task> tasks = new ArrayList<Task>();
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
    
    @Test
    public void testRankToBottomOnWhatsNext() {
        expect(whatsNextEntryDAO.getWhatsNextEntryFor(user, task1)).andReturn(whatsNextEntry_forTask1AndUser);
        expect(whatsNextEntryDAO.getLastTaskInRank(user)).andReturn(whatsNextEntry_forTask2AndUser);
        rankingBusiness.rankToBottom(whatsNextEntry_forTask1AndUser, whatsNextEntry_forTask2AndUser);
        replayAll();
        
        testable.rankToBottomOnWhatsNext(user, task1);
        verifyAll();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testRankToBottom_noEntry() {
        expect(whatsNextEntryDAO.getWhatsNextEntryFor(user, task1)).andReturn(null);
        
        replayAll();

        testable.rankToBottomOnWhatsNext(user, task1);
        verifyAll();
    }
    
    @Test
    public void testRankUnder_addEntry() {
        expect(whatsNextEntryDAO.getWhatsNextEntryFor(user, task1)).andReturn(null);

        Capture<WhatsNextEntry> entryCapture    = new Capture<WhatsNextEntry>();
        Capture<WhatsNextEntry> entryCapture2   = new Capture<WhatsNextEntry>();
        Capture<RankUnderDelegate> delegateCapture = new Capture<RankUnderDelegate>();

        whatsNextEntryDAO.store(and(isA(WhatsNextEntry.class), EasyMock.capture(entryCapture)));
        taskBusiness.addResponsible(task1, user);
        expect(whatsNextEntryDAO.getLastTaskInRank(user)).andReturn(whatsNextEntry_forTask2AndUser);
        expect(whatsNextEntryDAO.getWhatsNextEntryFor(user, task2)).andReturn(null);
        
        rankingBusiness.rankToBottom(and(isA(WhatsNextEntry.class), 
            EasyMock.capture(entryCapture2)), 
            eq(whatsNextEntry_forTask2AndUser)
        );
        
        rankingBusiness.rankUnder(
            isA(Rankable.class), 
            eq((Rankable)null),
            and(isA(RankUnderDelegate.class), capture(delegateCapture))
        );
        
        // from delegate:
        expect(whatsNextEntryDAO.getTasksWithRankBetween(1, 2, user)).andReturn(new ArrayList());
        
        replayAll();

        testable.rankUnderTaskOnWhatsNext(user, task1, task2);

        entryCapture.hasCaptured();
        entryCapture2.hasCaptured();
        delegateCapture.hasCaptured();
       
        delegateCapture.getValue().getWithRankBetween(1, 2);
        
        verifyAll();
    }
    
    @Test
    public void testRemoveFromWhatsNext() {
        expect(whatsNextEntryDAO.getWhatsNextEntryFor(user, task1)).andReturn(whatsNextEntry_forTask1AndUser);
        whatsNextEntryDAO.remove(whatsNextEntry_forTask1AndUser);

        replayAll();
        testable.removeFromWhatsNext(user, task1);
        
        verifyAll();
    }
    
    @Test
    public void testRemoveTaskFromWorkQueues() {
        whatsNextEntryDAO.removeAllByTask(task1);
        
        replayAll();
        testable.removeTaskFromWorkQueues(task1);
        verifyAll();
    }
}
