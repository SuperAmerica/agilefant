package fi.hut.soberit.agilefant.web;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

import fi.hut.soberit.agilefant.business.TransferObjectBusiness;
import fi.hut.soberit.agilefant.transfer.AutocompleteDataNode;

public class UserTeamAutocompleteActionTest {

    private UserTeamAutcompleteAction userTeamAutocompleteAction;
    private TransferObjectBusiness toBusiness;
    
    @Before
    public void setUp() {
        userTeamAutocompleteAction = new UserTeamAutcompleteAction();
        toBusiness = createMock(TransferObjectBusiness.class);
        userTeamAutocompleteAction.setTransferObjectBusiness(toBusiness);
    }

    @Test
    public void testExecute() {
        AutocompleteDataNode node = new AutocompleteDataNode(Class.class, 1, "");
        expect(toBusiness.constructUserAutocompleteData()).andReturn(Arrays.asList(node));
        expect(toBusiness.constructTeamAutocompleteData()).andReturn(Arrays.asList(node));
        
        replay(toBusiness);
        userTeamAutocompleteAction.execute();
        assertEquals(2, userTeamAutocompleteAction.getUserTeamData().size());
        verify(toBusiness);
    }
}
