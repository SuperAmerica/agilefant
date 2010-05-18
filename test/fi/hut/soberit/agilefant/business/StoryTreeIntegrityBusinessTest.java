package fi.hut.soberit.agilefant.business;

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

import fi.hut.soberit.agilefant.business.impl.StoryTreeIntegrityBusinessImpl;
import fi.hut.soberit.agilefant.model.Backlog;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Product;
import fi.hut.soberit.agilefant.model.Project;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.util.StoryHierarchyIntegrityViolationType;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryTreeIntegrityBusinessTest extends MockedTestCase {

    @TestedBean
    private StoryTreeIntegrityBusinessImpl testable;

    /*
     * CHANGING BACKLOG
     */

    @Test
    @DirtiesContext
    public void testChangeBacklog_hasChildren_movingToIteration() {

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable.checkChangeBacklog(
                story_31, iteration);
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

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable.checkChangeBacklog(
                story_21, project2);
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

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable.checkChangeBacklog(
                story_41, product);
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

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable.checkChangeBacklog(
                story_33, project1);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(messages,
                StoryHierarchyIntegrityViolationType.PARENT_IN_WRONG_BRANCH,
                story_33, story_22);
    }

    /*
     * CHANGING PARENT STORY
     */

    @Test
    @DirtiesContext
    public void testChangeParent_moveUnderIterationStory() {
        // 32 -> 41

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable
                .checkChangeParentStory(story_32, story_41);
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
        List<StoryTreeIntegrityMessage> messages = testable
                .checkChangeParentStory(story_41, story_22);
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
        List<StoryTreeIntegrityMessage> messages = testable
                .checkChangeParentStory(story_23, story_22);
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
        List<StoryTreeIntegrityMessage> messages = testable
                .checkChangeParentStory(story_22, story_31);
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
     * Dataset as ASCII: Story 11 (Prod) |- Story 21 (Prod) | |- Story 31
     * (Proj1) | | |- Story 41 (Iter1) | |- Story 32 (Proj1) |- Story 22 (Proj2)
     * | |- Story 33 (Proj2) |- Story 23 (Prod)
     */

    Product product;
    Project project1;
    Project project2;
    Iteration iteration;
    Story story_11;
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
        story_21 = new Story();
        story_31 = new Story();
        story_32 = new Story();
        story_41 = new Story();
        story_22 = new Story();
        story_33 = new Story();
        story_23 = new Story();
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

    }

    private void setStoryBacklogs() {
        story_11.setBacklog(product);
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
}
