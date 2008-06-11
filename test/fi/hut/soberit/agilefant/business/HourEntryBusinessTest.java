package fi.hut.soberit.agilefant.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.BacklogHourEntryDAO;
import fi.hut.soberit.agilefant.db.BacklogItemHourEntryDAO;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.AFTime;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.BacklogHourEntry;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.BacklogItemHourEntry;
import fi.hut.soberit.agilefant.model.HourEntry;
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
    private BacklogHourEntryDAO blheDAO;
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
    private void compareHe(HourEntry he1, HourEntry he2) 
            throws Exception {
        if(he1.getUser().getId() != he2.getUser().getId()) {
            throw new Exception("Users not equal.");
        }
        if(!he1.getDate().equals(he2.getDate())) {
            throw new Exception("Dates not equal.");
        }
        if(!he1.getDescription().equals(he2.getDescription())) {
            throw new Exception("Descriptions not equal.");
        }
        if(!he1.getTimeSpent().equals(he2.getTimeSpent())) {
            throw new Exception("Time spent not equal.");
        }
    }
    public void testStore()
    {
        hourEntryBusiness = new HourEntryBusinessImpl();
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        blheDAO = createMock(BacklogHourEntryDAO.class);
        hourEntryBusiness.setBacklogHourEntryDAO(blheDAO);
        hourEntryBusiness.setBacklogItemHourEntryDAO(bheDAO);
        
        Backlog bl = new Iteration();
        BacklogItem bli = new BacklogItem();
        HourEntry he = new HourEntry();
        BacklogItemHourEntry blihe = new BacklogItemHourEntry();
        BacklogHourEntry blhe = new BacklogHourEntry();
        User u = new User();
        u.setId(1);
        he.setUser(u);
        he.setDate(new Date());
        he.setDescription("test");
        he.setTimeSpent(new AFTime(2));
        blhe.setId(1);
        blihe.setId(1);
        
        he.setId(1);
        expect(bheDAO.get(1)).andReturn(blihe).times(1);
        expect(blheDAO.get(1)).andReturn(blhe).times(1);
        bheDAO.store(blihe);
        blheDAO.store(blhe);
        replay(blheDAO);
        replay(bheDAO);
        //store under BLI
        hourEntryBusiness.store(bli, he);
        try {
            compareHe(he,blihe);
        } catch(Exception e) {
            fail("Hour entry data update failed!");
        }
        //store under BL
        hourEntryBusiness.store(bl, he);
        try {
            compareHe(he,blhe);
        } catch(Exception e) {
            fail("Hour entry data update failed!");
        }
        //store under null
        try {
            hourEntryBusiness.store(null, he);
            fail("Exception expected when storing under invalid parent.");
        } catch(IllegalArgumentException iae) { }
        //store null entry
        try {
            hourEntryBusiness.store(bl, null);
            fail("Exception expected when storing null entry.");
        } catch(IllegalArgumentException iae) { }
        
        verify(bheDAO);
        verify(blheDAO);
    }

    public void testRemoveHourEntriesByParent()
    {
        hourEntryBusiness = new HourEntryBusinessImpl();
        heDAO = createMock(HourEntryDAO.class);
        hourEntryBusiness.setHourEntryDAO(heDAO);
        bheDAO = createMock(BacklogItemHourEntryDAO.class);
        blheDAO = createMock(BacklogHourEntryDAO.class);
        hourEntryBusiness.setBacklogHourEntryDAO(blheDAO);
        hourEntryBusiness.setBacklogItemHourEntryDAO(bheDAO);
        
        BacklogItemHourEntry blihe1 = new BacklogItemHourEntry();
        BacklogItemHourEntry blihe2 = new BacklogItemHourEntry();
        BacklogHourEntry blhe1 = new BacklogHourEntry();
        BacklogHourEntry blhe2 = new BacklogHourEntry();
        Backlog bl1 = new Iteration();
        Backlog bl2 = new Iteration();
        BacklogItem bli = new BacklogItem();
        List<BacklogHourEntry> blheList = new ArrayList<BacklogHourEntry>();
        List<BacklogItemHourEntry> bliheList = new ArrayList<BacklogItemHourEntry>();
  
        blihe1.setBacklogItem(bli);
        blihe1.setId(1);
        blihe2.setBacklogItem(bli);
        blihe2.setId(2);
        blhe1.setBacklog(bl1);
        blhe1.setId(3);
        blhe2.setBacklog(bl1);
        blhe2.setId(4);    
        
        blheList.add(blhe1);
        blheList.add(blhe2);
        bliheList.add(blihe1);
        bliheList.add(blihe2);
        
        expect(bheDAO.getEntriesByBacklogItem(bli)).andReturn(bliheList).times(1);
        expect(blheDAO.getEntriesByBacklog(bl1)).andReturn(blheList).times(1);
        expect(blheDAO.getEntriesByBacklog(bl2)).andReturn(null).times(1);
        heDAO.remove(1);
        heDAO.remove(2);
        heDAO.remove(3);
        heDAO.remove(4);
        replay(bheDAO);
        replay(blheDAO);
        replay(heDAO);
        //remove under BLI that has HEs
        hourEntryBusiness.removeHourEntriesByParent(bli);
        //remove under BL that has HEs
        hourEntryBusiness.removeHourEntriesByParent(bl1);
        //remove under BL that has no HEs
        hourEntryBusiness.removeHourEntriesByParent(bl2);
        //remove under null
        hourEntryBusiness.removeHourEntriesByParent(null);
        verify(bheDAO);
        verify(blheDAO);
        verify(heDAO);
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
