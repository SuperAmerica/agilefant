package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;

public class AutocompleteActionTest {

    private AutocompleteAction autocompleteAction;
    private TransferObjectBusiness toBusiness;
    
    AutocompleteDataNode node;
    
    @Before
    public void setUp() {
        autocompleteAction = new AutocompleteAction();
        toBusiness = createMock(TransferObjectBusiness.class);
        autocompleteAction.setTransferObjectBusiness(toBusiness);
    }
    
    @Before
    public void setUp_data() {
        node = new AutocompleteDataNode(Class.class, 1, "");
    }

    private void replayAll() {
        replay(toBusiness);
    }
    
    private void verifyAll() {
        verify(toBusiness);
    }

    @Test
    public void testUserTeamData() {
        
        expect(toBusiness.constructUserAutocompleteData()).andReturn(Arrays.asList(node));
        expect(toBusiness.constructTeamAutocompleteData(true)).andReturn(Arrays.asList(node));
        
        replayAll();
        autocompleteAction.userTeamData();
        assertEquals(2, autocompleteAction.getAutocompleteData().size());
        verifyAll();
    }
    
    @Test
    public void testBacklogData() {
        List<AutocompleteDataNode> list = Arrays.asList(node, node);
        expect(toBusiness.constructBacklogAutocompleteData()).andReturn(list);
        
        replayAll();
        assertEquals(Action.SUCCESS, autocompleteAction.backlogData());
        verifyAll();
        
        assertEquals(list, autocompleteAction.getAutocompleteData());
    }
    
    @Test
    public void testProductData() {
        List<AutocompleteDataNode> list = Arrays.asList(node, node);
        expect(toBusiness.constructProductAutocompleteData()).andReturn(list);
        
        replayAll();
        assertEquals(Action.SUCCESS, autocompleteAction.productData());
        verifyAll();
        
        assertEquals(list, autocompleteAction.getAutocompleteData());
    }
    
    @Test
    public void testProjectData() {
        List<AutocompleteDataNode> list = Arrays.asList(node, node);
        expect(toBusiness.constructProjectAutocompleteData()).andReturn(list);
        
        replayAll();
        assertEquals(Action.SUCCESS, autocompleteAction.projectData());
        verifyAll();
        
        assertEquals(list, autocompleteAction.getAutocompleteData());
    }
    
    @Test
    public void testTeamData() {
        expect(toBusiness.constructTeamAutocompleteData(false)).andReturn(Arrays.asList(node));
        
        replayAll();
        autocompleteAction.teamData();
        assertEquals(1, autocompleteAction.getAutocompleteData().size());
        verifyAll();
    }

    @Test
    public void testCurrentIterationData() {
        List<AutocompleteDataNode> list = Arrays.asList(node, node);
        expect(toBusiness.constructCurrentIterationAutocompleteData()).andReturn(list);
        
        replayAll();
        assertEquals(Action.SUCCESS, autocompleteAction.currentIterationData());
        verifyAll();
        
        assertEquals(list, autocompleteAction.getAutocompleteData());
    }
}
