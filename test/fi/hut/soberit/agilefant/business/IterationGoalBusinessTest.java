package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;

import junit.framework.TestCase;
import fi.hut.soberit.agilefant.business.impl.IterationGoalBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.exception.ObjectNotFoundException;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;


/**
 * A spring test case for testing the IterationGoal business layer.
 * 
 * @author rjokelai
 * 
 */

public class IterationGoalBusinessTest extends TestCase {

    private IterationGoalBusinessImpl iterationGoalBusiness = new IterationGoalBusinessImpl();
    private IterationGoalDAO iterationGoalDAO;
    
    /**
     * Test the getNewPriorityNumber method with no existing
     * iteration goals in the iteration.
     */
    public void testGetNewPriorityNumber_noIterationGoals() {
        iterationGoalDAO = createMock(IterationGoalDAO.class);
        iterationGoalBusiness.setIterationGoalDAO(iterationGoalDAO);
        
        // Test data
        Iteration iteration = new Iteration();
        iteration.setIterationGoals(new ArrayList<IterationGoal>());
        
        // Record expected behavior
        expect(iterationGoalDAO.getLowestRankedIterationGoalInIteration(iteration)).andReturn(null);
        
        // The test
        replay(iterationGoalDAO);
        
        int newNumber = iterationGoalBusiness.getNewPriorityNumber(iteration);
        
        assertEquals(1, newNumber);
        
        verify(iterationGoalDAO);
    }
    
    /**
     * Test the getNewPriorityNumber method with two existing
     * iteration goals in the iteration.
     */
    public void testGetNewPriorityNumber_existingIterationGoals() {
        iterationGoalDAO = createMock(IterationGoalDAO.class);
        iterationGoalBusiness.setIterationGoalDAO(iterationGoalDAO);
        
        // Test data
        Iteration iteration = new Iteration();
        iteration.setIterationGoals(new ArrayList<IterationGoal>());
        
        IterationGoal ig1 = new IterationGoal();
        ig1.setPriority(1);
        
        IterationGoal ig2 = new IterationGoal();
        ig2.setPriority(475);
        
        // Record expected behavior
        expect(iterationGoalDAO.getLowestRankedIterationGoalInIteration(iteration)).andReturn(ig2);
        
        // The test
        replay(iterationGoalDAO);
        
        int newNumber = iterationGoalBusiness.getNewPriorityNumber(iteration);
        
        assertEquals((ig2.getPriority() + 1), newNumber);
        
        verify(iterationGoalDAO);
    }
    
    public void testIterationGoalToJSON_noSuchGoal() {
        iterationGoalDAO = createMock(IterationGoalDAO.class);
        iterationGoalBusiness.setIterationGoalDAO(iterationGoalDAO);
        
        expect(iterationGoalDAO.get(123)).andReturn(null);
        replay(iterationGoalDAO);
        try {
            iterationGoalBusiness.iterationGoalToJSON(123);
            fail();
        }
        catch (ObjectNotFoundException onfe) {
        }
        
        verify(iterationGoalDAO);
    }
}
