package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.StoryBusinessImpl;
import fi.hut.soberit.agilefant.db.HourEntryDAO;
import fi.hut.soberit.agilefant.db.IterationDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.db.UserDAO;
import fi.hut.soberit.agilefant.db.history.StoryHistoryDAO;
import fi.hut.soberit.agilefant.exception.OperationNotPermittedException;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
@SuppressWarnings("unused")
public class StoryBusinessMoveStoryTest extends MockedTestCase {

    @TestedBean
    private StoryBusinessImpl storyBusiness;

    @Mock
    private IterationHistoryEntryBusiness iterationHistoryBusiness;
    @Mock
    private BacklogHistoryEntryBusiness backlogHistoryEntryBusiness;
    @Mock(strict = true)
    private StoryDAO storyDAO;
    @Mock
    private BacklogBusiness backlogBusiness;
    @Mock
    private IterationDAO iterationDAO;
    @Mock
    private UserDAO userDAO;
    @Mock
    private HourEntryDAO hourEntryDAO;
    @Mock
    private ProjectBusiness projectBusiness;
    @Mock
    private StoryHistoryDAO storyHistoryDAO;
    @Mock
    private StoryRankBusiness storyRankBusiness;
    @Mock
    private TransferObjectBusiness transferObjectBusiness;
    @Mock
    private HourEntryBusiness hourEntryBusiness;
    @Mock
    private TaskBusiness taskBusiness;
    @Mock
    private StoryHierarchyBusiness storyHierarchyBusiness;
    @Mock
    private StoryTreeIntegrityBusiness storyTreeIntegrityBusiness;

    private Story story;
    private Iteration firstIteration;
    private Iteration secondIteration;
    private Project firstProject;
    private Project secondProject;
    private Product firstProduct;

    @Before
    public void createModels() {
        story = new Story();
        story.setId(1);

        firstIteration = new Iteration();
        firstIteration.setId(1);

        secondIteration = new Iteration();
        secondIteration.setId(2);

        firstProject = new Project();
        firstProject.setId(3);

        secondProject = new Project();
        secondProject.setId(4);
        
        firstProduct = new Product();
        firstProduct.setId(5);
    }

