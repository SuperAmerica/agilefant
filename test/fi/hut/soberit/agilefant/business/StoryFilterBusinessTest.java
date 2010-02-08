package fi.hut.soberit.agilefant.business;

import java.util.HashSet;
import java.util.Set;

import fi.hut.soberit.agilefant.business.impl.PortfolioBusinessImpl;
import fi.hut.soberit.agilefant.business.impl.StoryFilterBusinessImpl;
import fi.hut.soberit.agilefant.db.ProductDAO;
import fi.hut.soberit.agilefant.db.ProjectDAO;
import fi.hut.soberit.agilefant.db.StoryDAO;
import fi.hut.soberit.agilefant.model.Label;
import fi.hut.soberit.agilefant.model.Story;
import fi.hut.soberit.agilefant.model.StoryState;
import fi.hut.soberit.agilefant.test.Mock;
import fi.hut.soberit.agilefant.test.MockContextLoader;
import fi.hut.soberit.agilefant.test.TestedBean;
import fi.hut.soberit.agilefant.util.StoryFilters;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = MockContextLoader.class)
public class StoryFilterBusinessTest {
    
    @TestedBean
    private StoryFilterBusinessImpl storyFilterBusiness;
    
    @Mock
    private SettingBusiness settingBusiness;
    @Mock
    private StoryDAO storyDAO;
    
    protected void verifyAll() {
        verify(storyDAO, settingBusiness);
    }

    protected void replayAll() {
        replay(storyDAO, settingBusiness);
    }
    
    @Test
    public void filterByStates() {
        Story story1 = new Story();
        Story story2 = new Story();
        
        story1.setState(StoryState.IMPLEMENTED);
        story2.setState(StoryState.DONE);
        
        HashSet<Story> stories = new HashSet<Story>();
        stories.add(story1);
        stories.add(story2);
        Set<StoryState> statesToKeep = new HashSet<StoryState>();
        
        //stateFilters.setImplemented(false);
        //StoryFilterBusiness.filterByStates(stories, statesToKeep);
    }
    
    @Test
    @DirtiesContext
    public void testFilterByState() {
        Story story1 = new Story();
        story1.setState(StoryState.IMPLEMENTED);
        Set<String> labels = new HashSet<String>();
        Set<StoryState> states = StoryState.valueSet;
        StoryFilters storyFilters = new StoryFilters("diibadaaba", labels, states);
        
        replayAll();
        assertTrue(storyFilterBusiness.filterByState(story1, storyFilters));
        verifyAll();
    }
    
    @Test
    @DirtiesContext
    public void testFilterByState_FilteredOut() {
        Story story = new Story();
        story.setState(StoryState.DONE);
        Set<String> labels = new HashSet<String>();
        Set<StoryState> states = new HashSet<StoryState>();
        states.add(StoryState.BLOCKED);
        StoryFilters storyFilters = new StoryFilters("diibadaaba", labels, states);
        
        replayAll();
        assertFalse(storyFilterBusiness.filterByState(story, storyFilters));
        verifyAll();
        
    }
    
    @Test
    @DirtiesContext
    public void testFilterByName() {
        Story story = new Story();
        story.setName("dIIbadAAba");
        StoryFilters storyFilters = new StoryFilters("DiiBaDaabA", null, null);
        
        assertTrue(storyFilterBusiness.filterByName(story, storyFilters));
        
    }
    
    @Test
    @DirtiesContext
    public void testFilterByName_notFound() {
        Story story = new Story();
        story.setName("dIIdadAAba");
        StoryFilters storyFilters = new StoryFilters("DiiBaDaabA", null, null);
        
        assertFalse(storyFilterBusiness.filterByName(story, storyFilters));
        
    }
    
    @Test
    @DirtiesContext
    public void testFilterByName_null() {
        Story story = new Story();
        story.setName("dIIbadAAba");
        StoryFilters storyFilters = new StoryFilters(null,null,null);
        
        assertTrue(storyFilterBusiness.filterByName(story, storyFilters));
    }
    
    @Test
    @DirtiesContext
    public void testFilterByName_length0() {
        Story story = new Story();
        story.setName("");
        StoryFilters storyFilters = new StoryFilters(null,null,null);
        
        assertTrue(storyFilterBusiness.filterByName(story, storyFilters));
    }
    
    @Test
    @DirtiesContext
    public void testFilterByLabels() {
        Story story = new Story();
        story.setState(StoryState.DONE);
        story.setName("dIIbadAAba");
        Set<Label> labels2 = new HashSet<Label>();
        Label great = new Label();
        great.setName("Great");
        great.setDisplayName("Great");
        labels2.add(great);
        story.setLabels(labels2);
        
        Set<String> labels = new HashSet<String>();
        labels.add("great");
        labels.add("tree");
        Set<StoryState> states = new HashSet<StoryState>();
        StoryFilters storyFilters = new StoryFilters("DiiBaDaabA", labels, states);
        
        replayAll();
        assertTrue(storyFilterBusiness.filterByLabels(story, storyFilters));
        verifyAll();
        
    }
    
    @Test
    @DirtiesContext
    public void testFilterByLabels_emptyLabels() {
 
    }
    
  
    
    
    
}
