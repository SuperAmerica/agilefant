package fi.hut.soberit.agilefant.business.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.impl.StoryTreeIntegrityBusinessImpl;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.util.StoryHierarchyIntegrityViolationType;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;

import static org.junit.Assert.*;

import static org.easymock.EasyMock.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryTreeIntegrityBusinessTest extends MockedTestCase {

    @TestedBean
    private StoryTreeIntegrityBusinessImpl testable;

    @Mock
    private BacklogBusiness backlogBusiness;

    List<StoryTreeIntegrityMessage> messages;

    /*
     * CHANGING BACKLOG
     */

    @Test
    @DirtiesContext
    public void testChangeBacklog_hasChildren_movingToIteration() {
        expect(backlogBusiness.getParentProduct(story_31.getBacklog()))
                .andReturn(product);
        expect(backlogBusiness.getParentProduct(iteration)).andReturn(product);

        replayAll();
        messages = testable.checkChangeBacklog(story_31, iteration);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(
                messages,
                StoryHierarchyIntegrityViolationType.MOVE_TO_ITERATION_HAS_CHILDREN,
                story_31, null);
    }

    @Test
    @DirtiesContext
    public void testChangeBacklog_hasChildren_notUnderTargetBacklog() {
        expect(backlogBusiness.getParentProduct(story_21.getBacklog()))
                .andReturn(product);
        expect(backlogBusiness.getParentProduct(project2)).andReturn(product);

        replayAll();
        messages = testable.checkChangeBacklog(story_21, project2);
        verifyAll();

        assertNumberOfMessages(messages, 3);
        assertMessagesContain(messages,
                StoryHierarchyIntegrityViolationType.CHILD_IN_WRONG_BRANCH,
                story_21, story_31);
        assertMessagesContain(messages,
                StoryHierarchyIntegrityViolationType.CHILD_IN_WRONG_BRANCH,
                story_31, story_41);
        assertMessagesContain(messages,
                StoryHierarchyIntegrityViolationType.CHILD_IN_WRONG_BRANCH,
                story_21, story_32);
    }

    @Test
    @DirtiesContext
    public void testChangeBacklog_moveToProduct_parentInProject() {
        expect(backlogBusiness.getParentProduct(story_41.getBacklog()))
                .andReturn(product);
        expect(backlogBusiness.getParentProduct(product)).andReturn(product);

        replayAll();
        messages = testable.checkChangeBacklog(story_41, product);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(
                messages,
                StoryHierarchyIntegrityViolationType.PARENT_DEEPER_IN_HIERARCHY,
                story_41, story_31);
    }

    @Test
    @DirtiesContext
    public void testChangeBacklog_moveToDifferentBranch() {
        expect(backlogBusiness.getParentProduct(story_33.getBacklog()))
                .andReturn(product);
        expect(backlogBusiness.getParentProduct(project1)).andReturn(product);

        replayAll();
        messages = testable.checkChangeBacklog(story_33, project1);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(messages,
                StoryHierarchyIntegrityViolationType.PARENT_IN_WRONG_BRANCH,
                story_33, story_22);
    }

    @Test
    @DirtiesContext
    public void testChangeBacklog_moveToAnotherProduct_noConflict() {
        replayAll();
        messages = testable.checkChangeBacklog(story_12, new Product());
        verifyAll();
        assertNumberOfMessages(messages, 0);
    }

    @Test
    @DirtiesContext
    public void testChangeBacklog_moveToAnotherProduct_withConflict() {
        Product another = new Product();
        
        expect(backlogBusiness.getParentProduct(story_23.getBacklog())).andReturn(product);
        expect(backlogBusiness.getParentProduct(another)).andReturn(another);
        
        replayAll();
        messages = testable.checkChangeBacklog(story_23, another);
        verifyAll();
        assertNumberOfMessages(messages, 1);
    }

    /*
     * CHANGING PARENT STORY
     */

    @Test
    @DirtiesContext
    public void testChangeParent_moveUnderIterationStory() {
        // 32 -> 41

        replayAll();
        messages = testable.checkChangeParentStory(story_32, story_41);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(
                messages,
                StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_ITERATION,
                story_32, story_41);
    }

    @Test
    @DirtiesContext
    public void testChangeParent_moveToDifferentBranch() {
        // 41 -> 22

        replayAll();
        messages = testable.checkChangeParentStory(story_41, story_22);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(
                messages,
                StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_WRONG_BRANCH,
                story_41, story_22);
    }

    @Test
    @DirtiesContext
    public void testChangeParent_moveProductStoryUnderProjectStory() {
        // 23 -> 22
        // 34 vaihtaa branchia

        replayAll();
        messages = testable.checkChangeParentStory(story_23, story_22);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(
                messages,
                StoryHierarchyIntegrityViolationType.TARGET_PARENT_DEEPER_IN_HIERARCHY,
                story_23, story_22);
    }

    @Test
    @DirtiesContext
    public void testChangeParent_moveBranchUnderAnother() {
        // 22 -> 31
        // 2 virhettä, 22->31, 33 -> 31

        replayAll();
        messages = testable.checkChangeParentStory(story_22, story_31);
        verifyAll();

        assertNumberOfMessages(messages, 2);
        assertMessagesContain(
                messages,
                StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_WRONG_BRANCH,
                story_22, story_31);
        assertMessagesContain(
                messages,
                StoryHierarchyIntegrityViolationType.TARGET_PARENT_IN_WRONG_BRANCH,
                story_22, story_33);
    }

    /*
     * HELPER METHODS
     */

    /**
     * Helper method for checking messages' content.
     */
    private void assertMessagesContain(
            List<StoryTreeIntegrityMessage> messages,
            StoryHierarchyIntegrityViolationType message, Story source,
            Story target) {

        for (StoryTreeIntegrityMessage msg : messages) {
            if (msg.getMessage() == message && msg.getSource() == source
                    && msg.getTarget() == target) {
                return;
            }
        }
        fail("Message not found: " + message);
    }

    private void assertNumberOfMessages(
            List<StoryTreeIntegrityMessage> messages, int num) {
        assertEquals("Incorrect number of messages", num, messages.size());
    }

    /*
     * DATA CONSTRUCTION
     */
    /*
     * Dataset as ASCII:
     * Story 11 (Prod)
     * |- Story 21 (Prod)
     * | |- Story 31 (Proj1)
     * | | |- Story 41 (Iter1)
     * | |- Story 32 (Proj1)
     * |- Story 22 (Proj2)
     * | |- Story 33 (Proj2)
     * |- Story 23 (Prod)
     * Story 12 (Prod)
     */

    Product product;
    Project project1;
    Project project2;
    Iteration iteration;
    Story story_11;
    Story story_12;
    Story story_21;
    Story story_31;
    Story story_32;
    Story story_41;
    Story story_22;
    Story story_33;
    Story story_23;

    @Before
    public void setUp_data() {
        constructBacklogs();
        constructStories();
        setStoryBacklogs();
        setStoryRelations();
    }

    private void constructStories() {
        story_11 = new Story();
        story_12 = new Story();
        story_21 = new Story();
        story_31 = new Story();
        story_32 = new Story();
        story_41 = new Story();
        story_22 = new Story();
        story_33 = new Story();
        story_23 = new Story();

        story_11.setName("Story 11");
        story_12.setName("Story 12");
        story_21.setName("Story 21");
        story_31.setName("Story 31");
        story_32.setName("Story 32");
        story_41.setName("Story 41");
        story_22.setName("Story 22");
        story_33.setName("Story 33");
        story_23.setName("Story 23");
    }

    private void setStoryRelations() {
        story_11.setChildren(new ArrayList<Story>(Arrays.asList(story_21,
                story_22)));
        story_21.setParent(story_11);
        story_22.setParent(story_11);

        story_21.setChildren(new ArrayList<Story>(Arrays.asList(story_31,
                story_32)));
        story_31.setParent(story_21);
        story_32.setParent(story_21);

        story_31.setChildren(new ArrayList<Story>(Arrays.asList(story_41)));
        story_41.setParent(story_31);

        story_22.setChildren(new ArrayList<Story>(Arrays.asList(story_33)));
        story_33.setParent(story_22);

        story_23.setParent(story_11);
    }

    private void setStoryBacklogs() {
        story_11.setBacklog(product);
        story_12.setBacklog(product);
        story_21.setBacklog(product);
        story_31.setBacklog(project1);
        story_41.setBacklog(iteration);
        story_22.setBacklog(project2);
        story_33.setBacklog(project2);
        story_23.setBacklog(product);
    }

    private void constructBacklogs() {
        product = new Product();
        project1 = new Project();
        project2 = new Project();
        iteration = new Iteration();

        product.setChildren(new HashSet<Backlog>(Arrays.asList(project1,
                project2)));

        project1.setParent(product);
        project1.setChildren(new HashSet<Backlog>(Arrays.asList(iteration)));
        iteration.setParent(project1);

        project2.setParent(product);
    }
    
    @Test
    @DirtiesContext
    public void testHasParentStoryConflict_toIteration() {
        Product product = new Product();
        Project project = new Project();
        Iteration iteration = new Iteration();
        
        Story parentStory = new Story();
        Story story = new Story();
        
        iteration.setParent(project);
        project.setParent(product);
        
        parentStory.setBacklog(project);
        story.setBacklog(project);
        story.setParent(parentStory);
        
        replayAll();
        assertFalse(this.testable.hasParentStoryConflict(story, iteration));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testHasParentStoryConflict_toOtherProject() {
        Product product = new Product();
        Project project = new Project();
        Project targetProject = new Project();
        
        Story parentStory = new Story();
        Story story = new Story();
        
        targetProject.setParent(product);
        project.setParent(product);
        
        parentStory.setBacklog(project);
        story.setBacklog(project);
        story.setParent(parentStory);
        
        expect(this.backlogBusiness.getParentProduct(targetProject)).andReturn(product);
        expect(this.backlogBusiness.getParentProduct(project)).andReturn(product);
        
        replayAll();
        assertTrue(this.testable.hasParentStoryConflict(story, targetProject));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testHasParentStoryConflict_differentProduct() {
        Product product = new Product();
        Product targetProduct = new Product();
        Project project = new Project();
        Project targetProject = new Project();
        
        Story parentStory = new Story();
        Story story = new Story();
        
        targetProject.setParent(targetProduct);
        project.setParent(product);
        
        parentStory.setBacklog(project);
        story.setBacklog(project);
        story.setParent(parentStory);
        
        expect(this.backlogBusiness.getParentProduct(targetProject)).andReturn(targetProduct);
        expect(this.backlogBusiness.getParentProduct(project)).andReturn(product);
        
        replayAll();
        assertTrue(this.testable.hasParentStoryConflict(story, targetProject));
        verifyAll();
    }
    
    // TODO add replay & verify wrapper
    /*
    @Test
    public void checkParentDifferentProjectRule_whenTargetIsStandalone() {
        Story parentStory = new Story();
        Story childStory = new Story();
        parentStory.getChildren().add(childStory);
        
        Iteration standAloneIteration = new Iteration();
        standAloneIteration.setParent(null);
        
        List<StoryTreeIntegrityMessage> messages = new ArrayList<StoryTreeIntegrityMessage>();
        
        
        StoryTreeIntegrityBusinessImpl.checkParentDifferentProjectRule(childStory, standAloneIteration, messages);
        
        assertTrue(messages.size() == 0);
        
    }
    */
    
    
}
