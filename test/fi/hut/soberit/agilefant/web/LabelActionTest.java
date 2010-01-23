package fi.hut.soberit.agilefant.web;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.opensymphony.xwork2.Action;

import fi.hut.soberit.agilefant.business.BacklogBusiness;
import fi.hut.soberit.agilefant.business.LabelBusiness;
import fi.hut.soberit.agilefant.business.StoryBusiness;
import fi.hut.soberit.agilefant.model.Iteration;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class LabelActionTest extends MockedTestCase {

    @TestedBean
    LabelAction labelAction;
    
    @Mock
    StoryBusiness storyBusiness;
    
    @Mock
    LabelBusiness labelBusiness;
    
    @Mock
    BacklogBusiness backlogBusiness;
    
    List<String> labelNames;
    Integer storyId;
    
    Story story;
    Iteration iter;
    

    @Before
    public void setUp() {
        labelNames = new ArrayList<String>();
        story = new Story();
        story.setId(1);
        iter = new Iteration();
        iter.setId(6446);
    }
    
    @Test
    @DirtiesContext
    public void testAddLabels() {
        Integer storyId = 1;
        labelNames.add("Kissa");
        labelNames.add("Koira");
        labelBusiness.createStoryLabels(labelNames, storyId);
        labelAction.setLabelNames(labelNames);
        labelAction.setStoryId(storyId);
        replayAll();
        assertEquals(Action.SUCCESS, labelAction.addStoryLabels());
        verifyAll();
    }
}
