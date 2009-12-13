package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;

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

        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, secondProject, firstProject);
        storyRankBusiness.removeRank(story, firstIteration);

        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());

        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());

        replayAll();
        storyBusiness.moveStoryToBacklog(story, secondProject);
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void moveFromIterationToProject_iterationUnderProject() {
        firstIteration.setParent(firstProject);
        story.setBacklog(firstIteration);

        storyDAO.store(story);

        storyRankBusiness.removeRank(story, firstIteration);

        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());

        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());

        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProject);
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void moveFromProjectToIteration() {
        firstIteration.setParent(firstProject);
        story.setBacklog(secondProject);
        
        
        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, firstIteration, secondProject);
        storyRankBusiness.rankToBottom(story, firstProject);
        
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        
        replayAll();
        storyBusiness.moveStoryToBacklog(story, firstIteration);
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void moveFromProjectToIteration_iterationInProject() {
        firstIteration.setParent(firstProject);
        story.setBacklog(firstProject);
        
        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, firstIteration);
        
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        
        replayAll();
        storyBusiness.moveStoryToBacklog(story, firstIteration);
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void moveFromIterationToIteration() {
        firstIteration.setParent(firstProject);
        secondIteration.setParent(secondProject);
        story.setBacklog(firstIteration);
        
        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, secondIteration, firstIteration);
        storyRankBusiness.rankToBottom(story, secondProject, firstProject);
        
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(secondIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        iterationHistoryBusiness.updateIterationHistory(secondIteration.getId());
        replayAll();
        storyBusiness.moveStoryToBacklog(story, secondIteration);
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void moveFromIterationToIteration_inProject() {
        firstIteration.setParent(firstProject);
        secondIteration.setParent(firstProject);
        story.setBacklog(firstIteration);
        
        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, secondIteration, firstIteration);
        
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(secondIteration.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        iterationHistoryBusiness.updateIterationHistory(secondIteration.getId());
        
        replayAll();

        storyBusiness.moveStoryToBacklog(story, secondIteration);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void moveFromProjectToProject() {
        story.setBacklog(secondProject);

        storyDAO.store(story);
        storyRankBusiness.rankToBottom(story, firstProject, secondProject);
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProject);
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void moveFromProjectToProject_hasChildren() {
        story.setBacklog(secondProject);
        Story child = new Story();
        story.getChildren().add(child);

        expect(backlogBusiness.getParentProduct(secondProject)).andReturn(firstProduct);
        expect(backlogBusiness.getParentProduct(firstProject)).andReturn(firstProduct);

        storyDAO.store(story);
        backlogHistoryEntryBusiness.updateHistory(secondProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProject);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void moveFromIterationToProduct() {
        firstIteration.setParent(firstProject);
        story.setBacklog(firstIteration);
        
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, firstIteration);
        storyRankBusiness.removeRank(story, firstProject);
        backlogHistoryEntryBusiness.updateHistory(firstIteration.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProduct.getId());
        
        iterationHistoryBusiness.updateIterationHistory(firstIteration.getId());
        replayAll();

        storyBusiness.moveStoryToBacklog(story, firstProduct);
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void moveFromProjectToProduct() {
        story.setBacklog(firstProject);
        
        storyDAO.store(story);
        storyRankBusiness.removeRank(story, firstProject);
        backlogHistoryEntryBusiness.updateHistory(firstProject.getId());
        backlogHistoryEntryBusiness.updateHistory(firstProduct.getId());
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

        movable.setChildren(new HashSet<Story>(Arrays.asList(new Story(),
                new Story())));

        expect(backlogBusiness.getParentProduct(oldBacklog)).andReturn(
                oldParent);
        expect(backlogBusiness.getParentProduct(newBacklog)).andReturn(
                newBacklog);
        replayAll();
        storyBusiness.moveStoryToBacklog(movable, newBacklog);
        verifyAll();
    }
/*
    @Test
    @DirtiesContext
    public void moveWithChildrenToSameProduct() {
        Product newBacklog = new Product();
        newBacklog.setId(1904);

        Backlog oldBacklog = new Iteration();
        oldBacklog.setId(8482);
        Project project = new Project();
        project.setParent(new Product());
        oldBacklog.setParent(newBacklog);

        Story movable = new Story();
        movable.setBacklog(oldBacklog);

        movable.setChildren(new HashSet<Story>(Arrays.asList(new Story(),
                new Story())));

        expect(backlogBusiness.getParentProduct(oldBacklog)).andReturn(
                newBacklog);
        expect(backlogBusiness.getParentProduct(newBacklog)).andReturn(
                newBacklog);

        storyDAO.store(isA(Story.class));
        expect(backlogBusiness.retrieve(1904)).andReturn(newBacklog);
        //storyRankBusiness.rankToBottom(movable, newBacklog);

        backlogHistoryEntryBusiness.updateHistory(oldBacklog.getId());
        backlogHistoryEntryBusiness.updateHistory(newBacklog.getId());

        iterationHistoryBusiness.updateIterationHistory(oldBacklog.getId());

        replayAll();
        storyBusiness.moveStoryToBacklog(movable, newBacklog);
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void moveParentStoryUnderDifferentProduct() {
        Product newBacklog = new Product();
        newBacklog.setId(1904);

        Backlog oldBacklog = new Iteration();
        oldBacklog.setId(8482);
        Project project = new Project();
        project.setParent(new Product());

        Story parentStory = new Story();
        parentStory.setBacklog(oldBacklog);

        Story movable = new Story();
        movable.setBacklog(oldBacklog);

        movable.setParent(parentStory);

        expect(backlogBusiness.getParentProduct(oldBacklog)).andReturn(
                new Product());
        expect(backlogBusiness.getParentProduct(newBacklog)).andReturn(
                newBacklog);

        storyDAO.store(isA(Story.class));
        expect(backlogBusiness.retrieve(1904)).andReturn(newBacklog);
        //storyRankBusiness.rankToBottom(movable, newBacklog);

        backlogHistoryEntryBusiness.updateHistory(oldBacklog.getId());
        backlogHistoryEntryBusiness.updateHistory(newBacklog.getId());

        iterationHistoryBusiness.updateIterationHistory(oldBacklog.getId());

        replayAll();
        storyBusiness.moveStoryToBacklog(movable, newBacklog);
        verifyAll();

        assertNull(movable.getParent());
    }
    */
}
