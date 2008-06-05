package fi.hut.soberit.agilefant.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.User;
import junit.framework.TestCase;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;



public class HourEntryBusinessTest extends TestCase {

    private HourEntryBusinessImpl hourEntryBusiness;  
    private BacklogItemHourEntryDAO bheDAO;
    private HourEntryDAO heDAO;
    private UserDAO userDAO;
    
    public void testGetEntriesByBacklogItem() {
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setBacklogItemHourEntryDAO(bheDAO);
        List<BacklogItemHourEntry> data = new ArrayList<BacklogItemHourEntry>();
        

        
        //set up backlog
        Backlog bl = new Iteration();
        
        //set up backlog items
        BacklogItem bli1 = new BacklogItem();
        bli1.setId(1);
        BacklogItem bli2 = new BacklogItem();
        bli2.setId(2);
        
        //set up hour report data
        BacklogItemHourEntry bhe1 = new BacklogItemHourEntry();
        bhe1.setTimeSpent(new AFTime(40));
        bhe1.setBacklogItem(bli1);
        BacklogItemHourEntry bhe2 = new BacklogItemHourEntry();
        bhe2.setTimeSpent(new AFTime(30));
        bhe2.setBacklogItem(bli1);
        BacklogItemHourEntry bhe3 = new BacklogItemHourEntry();
        bhe3.setTimeSpent(null);
        bhe3.setBacklogItem(bli2);
        
        data.add(bhe1);
        data.add(bhe2);
        data.add(bhe3);
        
        expect(bheDAO.getSumsByBacklog(bl)).andReturn(data);
        
        replay(bheDAO);
        
        try {
            Map<Integer, AFTime> sums = hourEntryBusiness.getSumsByBacklog(bl);
            //check correct sum
            assertEquals(sums.get(new Integer(1)).getTime(),70); 
            //check that null in effort spent is handled correctly
            assertEquals(sums.get(new Integer(2)).getTime(),0);
            verify(bheDAO);
        } catch(Exception e) {
            fail("HourEntryBusiness getSumpsByBacklogTest failed "+e.getMessage());
        }
    }

    public void testGetEffortSumByUserAndTimeInterval() {
        heDAO = createMock(HourEntryDAO.class);
        hourEntryBusiness = new HourEntryBusinessImpl();
        hourEntryBusiness.setHourEntryDAO(heDAO);
        User user = new User();
        String correctStartString = new String("2008-06-10 10:10");
        String falseStartString = new String("2008-13-10 10:10");
        String correctEndString = new String("2008-07-10 11:11");
        String falseEndString = new String("2008-07+10,13:24");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        AFTime timeSum = new AFTime(3600);
        AFTime timeRet;
        Date startDate;
        Date endDate;
        
        
        try {
            startDate = df.parse(correctStartString);
            endDate = df.parse(correctEndString);
            
            expect(heDAO.getEffortSumByUserAndTimeInterval(user, startDate, endDate))
                        .andReturn(timeSum).atLeastOnce();
            replay(heDAO);
            
            // Try with correct dates.
            timeRet = hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                    startDate, endDate);
            assertSame(timeSum, timeRet);
            
            // Try with correct strings.
            timeRet = hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                    correctStartString, correctEndString);
            assertSame(timeSum, timeRet);
            
            // Try with startDate being after endDate.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, endDate, startDate);
                fail("StartDate was after endDate, but no exception was thrown.");
            } catch (IllegalArgumentException iae) {
                
            }
            
            // Try the same as above, but with strings.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                        correctEndString, correctStartString);
                fail("StartDate was after endDate, but no exception was thrown. " +
                        "(Dates in string format)");
            } catch (IllegalArgumentException iae) {
                
            }
            
            // Try with starting string in wrong format.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                        falseStartString, correctEndString);
                fail("Starting date string was in wrong format, but no exception was thrown.");
            } catch (IllegalArgumentException iae) {
                
            }
            
            // Try with ending string in wrong format.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                        correctStartString, falseEndString);
                fail("Ending date string was in wrong format, but no exception was thrown.");
            } catch (IllegalArgumentException iae) {
                
            }
            
            // Try with both strings in wrong format.
            try {
                hourEntryBusiness.getEffortSumByUserAndTimeInterval(user, 
                        falseStartString, falseEndString);
                fail("Both date strings were in wrong format, but no exception was thrown.");
            } catch (IllegalArgumentException iae) {
                
            }
            
            verify(heDAO);
        } catch (ParseException pe) {
            fail("ParseException.");
        }
    }
    
}
