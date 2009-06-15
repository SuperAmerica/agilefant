package fi.hut.soberit.agilefant.web;

import org.junit.*;

import com.opensymphony.xwork.Action;

import fi.hut.soberit.agilefant.business.IterationBurndownBusiness;
import fi.hut.soberit.agilefant.business.IterationBusiness;
import fi.hut.soberit.agilefant.model.Iteration;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

public class ChartActionTest {

    ChartAction chartAction = new ChartAction();
    IterationBurndownBusiness iterationBurndownBusiness;
    IterationBusiness iterationBusiness;
    Iteration iteration;

    @Before
    public void setUp() {
        iterationBurndownBusiness = createMock(IterationBurndownBusiness.class);
        chartAction.setIterationBurndownBusiness(iterationBurndownBusiness);

        iterationBusiness = createMock(IterationBusiness.class);
        chartAction.setIterationBusiness(iterationBusiness);
        
        iteration = new Iteration();
        iteration.setId(100);
    }

    @Test
    public void testGetIterationBurndown() {
        byte[] expected = new byte[100];

        chartAction.setBacklogId(iteration.getId());
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(
                iteration);
        expect(iterationBurndownBusiness.getIterationBurndown(iteration))
                .andReturn(expected);
        replay(iterationBusiness, iterationBurndownBusiness);

        assertEquals(Action.SUCCESS, chartAction.getIterationBurndown());
        assertEquals(expected, chartAction.getResult());
        
        
        verify(iterationBusiness, iterationBurndownBusiness);
    }

    @Test
    public void testGetSmallIterationBurndown() {
        byte[] expected = new byte[100];

        chartAction.setBacklogId(iteration.getId());
        expect(iterationBusiness.retrieve(iteration.getId())).andReturn(
                iteration);
        expect(iterationBurndownBusiness.getSmallIterationBurndown(iteration))
                .andReturn(expected);
        replay(iterationBusiness, iterationBurndownBusiness);

        assertEquals(Action.SUCCESS, chartAction.getSmallIterationBurndown());
        assertEquals(expected, chartAction.getResult());
        
        
        verify(iterationBusiness, iterationBurndownBusiness);    }
}
