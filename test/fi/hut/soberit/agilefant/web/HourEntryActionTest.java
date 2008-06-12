package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.HourEntryBusiness;
import fi.hut.soberit.agilefant.db.BacklogItemDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.HourEntry;
import fi.hut.soberit.agilefant.model.TimesheetLoggable;
import fi.hut.soberit.agilefant.model.User;
import junit.framework.TestCase;

public class HourEntryActionTest extends TestCase {
    private HourEntryAction hourEntryAction;
    private HourEntryBusiness hourEntryBusiness;
    private BacklogItemDAO backlogItemDAO;
    private UserDAO userDAO;
    private HourEntry hourEntry;
    private BacklogItem bli;
    private User user;
    
    public void setUp() {
        hourEntryBusiness = createMock(HourEntryBusiness.class);
        backlogItemDAO = createMock(BacklogItemDAO.class);
        userDAO = createMock(UserDAO.class);
        user = new User();
        user.setId(10);
        bli = new BacklogItem();
        bli.setId(2);
        hourEntry = new HourEntry();
        hourEntry.setId(1);
        hourEntryAction = new HourEntryAction();
        hourEntryAction.setHourEntryBusiness(hourEntryBusiness);
        hourEntryAction.setBacklogItemDAO(backlogItemDAO);
        hourEntryAction.setUserDAO(userDAO);
        hourEntryAction.setHourEntry(hourEntry);
        hourEntryAction.setHourEntryId(hourEntry.getId());
        hourEntryAction.setUserId(user.getId());
        hourEntryAction.setBacklogItemId(bli.getId());
    }
    
    public void testDelete() {
        expect(hourEntryBusiness.getHourEntryById(hourEntry.getId())).andReturn(null);
        
        // Start the test
        replay(hourEntryBusiness);
        
        assertEquals(Action.ERROR, hourEntryAction.delete());
        
        verify(hourEntryBusiness);
    }
    
    public void testEdit() {
        expect(hourEntryBusiness.getHourEntryById(hourEntry.getId())).andReturn(null);
        
        // Start the test
        replay(hourEntryBusiness);
        
        assertEquals(Action.ERROR, hourEntryAction.edit());
        
        verify(hourEntryBusiness);
    }
    
    public void testStore() {
        expect(hourEntryBusiness.getHourEntryById(hourEntry.getId())).andReturn(hourEntry);
        expect(hourEntryBusiness.store(EasyMock.isA(TimesheetLoggable.class),
                EasyMock.isA(HourEntry.class))).andReturn(hourEntry);
        expect(userDAO.get(user.getId())).andReturn(user);
        expect(backlogItemDAO.get(bli.getId())).andReturn(bli);
        
        // Start the test
        replay(hourEntryBusiness);
        replay(backlogItemDAO);
        replay(userDAO);
        
        assertEquals(Action.SUCCESS, hourEntryAction.store());
        
        verify(hourEntryBusiness);
        verify(backlogItemDAO);
        verify(userDAO);
    }
    
    public void testStoreWithBadHourEntryId() {
        expect(hourEntryBusiness.getHourEntryById(hourEntry.getId())).andReturn(null);
        
        // Start the test
        replay(hourEntryBusiness);
        
        assertEquals(Action.ERROR, hourEntryAction.store());
        
        verify(hourEntryBusiness);
    }
    
    public void testStoreWithBadHourEntryIdAndUserId() {
        hourEntryAction.setHourEntryId(0);
        hourEntryAction.setUserId(0);
        
        // Start the test
        assertEquals(Action.ERROR, hourEntryAction.store());
    }
    
    public HourEntryAction getHourEntryAction() {
        return hourEntryAction;
    }

    public void setHourEntryAction(HourEntryAction hourEntryAction) {
        this.hourEntryAction = hourEntryAction;
    }
    
}
