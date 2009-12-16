package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import fi.hut.soberit.agilefant.business.impl.StorySplitBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.User;

public class StorySplitBusinessTest {

    StorySplitBusinessImpl testable;

    StoryDAO storyDAO;

    StoryBusiness storyBusiness;
    
    Product product;

    Project project;

    Iteration iteration;

    Story parentStory;

    List<Story> childStories;
    Collection<Story> oldStories;
    

    @Before
    public void setUp_dependencies() {
        testable = new StorySplitBusinessImpl();

        storyDAO = createStrictMock(StoryDAO.class);
        testable.setStoryDAO(storyDAO);

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

        User responsible = new User();
        parentStory.getResponsibles().add(responsible);

        childStories = new ArrayList<Story>();
        Story firstChild = new Story();
        firstChild.setBacklog(product);
        Story secondChild = new Story();
        secondChild.setBacklog(product);
        childStories.add(firstChild);
        childStories.add(secondChild);
    }

    private void verifyAll() {
        verify(storyDAO, storyBusiness);
    }

    private void replayAll() {
        replay(storyDAO, storyBusiness);
    }

    private void childCreationExpects() {
        expect(storyBusiness.updateStoryRanks(parentStory)).andReturn(null);
        expect(storyBusiness.create(childStories.get(0))).andReturn(1);
        expect(storyBusiness.create(childStories.get(1))).andReturn(2);
    }

    private void checkChildStories() {
        for (Story child : childStories) {
            assertEquals(parentStory, child.getParent());

            assertEquals(parentStory.getResponsibles(), child.getResponsibles());
            
            assertTrue(parentStory.getChildren().contains(child));
        }     
    }

    @Test
    public void testSplitStory_inProductBacklog() {
        parentStory.setBacklog(product);
        childCreationExpects();
        replayAll();
        testable.splitStory(parentStory, childStories, oldStories);
        verifyAll();
        checkChildStories();
    }

    @Test
    public void testSplitStory_inIterationBacklog() {
        parentStory.setBacklog(iteration);
        childCreationExpects();
        replayAll();
        testable.splitStory(parentStory, childStories, oldStories);
        verifyAll();
        checkChildStories();
    }

    @Test
    public void testSplitStory_inProjectBacklog() {
        parentStory.setBacklog(project);
        childCreationExpects();
        replayAll();
        testable.splitStory(parentStory, childStories, oldStories);
        verifyAll();
        checkChildStories();
    }
    
    @Test
    public void testSplitStory_changeOld() {
        Story existing = new Story();
        oldStories = Arrays.asList(existing);
        existing.setBacklog(project);
        existing.setId(10);
        parentStory.setBacklog(project);
        childCreationExpects();
        expect(storyBusiness.retrieve(10)).andReturn(existing);
        storyBusiness.storeBatch(oldStories);
        replayAll();
        testable.splitStory(parentStory, childStories, oldStories);
        verifyAll();
        checkChildStories();
    }
    
    @Test
    public void testSplitStory_backlogChangeOld() {
        Story existing = new Story();
        existing.setId(10);
        oldStories = Arrays.asList(existing);
        existing.setBacklog(project);
        
        Story persistedExisting = new Story();
        persistedExisting.setBacklog(iteration);
        
        parentStory.setBacklog(project);
        childCreationExpects();
        expect(storyBusiness.retrieve(10)).andReturn(persistedExisting);
        storyBusiness.moveStoryToBacklog(persistedExisting, project);
        storyBusiness.storeBatch(oldStories);
        replayAll();
        testable.splitStory(parentStory, childStories, oldStories);
        verifyAll();
        checkChildStories();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitStory_emptyList() {
        testable.splitStory(new Story(), new ArrayList<Story>(), oldStories);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSplitStory_nullOriginal() {
        testable.splitStory(null, Arrays.asList(new Story()), oldStories);
    }
    
    @Test(expected = RuntimeException.class)
    public void testSplitStory_originalNotPersisted() {
        testable.splitStory(new Story(), Arrays.asList(new Story()), oldStories);
    }
    
    @Test
    public void testSplitStory_dontChangeOriginalStoryBacklog() {
        parentStory.setBacklog(iteration);
        childCreationExpects();
        replayAll();
        testable.splitStory(parentStory, childStories, oldStories);
        verifyAll();
        checkChildStories();
        assertSame(iteration, parentStory.getBacklog());
    }
}
