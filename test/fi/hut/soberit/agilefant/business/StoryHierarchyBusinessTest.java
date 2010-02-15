package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.StoryHierarchyBusinessImpl;
import fi.hut.soberit.agilefant.db.StoryHierarchyDAO;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.util.StoryFilters;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryHierarchyBusinessTest extends MockedTestCase {

    @TestedBean
    private StoryHierarchyBusinessImpl storyHierarchyBusiness;

    @Mock(strict = true)
    private StoryHierarchyDAO storyHierarchyDAO;

    @Mock(strict = true)
    private StoryBusiness storyBusiness;

    @Mock(strict = true)
    private BacklogBusiness backlogBusiness;

    @Mock(strict = true)
    private StoryFilterBusiness storyFilterBusiness;

    private List<Story> children;

    private Story story;
    private Story reference;
    private Story story1;
    private Story story2;
    private Story story3;

    private Story oldParent;
    private Story story4;

    @Before
    public void setUp_storyList() {
        story1 = new Story();
        story1.setId(1);
        story2 = new Story();
        story2.setId(2);
        story3 = new Story();
        story3.setId(3);

        children = new ArrayList<Story>();

        story = new Story();
        story.setId(4);
        reference = new Story();
        reference.setId(5);

        story4 = new Story();
        story4.setId(6);
        oldParent = new Story();

    }

    @Test
    @DirtiesContext
    public void testRetrieveProjectLeafStories() {
        Project proj = new Project();
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProjectLeafStories(proj)).andReturn(
                stories);
        replayAll();
        assertSame(stories, storyHierarchyBusiness
                .retrieveProjectLeafStories(proj));
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void testRetrieveProjectRootStories() {
        int projectId = 100;
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProjectRootStories(projectId))
                .andReturn(stories);
        replayAll();
        assertEquals(stories, storyHierarchyBusiness
                .retrieveProjectRootStories(projectId, null));
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void testRetrieveProductRootStories() {
        int productId = 100;
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProductRootStories(productId))
                .andReturn(stories);
        replayAll();
        assertSame(stories, storyHierarchyBusiness.retrieveProductRootStories(
                productId, null));
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void testRetrieveProjectRootStories_withStoryFilters() {
        int projectId = 100;
        StoryFilters storyFilters = new StoryFilters(null, null, null);
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProjectRootStories(projectId))
                .andReturn(stories);
        expect(storyFilterBusiness.filterStories(stories, storyFilters))
                .andReturn(stories);
        replayAll();
        assertEquals(stories, storyHierarchyBusiness
                .retrieveProjectRootStories(projectId, storyFilters));
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void testRetrieveProductRootStories_withStoryFilters() {
        int productId = 100;
        StoryFilters storyFilters = new StoryFilters(null, null, null);
        List<Story> stories = new ArrayList<Story>();
        expect(storyHierarchyDAO.retrieveProductRootStories(productId))
                .andReturn(stories);
        expect(storyFilterBusiness.filterStories(stories, storyFilters))
                .andReturn(stories);
        replayAll();
        assertSame(stories, storyHierarchyBusiness.retrieveProductRootStories(
                productId, storyFilters));
        verifyAll();
    }

    @Test
    @DirtiesContext
    public void testMoveUnder() {
        story1.setParent(reference);
        story2.setParent(reference);
        story3.setParent(reference);
        children.add(story1);
        children.add(story2);
        children.add(story3);
        reference.setChildren(children);

        story.setParent(oldParent);
        oldParent.getChildren().add(story);
        oldParent.getChildren().add(story4);

        expect(storyBusiness.updateStoryRanks(oldParent)).andReturn(null);
        expect(storyBusiness.updateStoryRanks(reference)).andReturn(null);

        replayAll();

        storyHierarchyBusiness.moveUnder(story, reference);

        verifyAll();

        assertTrue(reference.getChildren().contains(story));
        assertFalse(oldParent.getChildren().contains(story));
        assertSame(reference, story.getParent());

        assertEquals(1, oldParent.getChildren().size());
        assertEquals(0, oldParent.getChildren().get(0).getTreeRank());

        assertEquals(4, reference.getChildren().size());
        assertEquals(story, reference.getChildren().get(3));
        assertEquals(3, reference.getChildren().get(3).getTreeRank());
    }

    @Test
    @DirtiesContext
    public void testMoveUnder_emptiesOld() {
        reference.setChildren(children);
        story1.setParent(reference);
        story2.setParent(reference);
        story3.setParent(reference);
        children.add(story1);
        children.add(story2);
        children.add(story3);

        story.setParent(oldParent);
        oldParent.getChildren().add(story);

        expect(storyBusiness.updateStoryRanks(oldParent)).andReturn(null);
        expect(storyBusiness.updateStoryRanks(reference)).andReturn(null);

        replayAll();

        storyHierarchyBusiness.moveUnder(story, reference);

        verifyAll();

        assertTrue(reference.getChildren().contains(story));
        assertFalse(oldParent.getChildren().contains(story));
        assertSame(reference, story.getParent());

        assertEquals(0, oldParent.getChildren().size());

        assertEquals(4, reference.getChildren().size());
        assertEquals(story, reference.getChildren().get(3));
        assertEquals(3, reference.getChildren().get(3).getTreeRank());
    }

    @Test
    @DirtiesContext
    public void testMoveUnder_toempty() {
        reference.setChildren(children);

        story.setParent(oldParent);
        oldParent.getChildren().add(story);
        oldParent.getChildren().add(story4);

        expect(storyBusiness.updateStoryRanks(oldParent)).andReturn(null);
        expect(storyBusiness.updateStoryRanks(reference)).andReturn(null);

        replayAll();

        storyHierarchyBusiness.moveUnder(story, reference);

        verifyAll();

        assertTrue(reference.getChildren().contains(story));
        assertFalse(oldParent.getChildren().contains(story));
        assertSame(reference, story.getParent());

        assertEquals(1, oldParent.getChildren().size());
        assertEquals(0, oldParent.getChildren().get(0).getTreeRank());

        assertEquals(1, reference.getChildren().size());
        assertEquals(story, reference.getChildren().get(0));
        assertEquals(0, reference.getChildren().get(0).getTreeRank());
    }

    @Test
    @DirtiesContext
    public void testRankAfter() {
        Story parent = new Story();
        story.setParent(parent);
        reference.setParent(parent);
        parent.setChildren(children);

        children.add(story1);
        children.add(reference);
        children.add(story2);
        children.add(story3);

        storyHierarchyBusiness.moveAfter(story, reference);
        assertEquals(0, story1.getTreeRank());
        assertEquals(1, reference.getTreeRank());
        assertEquals(2, story.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

    }

    @Test
    @DirtiesContext
    public void testRankAfter_move() {
        Story parent = new Story();
        story.setParent(oldParent);
        reference.setParent(parent);
        parent.setChildren(children);

        children.add(story1);
        children.add(reference);
        children.add(story2);
        children.add(story3);

        oldParent.getChildren().add(story4);

        expect(storyBusiness.updateStoryRanks(oldParent)).andReturn(null);
        expect(storyBusiness.updateStoryRanks(parent)).andReturn(null);

        replayAll();

        storyHierarchyBusiness.moveAfter(story, reference);

        verifyAll();

        assertEquals(0, story1.getTreeRank());
        assertEquals(1, reference.getTreeRank());
        assertEquals(2, story.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

        assertEquals(0, story4.getTreeRank());
        assertEquals(1, oldParent.getChildren().size());

        assertEquals(parent, story.getParent());

    }

    @Test
    @DirtiesContext
    public void testRankAfter_moveAsRoot() {
        Product product = new Product();
        Project proj = new Project();
        story.setParent(oldParent);
        reference.setParent(null);
        story.setBacklog(proj);

        children.add(story1);
        children.add(reference);
        children.add(story2);
        children.add(story3);

        expect(backlogBusiness.getParentProduct(story.getBacklog())).andReturn(
                product);
        expect(storyHierarchyDAO.retrieveProductRootStories(product.getId()))
                .andReturn(children);
        expect(storyBusiness.updateStoryRanks(oldParent)).andReturn(null);

        replayAll();

        storyHierarchyBusiness.moveAfter(story, reference);

        verifyAll();

        assertEquals(0, story1.getTreeRank());
        assertEquals(1, reference.getTreeRank());
        assertEquals(2, story.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

        assertNull(story.getParent());

    }

    @Test
    @DirtiesContext
    public void testRankAfter_rootStories() {
        Product product = new Product();
        Project proj = new Project();
        story.setParent(null);
        reference.setParent(null);
        story.setBacklog(proj);

        children.add(story1);
        children.add(reference);
        children.add(story2);
        children.add(story3);

        expect(backlogBusiness.getParentProduct(story.getBacklog())).andReturn(
                product);
        expect(storyHierarchyDAO.retrieveProductRootStories(product.getId()))
                .andReturn(children);

        replayAll();

        storyHierarchyBusiness.moveAfter(story, reference);

        verifyAll();

        assertEquals(0, story1.getTreeRank());
        assertEquals(1, reference.getTreeRank());
        assertEquals(2, story.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

    }

    @Test
    @DirtiesContext
    public void testRankAfter_last() {
        Story parent = new Story();
        story.setParent(parent);
        reference.setParent(parent);
        parent.setChildren(children);

        children.add(story1);
        children.add(story2);
        children.add(story3);
        children.add(reference);

        storyHierarchyBusiness.moveAfter(story, reference);

        assertEquals(0, story1.getTreeRank());
        assertEquals(1, story2.getTreeRank());
        assertEquals(2, story3.getTreeRank());
        assertEquals(3, reference.getTreeRank());
        assertEquals(4, story.getTreeRank());
    }

    @Test
    @DirtiesContext
    public void testRankBefore() {
        Story parent = new Story();
        story.setParent(parent);
        reference.setParent(parent);
        parent.setChildren(children);

        children.add(story1);
        children.add(reference);
        children.add(story2);
        children.add(story3);

        storyHierarchyBusiness.moveBefore(story, reference);

        assertEquals(0, story1.getTreeRank());
        assertEquals(1, story.getTreeRank());
        assertEquals(2, reference.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

    }

    @Test
    @DirtiesContext
    public void testRankBefore_move() {
        Story parent = new Story();
        story.setParent(oldParent);
        reference.setParent(parent);
        parent.setChildren(children);

        children.add(story1);
        children.add(reference);
        children.add(story2);
        children.add(story3);

        oldParent.getChildren().add(story4);

        expect(storyBusiness.updateStoryRanks(oldParent)).andReturn(null);
        expect(storyBusiness.updateStoryRanks(parent)).andReturn(null);

        replayAll();

        storyHierarchyBusiness.moveBefore(story, reference);

        verifyAll();

        assertEquals(0, story1.getTreeRank());
        assertEquals(1, story.getTreeRank());
        assertEquals(2, reference.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

        assertEquals(0, story4.getTreeRank());
        assertEquals(1, oldParent.getChildren().size());

        assertEquals(parent, story.getParent());

    }

    @Test
    @DirtiesContext
    public void testRankBefore_moveAsRoot() {
        Product product = new Product();
        Project proj = new Project();
        story.setParent(oldParent);
        reference.setParent(null);
        story.setBacklog(proj);

        children.add(story1);
        children.add(reference);
        children.add(story2);
        children.add(story3);

        expect(backlogBusiness.getParentProduct(story.getBacklog())).andReturn(
                product);
        expect(storyHierarchyDAO.retrieveProductRootStories(product.getId()))
                .andReturn(children);
        expect(storyBusiness.updateStoryRanks(oldParent)).andReturn(null);

        replayAll();

        storyHierarchyBusiness.moveBefore(story, reference);

        verifyAll();

        assertEquals(0, story1.getTreeRank());
        assertEquals(1, story.getTreeRank());
        assertEquals(2, reference.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

        assertNull(story.getParent());

    }

    @Test
    @DirtiesContext
    public void testRankBefore_rootStories() {
        Product product = new Product();
        Project proj = new Project();
        story.setParent(null);
        reference.setParent(null);
        story.setBacklog(proj);

        children.add(story1);
        children.add(reference);
        children.add(story2);
        children.add(story3);

        expect(backlogBusiness.getParentProduct(story.getBacklog())).andReturn(
                product);
        expect(storyHierarchyDAO.retrieveProductRootStories(product.getId()))
                .andReturn(children);

        replayAll();

        storyHierarchyBusiness.moveBefore(story, reference);

        verifyAll();

        assertEquals(0, story1.getTreeRank());
        assertEquals(1, story.getTreeRank());
        assertEquals(2, reference.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

    }

    @Test
    @DirtiesContext
    public void testRankBefore_first() {
        Story parent = new Story();
        story.setParent(parent);
        reference.setParent(parent);
        parent.setChildren(children);

        children.add(reference);
        children.add(story1);
        children.add(story2);
        children.add(story3);

        storyHierarchyBusiness.moveBefore(story, reference);

        assertEquals(0, story.getTreeRank());
        assertEquals(1, reference.getTreeRank());
        assertEquals(2, story1.getTreeRank());
        assertEquals(3, story2.getTreeRank());
        assertEquals(4, story3.getTreeRank());

    }

    @Test
    @DirtiesContext
    public void testReplaceStoryNodesWithRoots() {
        List<Story> stories = new ArrayList<Story>();

        // INPUT:
        // 1 -> [4]
        // 1 -> 2 -> [3]
        // OUTPUT:
        // 1
        // \- 2
        // ___\- 3
        // \- 4
        story.getChildren().add(story2);
        story2.setParent(story);
        story.getChildren().add(story4);
        story4.setParent(story);
        story2.getChildren().add(story3);
        story3.setParent(story2);

        stories.add(story4);
        stories.add(story3);
        List<Story> results = storyHierarchyBusiness
                .replaceStoryNodesWithRoots(stories);
        for (Story result : results) {
            System.out.println(result.getId());
        }

        assertEquals(1, results.size());
        Story result1 = results.get(0);
        assertEquals(story.getId(), result1.getId());
        assertEquals(2, result1.getChildren().size());
        Story result1_child1 = result1.getChildren().get(0);
        Story result1_child2 = result1.getChildren().get(1);
        Story result2;
        Story result4;
        if (result1_child1.getId() == story2.getId()) {
            result2 = result1_child1;
            result4 = result1_child2;
        } else {
            result2 = result1_child2;
            result4 = result1_child1;
        }

        assertEquals(story2.getId(), result2.getId());
        assertEquals(story4.getId(), result4.getId());

        assertEquals(0, story4.getChildren().size());
        assertEquals(1, story2.getChildren().size());

        Story result3 = story2.getChildren().get(0);

        assertEquals(story3.getId(), result3.getId());
    }

    @Test
    @DirtiesContext
    public void testReplaceStoryNodesWithRoots_emptyList() {
        List<Story> stories = new ArrayList<Story>();
        List<Story> results = storyHierarchyBusiness
                .replaceStoryNodesWithRoots(stories);

        assertTrue(results.isEmpty());
    }

}
