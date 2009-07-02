package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.PersonalLoadBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.TaskDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.User;
import fi.hut.soberit.agilefant.util.IntervalLoadContainer;
import static org.easymock.EasyMock.*;

public class PersonalLoadBusinessTest {

    private PersonalLoadBusinessImpl personalLoadBusiness;
    private UserBusiness userBusiness;
    private TaskDAO taskDAO;
    private StoryDAO storyDAO;
    private User user;
    
    
    @Before
    public void setUp() {
        personalLoadBusiness = new PersonalLoadBusinessImpl();
        userBusiness = createStrictMock(UserBusiness.class);
        taskDAO = createStrictMock(TaskDAO.class);
        storyDAO = createStrictMock(StoryDAO.class);
        personalLoadBusiness.setStoryDAO(storyDAO);
        personalLoadBusiness.setTaskDAO(taskDAO);
        personalLoadBusiness.setUserBusiness(userBusiness);
        user = new User();
        
    }
    private void replayAll() {
        replay(userBusiness, taskDAO, storyDAO);
    }
    private void verifyAll() {
        verify(userBusiness, taskDAO, storyDAO);
    }
    @Test
    public void testupdateUserLoadByInterval() {
        
    }
    @Test
    public void testupdateUserLoadByInterval_iterationStarts() {
       DateTime baseDate = new DateTime(2009,6,1,0,0,0,0);
       DateTime intervalStart = baseDate;
       DateTime intervalEnd = baseDate.plusDays(6);
       DateTime iterationStart = baseDate.plusDays(2);
       DateTime iterationEnd = baseDate.plusDays(9);
       
       //3.6 - 10.6
       Iteration iter = new Iteration();
       iter.setStartDate(iterationStart.toDate());
       iter.setEndDate(iterationEnd.toDate());
       
       //1.6 - 7.6
       IntervalLoadContainer container = new IntervalLoadContainer();
       Interval containerInterval = new Interval(intervalStart, intervalEnd);
       container.setInterval(containerInterval);
       
       //total intervals with vacations and weekends
       Interval actualInterval = new Interval(iterationStart, intervalEnd);
       Interval iterationInterval = new Interval(iterationStart, iterationEnd);
       //iteration and period durations without vacations and weekends
       Duration worktimeInIteration = new Duration(1000*3600*24*5L); //5 days
       Duration worktimeInPeriod = new Duration(1000*3600*24*3L); //3 days
       //total assigned effort
       long assignedEffort = 500L;
       
       expect(userBusiness.calculateWorktimePerPeriod(user, iterationInterval)).andReturn(worktimeInIteration);
       expect(userBusiness.calculateWorktimePerPeriod(user, actualInterval)).andReturn(worktimeInPeriod);

       replayAll();
       personalLoadBusiness.updateUserLoadByInterval(container, iter, user, assignedEffort);
       verifyAll();
       assertEquals(300, container.getAssignedLoad());
       
    }
    @Test
    public void testupdateUserLoadByInterval_iterationEnds() {
        DateTime baseDate = new DateTime(2009,6,1,0,0,0,0);
        DateTime intervalStart = baseDate.plusDays(7);
        DateTime intervalEnd = baseDate.plusDays(11);
        DateTime iterationStart = baseDate;
        DateTime iterationEnd = baseDate.plusDays(9);
        
        //1.6 - 9.6
        Iteration iter = new Iteration();
        iter.setStartDate(iterationStart.toDate());
        iter.setEndDate(iterationEnd.toDate());
        //8.6 - 12.6
        IntervalLoadContainer container = new IntervalLoadContainer();
        Interval containerInterval = new Interval(intervalStart, intervalEnd);
        container.setInterval(containerInterval);
        
        //total intervals with vacations and weekends
        Interval actualInterval = new Interval(intervalStart, iterationEnd);
        Interval iterationInterval = new Interval(iterationStart, iterationEnd);
        //iteration and period durations without vacations and weekends
        Duration worktimeInIteration = new Duration(1000*3600*24*5L); //5 days
        Duration worktimeInPeriod = new Duration(1000*3600*24*2L); //2 days
        //total assigned effort
        long assignedEffort = 500L;
        
        expect(userBusiness.calculateWorktimePerPeriod(user, iterationInterval)).andReturn(worktimeInIteration);
        expect(userBusiness.calculateWorktimePerPeriod(user, actualInterval)).andReturn(worktimeInPeriod);

        replayAll();
        personalLoadBusiness.updateUserLoadByInterval(container, iter, user, assignedEffort);
        verifyAll();
        assertEquals(200, container.getAssignedLoad());
    }
    @Test
    public void testupdateUserLoadByInterval_iterationInBetween() {
        DateTime baseDate = new DateTime(2009,6,1,0,0,0,0);
        DateTime intervalStart = baseDate;
        DateTime intervalEnd = baseDate.plusDays(4);
        DateTime iterationStart = baseDate.plusDays(1);
        DateTime iterationEnd = baseDate.plusDays(3);
        
        //2.6 - 4.6
        Iteration iter = new Iteration();
        iter.setStartDate(iterationStart.toDate());
        iter.setEndDate(iterationEnd.toDate());
        //1.6 - 5.6
        IntervalLoadContainer container = new IntervalLoadContainer();
        Interval containerInterval = new Interval(intervalStart, intervalEnd);
        container.setInterval(containerInterval);
        
        //total intervals with vacations and weekends
        Interval actualInterval = new Interval(iterationStart, iterationEnd);
        Interval iterationInterval = new Interval(iterationStart, iterationEnd);
        //iteration and period durations without vacations and weekends
        Duration worktimeInIteration = new Duration(1000*3600*24*3L); //3 days
        Duration worktimeInPeriod = new Duration(1000*3600*24*3L); //3 days
        //total assigned effort
        long assignedEffort = 500L;
        
        expect(userBusiness.calculateWorktimePerPeriod(user, iterationInterval)).andReturn(worktimeInIteration);
        expect(userBusiness.calculateWorktimePerPeriod(user, actualInterval)).andReturn(worktimeInPeriod);

        replayAll();
        personalLoadBusiness.updateUserLoadByInterval(container, iter, user, assignedEffort);
        verifyAll();
        assertEquals(500, container.getAssignedLoad());
    }
    @Test
    public void testupdateUserLoadByInterval_iterationNotOngoing() {
        DateTime baseDate = new DateTime(2009,6,1,0,0,0,0);
        DateTime intervalStart = baseDate;
        DateTime intervalEnd = baseDate.plusDays(4);
        DateTime iterationStart = baseDate.plusDays(7);
        DateTime iterationEnd = baseDate.plusDays(15);
        
        //1.6 - 4.6
        Iteration iter = new Iteration();
        iter.setStartDate(iterationStart.toDate());
        iter.setEndDate(iterationEnd.toDate());
        //8.6 - 16.6
        IntervalLoadContainer container = new IntervalLoadContainer();
        Interval containerInterval = new Interval(intervalStart, intervalEnd);
        container.setInterval(containerInterval);
        personalLoadBusiness.updateUserLoadByInterval(container, iter, user, 500L);
        assertEquals(0L, container.getAssignedLoad());


    }
}
