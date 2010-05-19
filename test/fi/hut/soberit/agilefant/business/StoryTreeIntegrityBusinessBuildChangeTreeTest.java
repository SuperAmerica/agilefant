package fi.hut.soberit.agilefant.business;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.StoryTreeIntegrityBusinessImpl;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.transfer.MoveStoryNode;
import fi.hut.soberit.agilefant.util.StoryTreeIntegrityMessage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryTreeIntegrityBusinessBuildChangeTreeTest extends MockedTestCase {

    @TestedBean
    private StoryTreeIntegrityBusinessImpl testable;
    
    @Test
    @DirtiesContext
    public void testGenerateChangedStoryTree_secondLevelChildren() {
        Story root = new Story();
        Story child1 = new Story();
        Story child2 = new Story();
        Story child11 = new Story();
        Story child12 = new Story();
        
        root.setChildren(Arrays.asList(child1, child2));
        
        child1.setChildren(Arrays.asList(child11, child12));
        
        List<StoryTreeIntegrityMessage> messages = new ArrayList<StoryTreeIntegrityMessage>();
        messages.add(new StoryTreeIntegrityMessage(root, child11, null));
        messages.add(new StoryTreeIntegrityMessage(root, child12, null));
        
        replayAll();
        MoveStoryNode storyNode = this.testable.generateChangedStoryTree(root, messages);
        verifyAll();
        
        MoveStoryNode node1 = findStoryNode(child1, storyNode.getChildren());
        
        assertNotNull("node 1 constructed", node1);
        
        MoveStoryNode node11 = findStoryNode(child11, node1.getChildren());
        MoveStoryNode node12 = findStoryNode(child12, node1.getChildren());
        
        assertNotNull("node 11 constructed", node11);
        assertNotNull("node 12 constructed", node12);
        
        assertTrue("root changed", storyNode.isContainsChanges());
        assertTrue("node 1 changed", node1.isContainsChanges());
        assertTrue("node 11 changed", node11.isContainsChanges());
        assertTrue("node 12 changed", node12.isContainsChanges());
        
        assertTrue("node 11 changed", node11.isChanged());
        assertTrue("node 12 changed", node12.isChanged());
    }
    @Test
    @DirtiesContext
    public void testGenerateChangedStoryTree_parentChanged() {
        Story level1 = new Story();
        Story level2 = new Story();
        Story level3 = new Story();
        
        level3.setParent(level2);
        level2.setParent(level1);
        
        List<StoryTreeIntegrityMessage> messages = new ArrayList<StoryTreeIntegrityMessage>();
        messages.add(new StoryTreeIntegrityMessage(level2, null, null));
        
        
        replayAll();
        MoveStoryNode storyNode = this.testable.generateChangedStoryTree(level3, messages);
        verifyAll();
        
        assertEquals("invalid root", level2, storyNode.getStory());
        assertTrue("root not changed", storyNode.isContainsChanges());
        assertEquals("root does not have one child", 1, storyNode.getChildren().size());
        assertEquals("invalid child", level3, storyNode.getChildren().get(0).getStory());
        assertEquals(0, storyNode.getChildren().get(0).getChildren().size());
        
    }
    private MoveStoryNode findStoryNode(Story story, List<MoveStoryNode> nodes) {
        for(MoveStoryNode node: nodes) {
            if(node.getStory() == story) {
                return node;
            }
        }
        return null;
    }
}
