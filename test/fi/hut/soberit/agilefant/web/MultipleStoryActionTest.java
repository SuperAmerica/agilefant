package fi.hut.soberit.agilefant.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fi.hut.soberit.agilefant.business.StoryBatchBusiness;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.MockedTestCase;
import fi.hut.soberit.agilefant.test.TestedBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class MultipleStoryActionTest  extends MockedTestCase {

    @TestedBean
    private MultipleStoryAction multipleStoryAction;
    @Mock
    private StoryBatchBusiness storyBatchBusiness;
    
    @Test
    @DirtiesContext
    public void testUpdateMultipleStories() {
        List<String> labels = new ArrayList<String>();
        Set<Integer> storyIds = new HashSet<Integer>(Arrays.asList(1,2));
        
        storyBatchBusiness.modifyMultiple(storyIds, StoryState.STARTED, labels);
        
        replayAll();
        multipleStoryAction.setLabelNames(labels);
        multipleStoryAction.setStoryIds(storyIds);
        multipleStoryAction.setState(StoryState.STARTED);
        
        multipleStoryAction.updateMultipleStories();
        verifyAll();
    }
}
