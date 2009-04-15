package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;

import fi.hut.soberit.agilefant.business.impl.IterationGoalBusinessImpl;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.IterationGoalDAO;
import fi.hut.soberit.agilefant.model.BacklogItem;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.IterationGoal;


/**
 * A spring test case for testing the IterationGoal business layer.
 * 
 * @author Pasi Pekkanen
 * 
 */

public class IterationGoalBusinessTest extends TestCase {

    private IterationGoalBusinessImpl iterationGoalBusiness = new IterationGoalBusinessImpl();
    private BacklogItemBusiness backlogItemBusiness;
    private IterationGoalDAO iterationGoalDAO;
    private IterationDAO iterationDAO;
    
   @Before
   public void setUp() {
       iterationGoalDAO = createMock(IterationGoalDAO.class);
       iterationDAO = createMock(IterationDAO.class);
       backlogItemBusiness = createMock(BacklogItemBusiness.class);
       iterationGoalBusiness.setIterationGoalDAO(iterationGoalDAO);
       iterationGoalBusiness.setIterationDAO(iterationDAO);     
       iterationGoalBusiness.setBacklogItemBusiness(backlogItemBusiness);
   }
   
   public void testAttachGoalToIteration_update() {
       Iteration oldIteration = new Iteration();
       Iteration newIteration = new Iteration();
       IterationGoal updateMe = new IterationGoal();
       BacklogItem inOldIteration = new BacklogItem();
      
       inOldIteration.setIterationGoal(updateMe);
       updateMe.getBacklogItems().add(inOldIteration);
       
       updateMe.setIteration(oldIteration);
       oldIteration.getIterationGoals().add(updateMe);
       
       expect(iterationDAO.get(1)).andReturn(newIteration).once();
       backlogItemBusiness.setBacklogItemIterationGoal(inOldIteration, null);
       
       replay(iterationDAO);
       replay(backlogItemBusiness);
       try {
           iterationGoalBusiness.attachGoalToIteration(updateMe, 1);
       } catch (Exception e) {
           fail();
       }
       assertEquals(newIteration, updateMe.getIteration());
       assertEquals(0, updateMe.getBacklogItems().size());
       assertFalse(oldIteration.getIterationGoals().contains(updateMe));
       assertTrue(newIteration.getIterationGoals().contains(updateMe));
       verify(iterationDAO);
       verify(backlogItemBusiness);
       
   }
   
   public void testAttachGoalToIteration_noUpdate() {
       Iteration oldIteration = new Iteration();
       IterationGoal updateMe = new IterationGoal();
       BacklogItem inOldIteration = new BacklogItem();
      
       inOldIteration.setIterationGoal(updateMe);
       updateMe.getBacklogItems().add(inOldIteration);
       
       updateMe.setIteration(oldIteration);
       oldIteration.getIterationGoals().add(updateMe);
       
       try {
           iterationGoalBusiness.attachGoalToIteration(updateMe, 0);
       } catch (Exception e) {
           fail();
       }
       assertEquals(oldIteration, updateMe.getIteration());
       assertEquals(1, updateMe.getBacklogItems().size());
       assertTrue(oldIteration.getIterationGoals().contains(updateMe));

       
   }
   
   public void testRemove() {
       Iteration iteration = new Iteration();
       BacklogItem bli1 = new BacklogItem();
       BacklogItem bli2 = new BacklogItem();
       IterationGoal removeMe = new IterationGoal();
       
       removeMe.getBacklogItems().add(bli2);
       removeMe.getBacklogItems().add(bli1);
       removeMe.setIteration(iteration);
       
       bli1.setIterationGoal(removeMe);
       bli2.setIterationGoal(removeMe);
       iteration.getIterationGoals().add(removeMe);
       
       backlogItemBusiness.setBacklogItemIterationGoal(bli1, null);
       backlogItemBusiness.setBacklogItemIterationGoal(bli2, null);
       iterationGoalDAO.remove(1);
       expect(iterationGoalDAO.get(1)).andReturn(removeMe).once();
       
       replay(iterationGoalDAO);
       replay(backlogItemBusiness);
       try {
           iterationGoalBusiness.remove(1);
       } catch (Exception e) {
           fail();
       }
       assertFalse(iteration.getIterationGoals().contains(removeMe));
       
       verify(iterationGoalDAO);
       verify(backlogItemBusiness);
   }
   private List<IterationGoal> createUpdatePrioData(Iteration iter) {
       List<IterationGoal> goals = new ArrayList<IterationGoal>();
       for(int i = 0 ; i < 6; i++) {
           IterationGoal tmp = new IterationGoal();
           tmp.setPriority(i);
           tmp.setIteration(iter);
           goals.add(tmp);
           iter.getIterationGoals().add(tmp);
       }
       return goals;
   }
   public void testUpdateIterationGoalPriority_noUpdate() {
       Iteration iter = new Iteration();
       List<IterationGoal> goals = createUpdatePrioData(iter);
       iterationGoalBusiness.updateIterationGoalPriority(goals.get(4), 4);
   }
   
