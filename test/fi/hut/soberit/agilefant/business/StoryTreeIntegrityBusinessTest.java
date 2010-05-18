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
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryTreeIntegrityBusinessTest extends MockedTestCase {

    @TestedBean
    private StoryTreeIntegrityBusinessImpl testable;
    

    
//    @Test
//    @DirtiesContext
//    public void testChangeBacklog_happyCase() {
//        
//        replayAll();
//        Collection<StoryTreeIntegrityMessage> messages = testable
//                .checkChangeBacklog(null, null);
//        verifyAll();
//        
//        assertEquals(0, messages.size());
//    }
    
    @Test
    @DirtiesContext
    public void testChangeBacklog_hasChildren_movingToIteration() {

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable
                .checkChangeBacklog(story_inProject1, iteration);
        verifyAll();
        
        assertNumberOfMessages(messages, 1);
        assertMessagesContain(messages, "story.constraint.moveToIterationHasChildren", story_inProject1);
    }
    
    @Test
    @DirtiesContext
    public void testChangeBacklog_hasChildren_notUnderTargetBacklog() {

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable.checkChangeBacklog(
                story_inProduct, project2);
        verifyAll();
        
        assertNumberOfMessages(messages, 2);
        assertMessagesContain(messages, "story.constraint.childInWrongBranch", story_inProduct);
        assertMessagesContain(messages, "story.constraint.childInWrongBranch", story_inProduct);
    }
    
    @Test
    @DirtiesContext
    public void testChangeBacklog_moveToProduct_parentInProject() {
        

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable.checkChangeBacklog(
                story_inIteration, product);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(messages, "story.constraint.parentDeeperInHierarchy", story_inIteration);
    }
    
    @Test
    @DirtiesContext
    public void testChangeBacklog_moveToDifferentBranch() {

        replayAll();
        List<StoryTreeIntegrityMessage> messages = testable.checkChangeBacklog(
                story_leaf_inProject2, project1);
        verifyAll();

        assertNumberOfMessages(messages, 1);
        assertMessagesContain(messages, "story.constraint.parentInWrongBranch", story_leaf_inProject2);
    }

    

    
    /**
     * Helper method for checking messages' content.
     */
    private void assertMessagesContain(List<StoryTreeIntegrityMessage> messages,
            String message, Story source) {     
        
        // TODO: Check source
        for (StoryTreeIntegrityMessage msg : messages) {
            if (msg.getMessage().equals(message)) {
                return;
            }
        }
        fail("Message not found");
    }
    
    private void assertNumberOfMessages(List<StoryTreeIntegrityMessage> messages, int num) {
        assertEquals("Incorrect number of messages", num, messages.size());
    }
    
    
    /*
     * DATA CONSTRUCTION 
     */
    
    Product     product;
    Project     project1;
    Project     project2;
    Iteration   iteration;
    Story       story_root;
    Story       story_inProduct;
    Story       story_inProject1;
    Story       story_inIteration;
    Story       story_inProject2;
    Story       story_leaf_inProject2;

    
    @Before
    public void setUp_data() {
        constructBacklogs();
        constructStories();
        setStoryBacklogs();
        setStoryRelations();
    }

    private void constructStories() {
        story_root = new Story();
        story_inProduct = new Story();
        story_inProject1 = new Story();
        story_inIteration = new Story();
        story_inProject2 = new Story();
        story_leaf_inProject2 = new Story();
    }

    private void setStoryRelations() {
        story_root.setChildren(new ArrayList<Story>(Arrays.asList(story_inProduct, story_inProject2)));
        story_inProduct.setParent(story_root);
        story_inProject2.setParent(story_root);
        
        story_inProduct.setChildren(new ArrayList<Story>(Arrays.asList(story_inProject1)));
        story_inProject1.setParent(story_inProduct);
        
        story_inProject1.setChildren(new ArrayList<Story>(Arrays.asList(story_inIteration)));
        story_inIteration.setParent(story_inProject1);
        
        story_inProject2.setChildren(new ArrayList<Story>(Arrays.asList(story_leaf_inProject2)));
        story_leaf_inProject2.setParent(story_inProject2);
    }

    private void setStoryBacklogs() {
        story_root.setBacklog(product);
        story_inProduct.setBacklog(product);
        story_inProject1.setBacklog(project1);
        story_inIteration.setBacklog(iteration);
        story_inProject2.setBacklog(project2);
        story_leaf_inProject2.setBacklog(project2);
    }

    private void constructBacklogs() {
        product = new Product();
        project1 = new Project();
        project2 = new Project();
        iteration = new Iteration();
        
        product.setChildren(new HashSet<Backlog>(Arrays.asList(project1, project2)));
        
        project1.setParent(product);
        project1.setChildren(new HashSet<Backlog>(Arrays.asList(iteration)));
        iteration.setParent(project1);
        
        project2.setParent(product);
    }
}
