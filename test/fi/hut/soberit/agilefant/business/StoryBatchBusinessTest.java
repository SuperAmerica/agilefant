package fi.hut.soberit.agilefant.business;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.impl.StoryBatchBusinessImpl;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryBatchBusinessTest extends MockedTestCase{

    @TestedBean
    private StoryBatchBusinessImpl storyBatchBusiness;
    @Mock
    private LabelBusiness labelBusiness;
    @Mock
    private StoryBusiness storyBusiness;
    
    @Test
    @DirtiesContext
    public void testmodifyMultiple() {
        Story story1 = new Story();
        story1.setId(1);
        Story story2 = new Story();
        story2.setId(2);
        
        List<String> labels = new ArrayList<String>();
        labels.add("test");
        StoryState state = StoryState.IMPLEMENTED;
        
        expect(storyBusiness.retrieve(1)).andReturn(story1);
        expect(storyBusiness.retrieve(2)).andReturn(story2);
        
        labelBusiness.createStoryLabels(labels, 1);
        labelBusiness.createStoryLabels(labels, 2);
        
        replayAll();
        storyBatchBusiness.modifyMultiple(new HashSet<Integer>(Arrays.asList(1,2)), state, labels);
        verifyAll();
        assertEquals(state, story1.getState());
        assertEquals(state, story2.getState());
    }
    
    @Test
    @DirtiesContext
    public void testmodifyMultiple_noLabels() {
        Story story1 = new Story();
        story1.setId(1);
        Story story2 = new Story();
        story2.setId(2);
        
        StoryState state = StoryState.IMPLEMENTED;
        
        expect(storyBusiness.retrieve(1)).andReturn(story1);
        expect(storyBusiness.retrieve(2)).andReturn(story2);

        replayAll();
        storyBatchBusiness.modifyMultiple(new HashSet<Integer>(Arrays.asList(1,2)), state, null);
        verifyAll();
        assertEquals(state, story1.getState());
        assertEquals(state, story2.getState());
    }
    
    @Test
    @DirtiesContext
    public void testmodifyMultiple_noState() {
        Story story1 = new Story();
        story1.setId(1);
        Story story2 = new Story();
        story2.setId(2);
        
        List<String> labels = new ArrayList<String>();
        
        expect(storyBusiness.retrieve(1)).andReturn(story1);
        expect(storyBusiness.retrieve(2)).andReturn(story2);
        
        replayAll();
        storyBatchBusiness.modifyMultiple(new HashSet<Integer>(Arrays.asList(1,2)), null, labels);
        verifyAll();

        assertEquals(StoryState.NOT_STARTED, story1.getState());
        assertEquals(StoryState.NOT_STARTED, story2.getState());
    }
}