   public void testUpdateIterationGoalPriority_oneUp() {
       Iteration iter = new Iteration();
       List<IterationGoal> goals = createUpdatePrioData(iter);
       
       iterationGoalDAO.store(goals.get(3));
       iterationGoalDAO.store(goals.get(4));
       replay(iterationGoalDAO);
       iterationGoalBusiness.updateIterationGoalPriority(goals.get(4), 3);
       assertSame(0, goals.get(0).getPriority());
       assertSame(1, goals.get(1).getPriority());
       assertSame(2, goals.get(2).getPriority());
       assertSame(4, goals.get(3).getPriority());
       assertSame(3, goals.get(4).getPriority());
       assertSame(5, goals.get(5).getPriority());
       verify(iterationGoalDAO);
   }
   
   public void testUpdateIterationGoalPriority_oneDown() {
       Iteration iter = new Iteration();
       List<IterationGoal> goals = createUpdatePrioData(iter);
       
       iterationGoalDAO.store(goals.get(4));
       iterationGoalDAO.store(goals.get(5));
       replay(iterationGoalDAO);
       iterationGoalBusiness.updateIterationGoalPriority(goals.get(4), 5);
       assertSame(0, goals.get(0).getPriority());
       assertSame(1, goals.get(1).getPriority());
       assertSame(2, goals.get(2).getPriority());
       assertSame(3, goals.get(3).getPriority());
       assertSame(5, goals.get(4).getPriority());
       assertSame(4, goals.get(5).getPriority());
       verify(iterationGoalDAO);
   }
   
   public void testUpdateIterationGoalPriority_toTop() {
       Iteration iter = new Iteration();
       List<IterationGoal> goals = createUpdatePrioData(iter);

       iterationGoalDAO.store(goals.get(0));
       iterationGoalDAO.store(goals.get(1));
       iterationGoalDAO.store(goals.get(2));
       iterationGoalDAO.store(goals.get(3));
       iterationGoalDAO.store(goals.get(4));
       iterationGoalDAO.store(goals.get(5));
       replay(iterationGoalDAO);
       iterationGoalBusiness.updateIterationGoalPriority(goals.get(5), 0);
       assertSame(1, goals.get(0).getPriority());
       assertSame(2, goals.get(1).getPriority());
       assertSame(3, goals.get(2).getPriority());
       assertSame(4, goals.get(3).getPriority());
       assertSame(5, goals.get(4).getPriority());
       assertSame(0, goals.get(5).getPriority());
       verify(iterationGoalDAO);
   }
   
   public void testUpdateIterationGoalPriority_insertNew() {
       Iteration iter = new Iteration();
       List<IterationGoal> goals = createUpdatePrioData(iter);
       IterationGoal newGoal = new IterationGoal();
       newGoal.setIteration(iter);
       newGoal.setPriority(-1);
       
       iterationGoalDAO.store(goals.get(3));
       iterationGoalDAO.store(goals.get(4));
       iterationGoalDAO.store(goals.get(5));
       iterationGoalDAO.store(newGoal);
       replay(iterationGoalDAO);
       iterationGoalBusiness.updateIterationGoalPriority(newGoal, 3);
       assertSame(0, goals.get(0).getPriority());
       assertSame(1, goals.get(1).getPriority());
       assertSame(2, goals.get(2).getPriority());
       assertSame(4, goals.get(3).getPriority());
       assertSame(5, goals.get(4).getPriority());
       assertSame(6, goals.get(5).getPriority());
       assertSame(3, newGoal.getPriority());
       verify(iterationGoalDAO);
   }
}
