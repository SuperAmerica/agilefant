package fi.hut.soberit.agilefant.web;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.impl.HourEntryBusinessImpl;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.model.HourEntry;
import junit.framework.TestCase;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

public class HourEntryActionTest extends TestCase {
    private HourEntryAction hourEntryAction = new HourEntryAction();
    private HourEntryDAO hourEntryDAO;
    
    
    /**
     * Edit Hour Entry 
     */
    
    public void testEdit(){
        hourEntryDAO = createMock(HourEntryDAO.class);
        hourEntryAction.setHourEntryDAO(hourEntryDAO);
        
        HourEntry entry = new HourEntry();
        entry.setId(144);
        expect(hourEntryDAO.get(144)).andReturn(entry);
        expect(hourEntryDAO.get(-42)).andReturn(null);
        replay(hourEntryDAO);
        
        //test valid
        hourEntryAction.setHourEntryId(entry.getId());
        assertEquals(Action.SUCCESS, hourEntryAction.edit());
        //test invalid
        hourEntryAction.setHourEntryId(-42);
        assertEquals(Action.ERROR, hourEntryAction.edit());
        
        verify(hourEntryDAO);
    }
    
    /**
     * A test for deleting a single existing and non-existing hour entry
     */
    public void testDelete(){
        hourEntryDAO = createMock(HourEntryDAO.class);
        hourEntryAction.setHourEntryDAO(hourEntryDAO);
        
        HourEntry entry = new HourEntry();
        entry.setId(666);
        expect(hourEntryDAO.get(666)).andReturn(entry);
        hourEntryDAO.remove(666);
        expect(hourEntryDAO.get(-40)).andReturn(null);
        replay(hourEntryDAO);
        
        //test valid
        hourEntryAction.setHourEntryId(entry.getId());
        assertEquals(Action.SUCCESS, hourEntryAction.delete());
        //test invalid
        hourEntryAction.setHourEntryId(-40);
        assertEquals(Action.ERROR, hourEntryAction.delete());
        verify(hourEntryDAO);
    }
    
    /**
     * A test for creating a new hour entry for editing
     */
    public void testCreate() {
        String result = hourEntryAction.create();
        assertEquals("create() was unsuccessful", result, Action.SUCCESS);
        super.assertEquals("New hour entry had an invalid id", 0, hourEntryAction.getHourEntryId());
    }
}
