package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StorySplitBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;

public class StorySplitBusinessTest {

    StorySplitBusinessImpl testable;

    StoryDAO storyDAO;

    BacklogHistoryEntryBusiness backlogHistoryBusiness;

    StoryBusiness storyBusiness;

    Product product;

    Project project;

    Iteration iteration;

    Story parentStory;

    List<Story> childStories;

    @Before
    public void setUp_dependencies() {
        testable = new StorySplitBusinessImpl();

        storyDAO = createStrictMock(StoryDAO.class);
        testable.setStoryDAO(storyDAO);

        backlogHistoryBusiness = createStrictMock(BacklogHistoryEntryBusiness.class);
        testable.setBacklogHistoryEntryBusiness(backlogHistoryBusiness);

        storyBusiness = createStrictMock(StoryBusiness.class);
        testable.setStoryBusiness(storyBusiness);
    }

    @Before
    public void setUp_data() {
        product = new Product();
        product.setId(1);

        project = new Project();
        project.setId(2);
        project.setParent(product);

        iteration = new Iteration();
        iteration.setId(3);
        iteration.setParent(project);

        parentStory = new Story();
        parentStory.setId(1);
        parentStory.setRank(2);

        User responsible = new User();
        parentStory.getResponsibles().add(responsible);

        childStories = new ArrayList<Story>();
        childStories.add(new Story());
        childStories.add(new Story());
    }

    private void verifyAll() {
        verify(storyDAO, backlogHistoryBusiness, storyBusiness);
    }

    private void replayAll() {
        replay(storyDAO, backlogHistoryBusiness, storyBusiness);
    }

    private void childCreationExpects() {
        expect(storyDAO.getLastStoryInRank(parentStory.getBacklog())).andReturn(parentStory);
        expect(storyDAO.create(childStories.get(0))).andReturn(1);
        expect(storyDAO.create(childStories.get(1))).andReturn(2);
    }

    private void checkChildStories(Backlog backlog) {
        for (Story child : childStories) {
            assertEquals(parentStory, child.getParent());

            assertEquals(backlog, child.getBacklog());

            assertEquals(parentStory.getResponsibles(), child.getResponsibles());
        }
        assertEquals(3, childStories.get(0).getRank());
        assertEquals(4, childStories.get(1).getRank());
    }

    @Test
    public void testSplitStory_inProductBacklog() {
        parentStory.setBacklog(product);
        childCreationExpects();
        backlogHistoryBusiness.updateHistory(1);
        replayAll();
        testable.splitStory(parentStory, childStories);
        verifyAll();
        checkChildStories(product);
    }

    @Test
    public void testSplitStory_inIterationBacklog() {
        parentStory.setBacklog(iteration);
        childCreationExpects();
        storyBusiness.moveStoryToBacklog(parentStory, product);
        replayAll();
        testable.splitStory(parentStory, childStories);
        verifyAll();
        checkChildStories(iteration);
    }

    @Test
    public void testSplitStory_inProjectBacklog() {
        parentStory.setBacklog(project);
        childCreationExpects();
        storyBusiness.moveStoryToBacklog(parentStory, product);
        replayAll();
        testable.splitStory(parentStory, childStories);
        verifyAll();
        checkChildStories(project);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitStory_emptyList() {
        testable.splitStory(new Story(), new ArrayList<Story>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitStory_nullOriginal() {
        testable.splitStory(null, Arrays.asList(new Story()));
    }
    
    @Test(expected = RuntimeException.class)
    public void testSplitStory_originalNotPersisted() {
        testable.splitStory(new Story(), Arrays.asList(new Story()));
    }
}
