package fi.hut.soberit.agilefant.web;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.ActionSupport;

import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.AgilefantHistoryEntry;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class IterationHistoryActionTest extends MockedTestCase {

    @TestedBean
    private IterationHistoryAction iterationHistoryAction;
    @Mock
    private IterationBusiness iterationBusiness;
    
    @Test
    @DirtiesContext
    public void testExecute() {
        Iteration iter = new Iteration();
        List<AgilefantHistoryEntry> items = new ArrayList<AgilefantHistoryEntry>();
        
        expect(iterationBusiness.retrieve(1)).andReturn(iter);
        expect(iterationBusiness.retrieveChangesInIterationStories(iter)).andReturn(items);
        replayAll();
        iterationHistoryAction.setIterationId(1);
        assertEquals(ActionSupport.SUCCESS, iterationHistoryAction.execute());
        verifyAll();
        assertEquals(items, iterationHistoryAction.getStoryHistory());
    }
}
