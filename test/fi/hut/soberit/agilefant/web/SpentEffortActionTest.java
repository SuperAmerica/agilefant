package fi.hut.soberit.agilefant.web;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collections;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.util.DailySpentEffort;
public class SpentEffortActionTest {

    private SpentEffortAction testable;
    private HourEntryBusiness heBusiness;
    
    @Before
    public void setUp() {
        testable = new SpentEffortAction();
        heBusiness = createMock(HourEntryBusiness.class);
        testable.setHourEntryBusiness(heBusiness);
    }
    @Test
    public void testInitializeWeekSelection() {
        DateTime middle = new DateTime(2009,6,1,0,0,0,0);
        final int firstWeek = 23 - SpentEffortAction.WEEKS_IN_WEEK_SELECTION/2;
        final int lastWeek = 23 + SpentEffortAction.WEEKS_IN_WEEK_SELECTION/2;
        
        assertEquals(0, testable.getWeeks().size());
        
        testable.initializeWeekSelection(middle);
        List<LocalDate> actual = testable.getWeeks();
        assertNotNull(actual);
        assertEquals(SpentEffortAction.WEEKS_IN_WEEK_SELECTION, actual.size());
        assertEquals(firstWeek, actual.get(0).getWeekOfWeekyear());
        assertEquals(lastWeek, actual.get(SpentEffortAction.WEEKS_IN_WEEK_SELECTION-1).getWeekOfWeekyear());
        
    }
    @Test
    public void getSelectedDate() {
        final int initWeek = 23;
        final int initYear = 2009;
        testable.setWeek(initWeek);
        testable.setYear(initYear);
        DateTime actual = testable.getSelectedDate();
        assertEquals(initYear, actual.getYear());
        assertEquals(initWeek, actual.getWeekOfWeekyear());
        assertEquals(initWeek + 1, testable.getNextWeek().getWeekOfWeekyear());
        assertEquals(initYear, testable.getNextWeek().getYear());
        assertEquals(initWeek - 1, testable.getPrevWeek().getWeekOfWeekyear());
        assertEquals(initYear, testable.getPrevWeek().getYear());
        
    }
    
    @Test
    public void getSelectedDate_currentDate() {
        LocalDate current = new LocalDate();
        int currentWeek = current.getWeekOfWeekyear();
        int currentYear = current.getYear();
        DateTime actual = testable.getSelectedDate();
        assertEquals(currentYear, actual.getYear());
        assertEquals(currentWeek, actual.getWeekOfWeekyear());
        assertEquals(currentWeek + 1, testable.getNextWeek().getWeekOfWeekyear());
        assertEquals(currentYear, testable.getNextWeek().getYear());
        assertEquals(currentWeek - 1, testable.getPrevWeek().getWeekOfWeekyear());
        assertEquals(currentYear, testable.getPrevWeek().getYear());
    }
    
    @Test
    public void testGetDaySumsByWeek() {
        DateTime startDate = new DateTime(2009,6,1,0,0,0,0);
        List<DailySpentEffort> entries = Collections.emptyList();
        
        expect(heBusiness.getDailySpentEffortByWeek(startDate.toLocalDate(), 11)).andReturn(entries);
        expect(heBusiness.calculateWeekSum(startDate.toLocalDate(), 11)).andReturn(0L);
        replay(heBusiness);
        testable.setYear(2009);
        testable.setWeek(23);
        testable.setUserId(11);
        assertEquals(Action.SUCCESS, testable.getDaySumsByWeek());
        //check that initializeWeekSelection and getSelectedDate have been called
        assertEquals(24, testable.getNextWeek().getWeekOfWeekyear());
        assertEquals(22, testable.getPrevWeek().getWeekOfWeekyear());
        assertEquals(SpentEffortAction.WEEKS_IN_WEEK_SELECTION, testable.getWeeks().size());
        verify(heBusiness);
    }
    @Test
    public void testGetHourEntriesByUserAndDay() {
        testable.setUserId(42);
        testable.setYear(2009);
        testable.setDay(153);
        LocalDate day = new DateTime(2009,6,2,0,0,0,0).toLocalDate();
        expect(heBusiness.getEntriesByUserAndDay(day, 42)).andReturn(null);
        replay(heBusiness);
        assertEquals(Action.SUCCESS, testable.getHourEntriesByUserAndDay());
        assertEquals(null, testable.getEffortEntries());
        verify(heBusiness);
    }
}