    @Test
    @DirtiesContext
    public void moveFromIterationToProject() {
        firstIteration.setParent(firstProject);
        story.setBacklog(firstIteration);

        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, secondProject)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, firstProject);
        storyRankBusiness.rankToBottom(story, secondProject);
        storyRankBusiness.removeRank(story, firstIteration);

        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());

        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());

        replayAll();
        storyBusiness.moveStoryToBacklog(story, secondProject);
        verifyAll();
        assertEquals(secondProject, story.getBacklog());  
    }

    @Test
    @DirtiesContext
    public void moveFromIterationToProject_iterationUnderProject() {
        firstIteration.setParent(firstProject);
        story.setBacklog(firstIteration);

        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstProject)).andReturn(true);

        storyDAO.store(story);

        storyRankBusiness.removeRank(story, firstIteration);

        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());

        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());

        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProject);
        verifyAll();
        assertEquals(firstProject, story.getBacklog());
    }

    @Test
    @DirtiesContext
    public void moveFromProjectToIteration() {
        firstIteration.setParent(firstProject);
        story.setBacklog(secondProject);
        
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstIteration)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, secondProject);
        storyRankBusiness.rankToBottom(story, firstIteration);
        storyRankBusiness.rankToBottom(story, firstProject);
        
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        
        replayAll();
        storyBusiness.moveStoryToBacklog(story, firstIteration);
        verifyAll();
        assertEquals(firstIteration, story.getBacklog());
    }

    @Test
    @DirtiesContext
    public void moveFromProjectToIteration_iterationInProject() {
        firstIteration.setParent(firstProject);
        story.setBacklog(firstProject);
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstIteration)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, firstIteration);
        
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        
        replayAll();
        storyBusiness.moveStoryToBacklog(story, firstIteration);
        verifyAll();
        assertEquals(firstIteration, story.getBacklog());
    }

    @Test
    @DirtiesContext
    public void moveFromIterationToIteration() {
        firstIteration.setParent(firstProject);
        secondIteration.setParent(secondProject);
        story.setBacklog(firstIteration);
        
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, secondIteration)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, firstIteration);
        storyRankBusiness.rankToBottom(story, secondIteration);
        storyRankBusiness.removeRank(story, firstProject);
        storyRankBusiness.rankToBottom(story, secondProject);
        
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(secondIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        iterationHistoryBusiness.updateIterationHistory(secondIteration.getId());
        replayAll();
        storyBusiness.moveStoryToBacklog(story, secondIteration);
        verifyAll();
        assertEquals(secondIteration, story.getBacklog());
    }

    @Test
    @DirtiesContext
    public void moveFromIterationToIteration_inProject() {
        firstIteration.setParent(firstProject);
        secondIteration.setParent(firstProject);
        story.setBacklog(firstIteration);
        
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, secondIteration)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, firstIteration);
        storyRankBusiness.rankToBottom(story, secondIteration);
        
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(secondIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        iterationHistoryBusiness.updateIterationHistory(secondIteration.getId());
        
        replayAll();

        storyBusiness.moveStoryToBacklog(story, secondIteration);
        verifyAll();
        assertEquals(secondIteration, story.getBacklog());
    }
    
    @Test
    @DirtiesContext
    public void moveFromProjectToProject() {
        story.setBacklog(secondProject);

        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstProject)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, secondProject);
        storyRankBusiness.rankToBottom(story, firstProject);
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProject);
        verifyAll();
        assertEquals(firstProject, story.getBacklog());
    }

    @Test
    @DirtiesContext
    public void moveFromProjectToProject_hasChildren() {
        story.setBacklog(secondProject);
        Story child = new Story();
        story.getChildren().add(child);

        expect(backlogBusiness.getParentProduct(secondProject)).andReturn(firstProduct);
        expect(backlogBusiness.getParentProduct(firstProject)).andReturn(firstProduct);

        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstProject)).andReturn(true);
        storyDAO.store(story);
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProject);
        verifyAll();
        assertEquals(firstProject, story.getBacklog());
    }
    
    @Test
    @DirtiesContext
    public void moveFromIterationToProduct() {
        firstIteration.setParent(firstProject);
        story.setBacklog(firstIteration);
        
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstProduct)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, firstIteration);
        storyRankBusiness.removeRank(story, firstProject);
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProduct.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProduct);
        verifyAll();
        assertEquals(firstProduct, story.getBacklog());
    }
    
    @Test
    @DirtiesContext
    public void moveFromProjectToProduct() {
        story.setBacklog(firstProject);
        
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstProduct)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, firstProject);
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProduct.getId());
        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProduct);
        verifyAll();
        assertEquals(firstProduct, story.getBacklog());
    }
    
    @Test(expected = OperationNotPermittedException.class)
    @DirtiesContext
    public void moveFromProjectToProduct_integrityViolation() {
        story.setBacklog(firstProject);
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstProduct)).andReturn(false);
        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProduct);
        verifyAll();
    }

    @Test(expected = OperationNotPermittedException.class)
    @DirtiesContext
    public void moveWithChildrenToAnotherProduct() {
        Product oldParent = new Product();
        Backlog oldBacklog = new Project();
        oldBacklog.setId(8482);
        oldBacklog.setParent(oldParent);

        Product newBacklog = new Product();
        newBacklog.setId(1904);

        Story movable = new Story();
        movable.setBacklog(oldBacklog);

        movable.setChildren(Arrays.asList(new Story(),
                new Story()));

        expect(backlogBusiness.getParentProduct(oldBacklog)).andReturn(
                oldParent);
        expect(backlogBusiness.getParentProduct(newBacklog)).andReturn(
                newBacklog);
        replayAll();
        storyBusiness.moveStoryToBacklog(movable, newBacklog);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void moveFromProductToIteration() {
        firstProject.setParent(firstProduct);
        firstIteration.setParent(firstProject);
        story.setBacklog(firstProduct);
        
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstIteration)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, firstIteration);
        storyRankBusiness.rankToBottom(story, firstProject);
        
        backlogHistoryEntryBusiness.updateHistory(firstProduct.getId());
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        
        replayAll();
        storyBusiness.moveStoryToBacklog(story, firstIteration);
        verifyAll();
        assertEquals(firstIteration, story.getBacklog());
    }
    
    @Test
    @DirtiesContext
    public void moveFromProductToProject() {
        story.setBacklog(firstProduct);
        
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, firstProject)).andReturn(true);
        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, firstProject);
        
        backlogHistoryEntryBusiness.updateHistory(firstProduct.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        
        replayAll();
        storyBusiness.moveStoryToBacklog(story, firstProject);
        verifyAll();
        assertEquals(firstProject, story.getBacklog());
    }
    
    @Test
    @DirtiesContext
    public void moveFromProductToProduct() {
        Product prod = new Product();
        prod.setId(313);
        story.setBacklog(firstProduct);
        expect(storyTreeIntegrityBusiness.canStoryBeMovedToBacklog(story, prod)).andReturn(true);
        storyDAO.store(story);
        backlogHistoryEntryBusiness.updateHistory(prod.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProduct.getId());
        replayAll();
        storyBusiness.moveStoryToBacklog(story, prod);
        assertEquals(prod, story.getBacklog());
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void moveSingleFromProjectToProject() {
        Story parent = new Story();
        Story child = new Story();
        story.setBacklog(secondProject);
        story.setParent(parent);
        story.getChildren().add(child);
        child.setParent(story);
        
        expect(backlogBusiness.getParentProduct(secondProject)).andReturn(firstProduct);
        expect(backlogBusiness.getParentProduct(firstProject)).andReturn(firstProduct);
        
        storyDAO.store(child);
        storyDAO.store(story);
        expect(storyTreeIntegrityBusiness.hasParentStoryConflict(story, firstProject)).andReturn(true);
        storyHierarchyBusiness.updateChildrenTreeRanks(parent);
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        replayAll();

        storyBusiness.moveSingleStoryToBacklog(story, firstProject);
        verifyAll();
        assertEquals(firstProject, story.getBacklog());
        assertNull(story.getParent());
        assertEquals(parent, child.getParent());
    }
    
    @Test
    @DirtiesContext
    public void moveSingleFromProjectToProjectNoViolation() {
        Story parent = new Story();
        Story child = new Story();
        story.setBacklog(secondProject);
        story.setParent(parent);
        story.getChildren().add(child);
        child.setParent(story);
        
        expect(backlogBusiness.getParentProduct(secondProject)).andReturn(firstProduct);
        expect(backlogBusiness.getParentProduct(firstProject)).andReturn(firstProduct);
        
        storyDAO.store(child);
        storyDAO.store(story);
        expect(storyTreeIntegrityBusiness.hasParentStoryConflict(story, firstProject)).andReturn(false);
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        replayAll();

        storyBusiness.moveSingleStoryToBacklog(story, firstProject);
        verifyAll();
        assertEquals(firstProject, story.getBacklog());
        assertEquals(parent, story.getParent());
        assertEquals(parent, child.getParent());
    }
    
    @Test
    @DirtiesContext
    public void testMoveStoryAndChildren() {
        Story parent = new Story();
        Story child1 = new Story();
        Story child2 = new Story();
        
        parent.getChildren().add(story);
        
        story.setParent(parent);
        story.getChildren().add(child1);
        
        child1.setParent(story);
        child1.getChildren().add(child2);
        
        child2.setParent(child1);
        
        parent.setBacklog(firstProject);
        story.setBacklog(firstProject);
        child1.setBacklog(firstProject);
        child2.setBacklog(firstIteration);
        
        firstIteration.setParent(firstProduct);
        
        expect(storyTreeIntegrityBusiness.hasParentStoryConflict(story, secondProject)).andReturn(true);
        storyHierarchyBusiness.updateChildrenTreeRanks(parent);
        
        expect(backlogBusiness.getParentProduct(firstProject)).andReturn(firstProduct);
        expect(backlogBusiness.getParentProduct(secondProject)).andReturn(firstProduct);
                
        storyDAO.store(child2);
        
        storyRankBusiness.removeRank(child2, firstIteration);
        storyRankBusiness.removeRank(child2, firstIteration.getParent());
    
        storyRankBusiness.rankToBottom(child2, secondProject);
        
        storyDAO.store(child1);
        storyDAO.store(story);
        
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        EasyMock.expectLastCall().times(2);
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        EasyMock.expectLastCall().times(2);
        
        replayAll();
        storyBusiness.moveStoryAndChildren(story, secondProject);
        verifyAll();
    }
    
    @Test(expected=OperationNotPermittedException.class)
    @DirtiesContext
    public void testMoveStoryAndChildren_toIteration() {
        Story parent = new Story();
        Story child1 = new Story();
        Story child2 = new Story();
        
        parent.getChildren().add(story);
        
        story.setParent(parent);
        story.getChildren().add(child1);
        
        child1.setParent(story);
        child1.getChildren().add(child2);
        
        child2.setParent(child1);
        
        parent.setBacklog(firstProject);
        story.setBacklog(firstProject);
        child1.setBacklog(firstProject);
        child2.setBacklog(firstIteration);
        
        firstIteration.setParent(firstProduct);
        expect(backlogBusiness.getParentProduct(firstProject)).andReturn(firstProduct);
        expect(backlogBusiness.getParentProduct(secondIteration)).andReturn(firstProduct);
        replayAll();
        storyBusiness.moveStoryAndChildren(story, secondIteration);
        verifyAll();
    }
}
